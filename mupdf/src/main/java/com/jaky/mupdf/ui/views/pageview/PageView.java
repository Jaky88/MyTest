package com.jaky.mupdf.ui.views.pageview;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.jaky.mupdf.data.Annotation;
import com.jaky.mupdf.task.AsyncTask;
import com.jaky.mupdf.task.CancellableAsyncTask;
import com.jaky.mupdf.task.CancellableTaskDefinition;
import com.jaky.mupdf.data.LinkInfo;
import com.jaky.mupdf.R;
import com.jaky.mupdf.data.TextWord;
import com.jaky.mupdf.async.TextProcessor;
import com.jaky.mupdf.async.TextSelector;
import com.jaky.mupdf.ui.views.imageview.OpaqueImageView;

public abstract class PageView extends ViewGroup {
    private static final int HIGHLIGHT_COLOR = 0x802572AC;
    private static final int LINK_COLOR = 0x80AC7225;
    private static final int BOX_COLOR = 0xFF4444FF;
    private static final int INK_COLOR = 0xFFFF0000;
    private static final float INK_THICKNESS = 10.0f;
    private static final int BACKGROUND_COLOR = 0xFFFFFFFF;
    private static final int PROGRESS_DIALOG_DELAY = 200;
    protected final Context mContext;

    private ImageView mIvEntirePicture;
    private ImageView mIvPatchPicture;
    private ProgressBar mLoadingBar;
    private View mSearchView;

    private Bitmap mEntireBmp;
    private Bitmap mPatchBmp;
    private Matrix mEntireMatrix;
    protected LinkInfo mLinks[];
    private TextWord mText[][];
    private AsyncTask<Void, Void, TextWord[][]> mGetTextTask;
    private AsyncTask<Void, Void, LinkInfo[]> mGetLinkInfoTask;
    private CancellableAsyncTask<Void, Void> mDrawEntireTask;
    private CancellableAsyncTask<Void, Void> mDrawPatchTask;

    private Rect mPatchArea;
    private RectF mSearchArea[];
    private RectF mSelectArea;
    private RectF mItemSelectArea;

    private Point mParentSize;
    protected Point mPageSize;
    private Point mPatchViewSize;
    protected ArrayList<ArrayList<PointF>> mDrawingSizeList;

    private boolean isEmpty;
    private boolean mHighlightLinks;
    protected int mPageNumber;
    protected float mSrcScale;

    private final Handler mHandler = new Handler();

    public PageView(Context c, Point parentSize, Bitmap sharedHqBmp) {
        super(c);
        mContext = c;
        mParentSize = parentSize;
        setBackgroundColor(BACKGROUND_COLOR);
        mEntireBmp = Bitmap.createBitmap(parentSize.x, parentSize.y, Config.ARGB_8888);
        mPatchBmp = sharedHqBmp;
        mEntireMatrix = new Matrix();
    }

    protected abstract CancellableTaskDefinition<Void, Void> getDrawPageTask(Bitmap bm, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);

    protected abstract CancellableTaskDefinition<Void, Void> getUpdatePageTask(Bitmap bm, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);

    protected abstract LinkInfo[] getLinkInfo();

    protected abstract TextWord[][] getText();

    protected abstract void addMarkup(PointF[] quadPoints, @Annotation.Type int type);

    public void releaseResources() {
        reinit();
        if (mLoadingBar != null) {
            removeView(mLoadingBar);
            mLoadingBar = null;
        }
    }

    public void releaseBitmaps() {
        reinit();
        if (mEntireBmp != null) {
            mEntireBmp.recycle();
        }
        mEntireBmp = null;

        if (mPatchBmp != null) {
            mPatchBmp.recycle();
        }
        mPatchBmp = null;
    }

    public void setPage(int page, PointF size) {
        if (mDrawEntireTask != null) {
            mDrawEntireTask.cancelAndWait();
            mDrawEntireTask = null;
        }

        isEmpty = false;
        if (mSearchView != null) {
            mSearchView.invalidate();
        }
        mPageNumber = page;
        if (mIvEntirePicture == null) {
            mIvEntirePicture = new OpaqueImageView(mContext);
            mIvEntirePicture.setScaleType(ImageView.ScaleType.MATRIX);
            addView(mIvEntirePicture);
        }

        mSrcScale = Math.min(mParentSize.x / size.x, mParentSize.y / size.y);
        Point pageSize = new Point((int) (size.x * mSrcScale), (int) (size.y * mSrcScale));
        mPageSize = pageSize;

        mIvEntirePicture.setImageBitmap(null);
        mIvEntirePicture.invalidate();

        //获取链接信息
        mGetLinkInfoTask = new AsyncTask<Void, Void, LinkInfo[]>() {
            protected LinkInfo[] doInBackground(Void... v) {
                return getLinkInfo();
            }

            protected void onPostExecute(LinkInfo[] v) {
                mLinks = v;
                if (mSearchView != null) {
                    mSearchView.invalidate();
                }
            }
        };
        mGetLinkInfoTask.execute();

        //绘制整页
        mDrawEntireTask = new CancellableAsyncTask<Void, Void>(
                getDrawPageTask(mEntireBmp, mPageSize.x, mPageSize.y, 0, 0, mPageSize.x, mPageSize.y)) {

            @Override
            public void onPreExecute() {
                setBackgroundColor(BACKGROUND_COLOR);
                mIvEntirePicture.setImageBitmap(null);
                mIvEntirePicture.invalidate();

                if (mLoadingBar == null) {
                    mLoadingBar = new ProgressBar(mContext);
                    mLoadingBar.setIndeterminate(true);
                    mLoadingBar.setBackgroundResource(R.drawable.loading);
                    addView(mLoadingBar);
                    mLoadingBar.setVisibility(INVISIBLE);
                    mHandler.postDelayed(new Runnable() {
                        public void run() {
                            if (mLoadingBar != null)
                                mLoadingBar.setVisibility(VISIBLE);
                        }
                    }, PROGRESS_DIALOG_DELAY);
                }
            }

            @Override
            public void onPostExecute(Void result) {
                removeView(mLoadingBar);
                mLoadingBar = null;
                mIvEntirePicture.setImageBitmap(mEntireBmp);
                mIvEntirePicture.invalidate();
                setBackgroundColor(Color.TRANSPARENT);

            }
        };
        mDrawEntireTask.execute();

        if (mSearchView == null) {
            mSearchView = new View(mContext) {
                @Override
                protected void onDraw(final Canvas canvas) {
                    super.onDraw(canvas);
                    final float scale = mSrcScale * (float) getWidth() / (float) mPageSize.x;
                    final Paint paint = new Paint();

                    if (!isEmpty && mSearchArea != null) {
                        paint.setColor(HIGHLIGHT_COLOR);
                        for (RectF rect : mSearchArea) {
                            canvas.drawRect(rect.left * scale, rect.top * scale, rect.right * scale, rect.bottom * scale, paint);
                        }
                    }

                    if (!isEmpty && mLinks != null && mHighlightLinks) {
                        paint.setColor(LINK_COLOR);
                        for (LinkInfo link : mLinks) {
                            canvas.drawRect(link.rect.left * scale, link.rect.top * scale, link.rect.right * scale, link.rect.bottom * scale, paint);
                        }
                    }

                    if (mSelectArea != null && mText != null) {
                        paint.setColor(HIGHLIGHT_COLOR);
                        processSelectedText(new TextProcessor() {
                            RectF rect;

                            public void onStartLine() {
                                rect = new RectF();
                            }

                            public void onWord(TextWord word) {
                                rect.union(word);
                            }

                            public void onEndLine() {
                                if (!rect.isEmpty()) {
                                    canvas.drawRect(rect.left * scale, rect.top * scale, rect.right * scale, rect.bottom * scale, paint);
                                }
                            }
                        });
                    }

                    if (mItemSelectArea != null) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setColor(BOX_COLOR);
                        canvas.drawRect(mItemSelectArea.left * scale, mItemSelectArea.top * scale, mItemSelectArea.right * scale, mItemSelectArea.bottom * scale, paint);
                    }

                    if (mDrawingSizeList != null) {
                        Path path = new Path();
                        PointF p;

                        paint.setAntiAlias(true);
                        paint.setDither(true);
                        paint.setStrokeJoin(Paint.Join.ROUND);
                        paint.setStrokeCap(Paint.Cap.ROUND);

                        paint.setStyle(Paint.Style.FILL);
                        paint.setStrokeWidth(INK_THICKNESS * scale);
                        paint.setColor(INK_COLOR);

                        Iterator<ArrayList<PointF>> it = mDrawingSizeList.iterator();
                        while (it.hasNext()) {
                            ArrayList<PointF> arc = it.next();
                            if (arc.size() >= 2) {
                                Iterator<PointF> iit = arc.iterator();
                                p = iit.next();
                                float mX = p.x * scale;
                                float mY = p.y * scale;
                                path.moveTo(mX, mY);
                                while (iit.hasNext()) {
                                    p = iit.next();
                                    float x = p.x * scale;
                                    float y = p.y * scale;
                                    path.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
                                    mX = x;
                                    mY = y;
                                }
                                path.lineTo(mX, mY);
                            } else {
                                p = arc.get(0);
                                canvas.drawCircle(p.x * scale, p.y * scale, INK_THICKNESS * scale / 2, paint);
                            }
                        }

                        paint.setStyle(Paint.Style.STROKE);
                        canvas.drawPath(path, paint);
                    }
                }
            };

            addView(mSearchView);
        }
        requestLayout();
    }

    public void setSearchBoxes(RectF searchBoxes[]) {
        mSearchArea = searchBoxes;
        if (mSearchView != null) {
            mSearchView.invalidate();
        }
    }

    public void setLinkHighlighting(boolean f) {
        mHighlightLinks = f;
        if (mSearchView != null) {
            mSearchView.invalidate();
        }
    }

    public void deselectText() {
        mSelectArea = null;
        mSearchView.invalidate();
    }

    public void selectText(float x0, float y0, float x1, float y1) {
        float scale = mSrcScale * (float) getWidth() / (float) mPageSize.x;
        float docRelX0 = (x0 - getLeft()) / scale;
        float docRelY0 = (y0 - getTop()) / scale;
        float docRelX1 = (x1 - getLeft()) / scale;
        float docRelY1 = (y1 - getTop()) / scale;
        if (docRelY0 <= docRelY1) {
            mSelectArea = new RectF(docRelX0, docRelY0, docRelX1, docRelY1);
        } else {
            mSelectArea = new RectF(docRelX1, docRelY1, docRelX0, docRelY0);
        }
        mSearchView.invalidate();

        if (mGetTextTask == null) {
            mGetTextTask = new AsyncTask<Void, Void, TextWord[][]>() {
                @Override
                protected TextWord[][] doInBackground(Void... params) {
                    return getText();
                }

                @Override
                protected void onPostExecute(TextWord[][] result) {
                    mText = result;
                    mSearchView.invalidate();
                }
            };

            mGetTextTask.execute();
        }
    }

    public void startDraw(float x, float y) {
        float scale = mSrcScale * (float) getWidth() / (float) mPageSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;
        if (mDrawingSizeList == null) {
            mDrawingSizeList = new ArrayList<ArrayList<PointF>>();
        }

        ArrayList<PointF> arc = new ArrayList<PointF>();
        arc.add(new PointF(docRelX, docRelY));
        mDrawingSizeList.add(arc);
        mSearchView.invalidate();
    }

    public void continueDraw(float x, float y) {
        float scale = mSrcScale * (float) getWidth() / (float) mPageSize.x;
        float docRelX = (x - getLeft()) / scale;
        float docRelY = (y - getTop()) / scale;

        if (mDrawingSizeList != null && mDrawingSizeList.size() > 0) {
            ArrayList<PointF> arc = mDrawingSizeList.get(mDrawingSizeList.size() - 1);
            arc.add(new PointF(docRelX, docRelY));
            mSearchView.invalidate();
        }
    }

    public void cancelDraw() {
        mDrawingSizeList = null;
        mSearchView.invalidate();
    }

    protected PointF[][] getDraw() {
        if (mDrawingSizeList == null) {
            return null;
        }
        PointF[][] path = new PointF[mDrawingSizeList.size()][];

        for (int i = 0; i < mDrawingSizeList.size(); i++) {
            ArrayList<PointF> arc = mDrawingSizeList.get(i);
            path[i] = arc.toArray(new PointF[arc.size()]);
        }
        return path;
    }

    protected void processSelectedText(TextProcessor tp) {
        (new TextSelector(mText, mSelectArea)).select(tp);
    }

    public void setItemSelectBox(RectF rect) {
        mItemSelectArea = rect;
        if (mSearchView != null) {
            mSearchView.invalidate();
        }
    }

    //============================ViewGroup========================================

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int x, y;
        switch (MeasureSpec.getMode(widthMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                x = mPageSize.x;
                break;
            default:
                x = MeasureSpec.getSize(widthMeasureSpec);
        }
        switch (MeasureSpec.getMode(heightMeasureSpec)) {
            case MeasureSpec.UNSPECIFIED:
                y = mPageSize.y;
                break;
            default:
                y = MeasureSpec.getSize(heightMeasureSpec);
        }

        setMeasuredDimension(x, y);

        if (mLoadingBar != null) {
            int limit = Math.min(mParentSize.x, mParentSize.y) / 2;
            mLoadingBar.measure(MeasureSpec.AT_MOST | limit, MeasureSpec.AT_MOST | limit);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int w = right - left;
        int h = bottom - top;

        if (mIvEntirePicture != null) {
            if (mIvEntirePicture.getWidth() != w || mIvEntirePicture.getHeight() != h) {
                mEntireMatrix.setScale(w / (float) mPageSize.x, h / (float) mPageSize.y);
                mIvEntirePicture.setImageMatrix(mEntireMatrix);
                mIvEntirePicture.invalidate();
            }
            mIvEntirePicture.layout(0, 0, w, h);
        }

        if (mSearchView != null) {
            mSearchView.layout(0, 0, w, h);
        }

        if (mPatchViewSize != null) {
            if (mPatchViewSize.x != w || mPatchViewSize.y != h) {
                mPatchViewSize = null;
                mPatchArea = null;
                if (mIvPatchPicture != null) {
                    mIvPatchPicture.setImageBitmap(null);
                    mIvPatchPicture.invalidate();
                }
            } else {
                mIvPatchPicture.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
            }
        }

        if (mLoadingBar != null) {
            int bw = mLoadingBar.getMeasuredWidth();
            int bh = mLoadingBar.getMeasuredHeight();

            mLoadingBar.layout((w - bw) / 2, (h - bh) / 2, (w + bw) / 2, (h + bh) / 2);
        }
    }

    //====================================================================

    private void reinit() {
        if (mDrawEntireTask != null) {
            mDrawEntireTask.cancelAndWait();
            mDrawEntireTask = null;
        }

        if (mDrawPatchTask != null) {
            mDrawPatchTask.cancelAndWait();
            mDrawPatchTask = null;
        }

        if (mGetLinkInfoTask != null) {
            mGetLinkInfoTask.cancel(true);
            mGetLinkInfoTask = null;
        }

        if (mGetTextTask != null) {
            mGetTextTask.cancel(true);
            mGetTextTask = null;
        }

        isEmpty = true;
        mPageNumber = 0;

        if (mPageSize == null) {
            mPageSize = mParentSize;
        }

        if (mIvEntirePicture != null) {
            mIvEntirePicture.setImageBitmap(null);
            mIvEntirePicture.invalidate();
        }

        if (mIvPatchPicture != null) {
            mIvPatchPicture.setImageBitmap(null);
            mIvPatchPicture.invalidate();
        }

        mPatchViewSize = null;
        mPatchArea = null;

        mSearchArea = null;
        mLinks = null;
        mSelectArea = null;
        mText = null;
        mItemSelectArea = null;
    }

    public void blank(int page) {
        reinit();
        mPageNumber = page;

        if (mLoadingBar == null) {
            mLoadingBar = new ProgressBar(mContext);
            mLoadingBar.setIndeterminate(true);
            mLoadingBar.setBackgroundResource(R.drawable.loading);
            addView(mLoadingBar);
        }

        setBackgroundColor(BACKGROUND_COLOR);
    }

    public void update() {
        if (mDrawEntireTask != null) {
            mDrawEntireTask.cancelAndWait();
            mDrawEntireTask = null;
        }

        if (mDrawPatchTask != null) {
            mDrawPatchTask.cancelAndWait();
            mDrawPatchTask = null;
        }

        //绘制整页
        mDrawEntireTask = new CancellableAsyncTask<Void, Void>(
                getUpdatePageTask(mEntireBmp, mPageSize.x, mPageSize.y, 0, 0, mPageSize.x, mPageSize.y)) {

            public void onPostExecute(Void result) {
                mIvEntirePicture.setImageBitmap(mEntireBmp);
                mIvEntirePicture.invalidate();
            }
        };
        mDrawEntireTask.execute();

        updateHq(true);
    }

    public void updateHq(boolean update) {
        Rect viewArea = new Rect(getLeft(), getTop(), getRight(), getBottom());
        if (viewArea.width() == mPageSize.x || viewArea.height() == mPageSize.y) {
            if (mIvPatchPicture != null) {
                mIvPatchPicture.setImageBitmap(null);
                mIvPatchPicture.invalidate();
            }
        } else {
            final Point patchViewSize = new Point(viewArea.width(), viewArea.height());
            final Rect patchArea = new Rect(0, 0, mParentSize.x, mParentSize.y);

            if (!patchArea.intersect(viewArea)) {
                return;
            }

            patchArea.offset(-viewArea.left, -viewArea.top);

            boolean area_unchanged = patchArea.equals(mPatchArea) && patchViewSize.equals(mPatchViewSize);

            if (area_unchanged && !update) {
                return;
            }

            boolean completeRedraw = !(area_unchanged && update);

            if (mDrawPatchTask != null) {
                mDrawPatchTask.cancelAndWait();
                mDrawPatchTask = null;
            }

            if (mIvPatchPicture == null) {
                mIvPatchPicture = new OpaqueImageView(mContext);
                mIvPatchPicture.setScaleType(ImageView.ScaleType.MATRIX);
                addView(mIvPatchPicture);
                mSearchView.bringToFront();
            }

            CancellableTaskDefinition<Void, Void> task;

            if (completeRedraw) {
                task = getDrawPageTask(mPatchBmp, patchViewSize.x, patchViewSize.y,
                        patchArea.left, patchArea.top,
                        patchArea.width(), patchArea.height());
            } else {
                task = getUpdatePageTask(mPatchBmp, patchViewSize.x, patchViewSize.y,
                        patchArea.left, patchArea.top,
                        patchArea.width(), patchArea.height());
            }

            mDrawPatchTask = new CancellableAsyncTask<Void, Void>(task) {

                public void onPostExecute(Void result) {
                    mPatchViewSize = patchViewSize;
                    mPatchArea = patchArea;
                    mIvPatchPicture.setImageBitmap(mPatchBmp);
                    mIvPatchPicture.invalidate();
                    mIvPatchPicture.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
                }
            };

            mDrawPatchTask.execute();
        }
    }

    public void removeHq() {
        if (mDrawPatchTask != null) {
            mDrawPatchTask.cancelAndWait();
            mDrawPatchTask = null;
        }

        mPatchViewSize = null;
        mPatchArea = null;
        if (mIvPatchPicture != null) {
            mIvPatchPicture.setImageBitmap(null);
            mIvPatchPicture.invalidate();
        }
    }

    public int getPage() {
        return mPageNumber;
    }

    @Override
    public boolean isOpaque() {
        return true;
    }
}

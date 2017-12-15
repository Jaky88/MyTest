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
import com.jaky.mupdf.task.CAsyncTask;
import com.jaky.mupdf.task.AsyncTaskImpl;
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
    private CAsyncTask<Void, Void> mDrawEntireTask;
    private CAsyncTask<Void, Void> mDrawPatchTask;

    private Rect mPatchArea;
    private RectF mSearchArea[];
    private RectF mSelectArea;
    private RectF mItemSelectArea;

    private Point mViewPortSize;
    protected Point mPageSize;
    private Point mPatchViewSize;
    protected ArrayList<ArrayList<PointF>> mDrawingSizeList;

    private boolean isEmpty;
    private boolean mHighlightLinks;
    protected int mPageNumber;
    protected float mSrcScale;

    private final Handler mHandler = new Handler();

    public PageView(Context c, Point parentSize, Bitmap emptyHqBmp) {
        super(c);
        mContext = c;
        mViewPortSize = parentSize;
        setBackgroundColor(BACKGROUND_COLOR);
        mEntireBmp = Bitmap.createBitmap(parentSize.x, parentSize.y, Config.ARGB_8888);
        mPatchBmp = emptyHqBmp;
        mEntireMatrix = new Matrix();
    }


    //抽象方法----子类完成
    protected abstract AsyncTaskImpl<Void, Void> doDrawPage(Bitmap bm, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);
    protected abstract AsyncTaskImpl<Void, Void> doUpdatePage(Bitmap bm, int sizeX, int sizeY, int patchX, int patchY, int patchWidth, int patchHeight);
    protected abstract LinkInfo[] getLinkInfo();
    protected abstract TextWord[][] getText();
    protected abstract void addMarkup(PointF[] quadPoints, @Annotation.Type int type);

    public int getPage() {
        return mPageNumber;
    }

    public void setPage(int pageNum, PointF pageSize) {
        cancelDrawEntireTask();
        isEmpty = false;
        updateSearchView();
        mPageNumber = pageNum;
        addEntirePicture();
        adaptPageSize(pageSize);
        showEmptyEntirePicture();

        initGetLinkInfoTask();
        mGetLinkInfoTask.execute();
        initDrawEntirePictureTask();
        mDrawEntireTask.execute();

        addSearchView();
        requestLayout();
    }

    //更新页面
    public void update() {
        cancelDrawEntireTask();
        cancelDrawPatchTask();
        initUpdateEntirePictureTask();
        mDrawEntireTask.execute();
        updateHq(true);
    }

    public void blank(int page) {
        reinit();
        mPageNumber = page;
        showLoadingBar();
        setBackgroundColor(BACKGROUND_COLOR);
    }

    public void updateHq(boolean update) {
        Rect viewArea = new Rect(getLeft(), getTop(), getRight(), getBottom());
        if (viewArea.width() == mPageSize.x || viewArea.height() == mPageSize.y) {
            showEmptyPatchPicture();
        } else {
            final Point patchViewSize = new Point(viewArea.width(), viewArea.height());
            final Rect patchArea = new Rect(0, 0, mViewPortSize.x, mViewPortSize.y);

            if (!patchArea.intersect(viewArea)) {
                return;
            }

            patchArea.offset(-viewArea.left, -viewArea.top);

            boolean area_unchanged = patchArea.equals(mPatchArea) && patchViewSize.equals(mPatchViewSize);

            if (area_unchanged && !update) {
                return;
            }

            boolean completeRedraw = !(area_unchanged && update);

            cancelDrawPatchTask();
            showPatchPicture();
            initDrawPatchPicture(patchViewSize, patchArea, completeRedraw);

            mDrawPatchTask.execute();
        }
    }

    public void removeHq() {
        cancelDrawPatchTask();

        mPatchViewSize = null;
        mPatchArea = null;
        showEmptyPatchPicture();
    }

    //==================init task==============================

    private void initGetLinkInfoTask() {
        mGetLinkInfoTask = new AsyncTask<Void, Void, LinkInfo[]>() {
            protected LinkInfo[] doInBackground(Void... v) {
                return getLinkInfo();
            }

            protected void onPostExecute(LinkInfo[] v) {
                mLinks = v;
                updateSearchView();
            }
        };
    }

    private void initDrawEntirePictureTask() {
        mDrawEntireTask = new CAsyncTask<Void, Void>(
                doDrawPage(mEntireBmp, mPageSize.x, mPageSize.y, 0, 0, mPageSize.x, mPageSize.y)) {
            @Override
            public void onPreExecute() {
                setBackgroundColor(BACKGROUND_COLOR);
                showEmptyEntirePicture();
                showLoadingBar();
                mLoadingBar.setVisibility(INVISIBLE);
                mHandler.postDelayed(new Runnable() {
                    public void run() {
                        if (mLoadingBar != null)
                            mLoadingBar.setVisibility(VISIBLE);
                    }
                }, PROGRESS_DIALOG_DELAY);
            }

            @Override
            public void onPostExecute(Void result) {
                hideLoadingBar();
                showEntirePicture();
                setBackgroundColor(Color.TRANSPARENT);
            }
        };
    }

    private void initUpdateEntirePictureTask() {
        mDrawEntireTask = new CAsyncTask<Void, Void>(
                doUpdatePage(mEntireBmp, mPageSize.x, mPageSize.y, 0, 0, mPageSize.x, mPageSize.y)) {

            public void onPostExecute(Void result) {
                showEntirePicture();
            }
        };
    }

    private void initDrawPatchPicture(final Point patchViewSize, final Rect patchArea, boolean completeRedraw) {
        AsyncTaskImpl<Void, Void> taskDrawPage;

        if (completeRedraw) {
            taskDrawPage = doDrawPage(mPatchBmp,
                    patchViewSize.x, patchViewSize.y,
                    patchArea.left, patchArea.top,
                    patchArea.width(), patchArea.height());
        } else {
            taskDrawPage = doUpdatePage(mPatchBmp,
                    patchViewSize.x, patchViewSize.y,
                    patchArea.left, patchArea.top,
                    patchArea.width(), patchArea.height());
        }

        mDrawPatchTask = new CAsyncTask<Void, Void>(taskDrawPage) {

            public void onPostExecute(Void result) {
                mPatchViewSize = patchViewSize;
                mPatchArea = patchArea;
                mIvPatchPicture.setImageBitmap(mPatchBmp);
                mIvPatchPicture.invalidate();
                mIvPatchPicture.layout(mPatchArea.left, mPatchArea.top, mPatchArea.right, mPatchArea.bottom);
            }
        };
    }

    private void initGetTextTask() {
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
    }

    //==================cancel task==============================

    private void cancelDrawPatchTask() {
        if (mDrawPatchTask != null) {
            mDrawPatchTask.cancelAndWait();
            mDrawPatchTask = null;
        }
    }

    private void cancelDrawEntireTask() {
        if (mDrawEntireTask != null) {
            mDrawEntireTask.cancelAndWait();
            mDrawEntireTask = null;
        }
    }

    private void cancelGetTextTask() {
        if (mGetTextTask != null) {
            mGetTextTask.cancel(true);
            mGetTextTask = null;
        }
    }

    private void cancelGetLinkInfoTask() {
        if (mGetLinkInfoTask != null) {
            mGetLinkInfoTask.cancel(true);
            mGetLinkInfoTask = null;
        }
    }

    public void releaseResources() {
        reinit();
        hideLoadingBar();
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

    private void reinit() {
        cancelDrawEntireTask();
        cancelDrawPatchTask();
        cancelGetLinkInfoTask();
        cancelGetTextTask();
        showEmptyEntirePicture();
        showEmptyPatchPicture();
        isEmpty = true;
        mPageNumber = 0;

        if (mPageSize == null) {
            mPageSize = mViewPortSize;
        }
        mPatchViewSize = null;
        mPatchArea = null;
        mSearchArea = null;
        mLinks = null;
        mSelectArea = null;
        mText = null;
        mItemSelectArea = null;
    }

    //=========================ui=======================

    private void addEntirePicture() {
        if (mIvEntirePicture == null) {
            mIvEntirePicture = new OpaqueImageView(mContext);
            mIvEntirePicture.setBackgroundColor(Color.WHITE);
            mIvEntirePicture.setScaleType(ImageView.ScaleType.MATRIX);
            addView(mIvEntirePicture);
        }
    }

    private void showEmptyEntirePicture() {
        if (mIvEntirePicture != null) {
            mIvEntirePicture.setImageBitmap(null);
            mIvEntirePicture.invalidate();
        }
    }

    private void showEmptyPatchPicture() {
        if (mIvPatchPicture != null) {
            mIvPatchPicture.setImageBitmap(null);
            mIvPatchPicture.invalidate();
        }
    }

    private void showEntirePicture() {
        mIvEntirePicture.setImageBitmap(mEntireBmp);
        mIvEntirePicture.invalidate();
    }

    private void showPatchPicture() {
        if (mIvPatchPicture == null) {
            mIvPatchPicture = new OpaqueImageView(mContext);
            mIvPatchPicture.setBackgroundColor(Color.WHITE);
            mIvPatchPicture.setScaleType(ImageView.ScaleType.FIT_XY);
            addView(mIvPatchPicture);
            mSearchView.bringToFront();
        }
    }

    private void hideLoadingBar() {
        if (mLoadingBar != null) {
            removeView(mLoadingBar);
            mLoadingBar = null;
        }
    }

    private void showLoadingBar() {
        if (mLoadingBar == null) {
            mLoadingBar = new ProgressBar(mContext);
            mLoadingBar.setIndeterminate(true);
            mLoadingBar.setBackgroundResource(R.drawable.loading);
            addView(mLoadingBar);
        }
    }

    private void updateSearchView() {
        if (mSearchView != null) {
            mSearchView.invalidate();
        }
    }

    private void addSearchView() {
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
    }

    private void adaptPageSize(PointF pageSize) {
        mSrcScale = Math.min(mViewPortSize.x / pageSize.x, mViewPortSize.y / pageSize.y);
        Point newSize = new Point((int) (pageSize.x * mSrcScale), (int) (pageSize.y * mSrcScale));
        mPageSize = newSize;
    }

    public void setSearchBoxes(RectF searchBoxes[]) {
        mSearchArea = searchBoxes;
        updateSearchView();
    }

    public void setLinkHighlighting(boolean f) {
        mHighlightLinks = f;
        updateSearchView();
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
            initGetTextTask();
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
        updateSearchView();
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
            int limit = Math.min(mViewPortSize.x, mViewPortSize.y) / 2;
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
                showEmptyPatchPicture();
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

    @Override
    public boolean isOpaque() {
        return true;
    }

}

package com.jaky.mupdf.ui.views.pageview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jaky.mupdf.async.TextProcessor;
import com.jaky.mupdf.data.LinkInfo;
import com.jaky.mupdf.data.TextWord;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by jaky on 2017/12/8 0008.
 */

public class SearchView extends View{
    public SearchView(Context context) {
        this(context, null);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(final Canvas canvas) {
        /*super.onDraw(canvas);
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
        }*/
    }

}

package com.jaky.mupdf.ui.views.adapterview;

import java.util.LinkedList;
import java.util.NoSuchElementException;

import android.app.AlertDialog;
import android.content.DialogInterface;

import android.content.Context;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Scroller;

import com.jaky.mupdf.R;
import com.jaky.mupdf.data.Stepper;
import com.jaky.mupdf.async.ViewMapper;
import com.jaky.mupdf.ui.activity.ReaderActivity;
import com.jaky.mupdf.ui.adapter.MuPDFPageAdapter;

public class ReaderView extends AdapterView<Adapter> implements
		GestureDetector.OnGestureListener,
		ScaleGestureDetector.OnScaleGestureListener,
		Runnable,
		ReaderViewItemAction {
		private static final int  MOVING_DIAGONALLY = 0;
		private static final int  MOVING_LEFT       = 1;
		private static final int  MOVING_RIGHT      = 2;
		private static final int  MOVING_UP         = 3;
		private static final int  MOVING_DOWN       = 4;

		private static final int  FLING_MARGIN      = 100;
		private static final int  GAP               = 20;

		private static final float MIN_SCALE        = 1.0f;
		private static final float MAX_SCALE        = 5.0f;
		private static final float REFLOW_SCALE_FACTOR = 0.5f;

		private static final boolean HORIZONTAL_SCROLLING = true;

		private Adapter           mAdapter;
		private int               mCurrent;
		private boolean           mResetLayout;
		private final SparseArray<View> mChildViews = new SparseArray<View>(3);
		private final LinkedList<View>
					  mViewCache = new LinkedList<View>();
		private boolean           mUserInteracting;
		private boolean           mScaling;
		private float             mScale     = 1.0f;
		private int               mXScroll;
		private int               mYScroll;
		private boolean           mReflow = false;
		private boolean           mReflowChanged = false;
		private final GestureDetector
					  mGestureDetector;
		private final ScaleGestureDetector
					  mScaleGestureDetector;
		private final Scroller    mScroller;
		private final Stepper mStepper;
		private int               mScrollerLastX;
		private int               mScrollerLastY;
		private float		  mLastScaleFocusX;
		private float		  mLastScaleFocusY;



	public ReaderView(Context context) {
		super(context);
		mGestureDetector = new GestureDetector(this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller        = new Scroller(context);
		mStepper = new Stepper(this, this);
	}

	public ReaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		if (isInEditMode()) {
			mGestureDetector = null;
			mScaleGestureDetector = null;
			mScroller = null;
			mStepper = null;
		} else {
			mGestureDetector = new GestureDetector(this);
			mScaleGestureDetector = new ScaleGestureDetector(context, this);
			mScroller        = new Scroller(context);
			mStepper = new Stepper(this, this);
		}
	}

	public ReaderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mGestureDetector = new GestureDetector(this);
		mScaleGestureDetector = new ScaleGestureDetector(context, this);
		mScroller        = new Scroller(context);
		mStepper = new Stepper(this, this);
	}

	public int getDisplayedViewIndex() {
		return mCurrent;
	}

	public void setDisplayedViewIndex(int i) {
		if (0 <= i && i < mAdapter.getCount()) {
			onMoveOffChild(mCurrent);
			mCurrent = i;
			onMoveToChild(i);
			mResetLayout = true;
			requestLayout();
		}
	}

	public void moveToNext() {
		View v = mChildViews.get(mCurrent+1);
		if (v != null)
			slideViewOntoScreen(v);
	}

	public void moveToPrevious() {
		View v = mChildViews.get(mCurrent-1);
		if (v != null)
			slideViewOntoScreen(v);
	}

	private int smartAdvanceAmount(int screenHeight, int max) {
		int advance = (int)(screenHeight * 0.9 + 0.5);
		int leftOver = max % advance;
		int steps = max / advance;
		if (leftOver == 0) {
		} else if ((float)leftOver / steps <= screenHeight * 0.05) {
			advance += (int)((float)leftOver/steps + 0.5);
		} else {
			int overshoot = advance - leftOver;
			if ((float)overshoot / steps <= screenHeight * 0.1) {
				advance -= (int)((float)overshoot/steps + 0.5);
			}
		}
		if (advance > max)
			advance = max;
		return advance;
	}

	public void smartMoveForwards() {
		View v = mChildViews.get(mCurrent);
		if (v == null)
			return;

		int screenWidth  = getWidth();
		int screenHeight = getHeight();

		int remainingX = mScroller.getFinalX() - mScroller.getCurrX();
		int remainingY = mScroller.getFinalY() - mScroller.getCurrY();
		// right/bottom is in terms of pixels within the scaled document; e.g. 1000
		int top = -(v.getTop()  + mYScroll + remainingY);
		int right  = screenWidth -(v.getLeft() + mXScroll + remainingX);
		int bottom = screenHeight+top;
		int docWidth  = v.getMeasuredWidth();
		int docHeight = v.getMeasuredHeight();

		int xOffset, yOffset;
		if (bottom >= docHeight) {
			if (right + screenWidth > docWidth) {
				View nv = mChildViews.get(mCurrent+1);
				if (nv == null)
					return;
				int nextTop  = -(nv.getTop() + mYScroll + remainingY);
				int nextLeft = -(nv.getLeft() + mXScroll + remainingX);
				int nextDocWidth = nv.getMeasuredWidth();
				int nextDocHeight = nv.getMeasuredHeight();

				yOffset = (nextDocHeight < screenHeight ? ((nextDocHeight - screenHeight)>>1) : 0);

				if (nextDocWidth < screenWidth) {
					xOffset = (nextDocWidth - screenWidth)>>1;
				} else {
					xOffset = right % screenWidth;
					if (xOffset + screenWidth > nextDocWidth)
						xOffset = nextDocWidth - screenWidth;
				}
				xOffset -= nextLeft;
				yOffset -= nextTop;
			} else {
				xOffset = screenWidth;
				yOffset = screenHeight - bottom;
			}
		} else {
			xOffset = 0;
			yOffset = smartAdvanceAmount(screenHeight, docHeight - bottom);
		}
		mScrollerLastX = mScrollerLastY = 0;
		mScroller.startScroll(0, 0, remainingX - xOffset, remainingY - yOffset, 400);
		mStepper.prod();
	}

	public void smartMoveBackwards() {
		View v = mChildViews.get(mCurrent);
		if (v == null) {
			return;
		}

		int screenWidth  = getWidth();
		int screenHeight = getHeight();
		int remainingX = mScroller.getFinalX() - mScroller.getCurrX();
		int remainingY = mScroller.getFinalY() - mScroller.getCurrY();
		int left  = -(v.getLeft() + mXScroll + remainingX);
		int top   = -(v.getTop()  + mYScroll + remainingY);
		int docHeight = v.getMeasuredHeight();

		int xOffset, yOffset;
		if (top <= 0) {
			if (left < screenWidth) {
				View pv = mChildViews.get(mCurrent-1);
				if (pv == null){
					return;
				}
				int prevDocWidth = pv.getMeasuredWidth();
				int prevDocHeight = pv.getMeasuredHeight();

				yOffset = (prevDocHeight < screenHeight ? ((prevDocHeight - screenHeight)>>1) : 0);

				int prevLeft  = -(pv.getLeft() + mXScroll);
				int prevTop  = -(pv.getTop() + mYScroll);
				if (prevDocWidth < screenWidth) {
					xOffset = (prevDocWidth - screenWidth)>>1;
				} else {
					xOffset = (left > 0 ? left % screenWidth : 0);
					if (xOffset + screenWidth > prevDocWidth)
						xOffset = prevDocWidth - screenWidth;
					while (xOffset + screenWidth*2 < prevDocWidth)
						xOffset += screenWidth;
				}
				xOffset -= prevLeft;
				yOffset -= prevTop-prevDocHeight+screenHeight;
			} else {
				xOffset = -screenWidth;
				yOffset = docHeight - screenHeight + top;
			}
		} else {
			xOffset = 0;
			yOffset = -smartAdvanceAmount(screenHeight, top);
		}
		mScrollerLastX = mScrollerLastY = 0;
		mScroller.startScroll(0, 0, remainingX - xOffset, remainingY - yOffset, 400);
		mStepper.prod();
	}

	public void resetupChildren() {
		for (int i = 0; i < mChildViews.size(); i++)
			onChildSetup(mChildViews.keyAt(i), mChildViews.valueAt(i));
	}

	public void applyToChildren(ViewMapper mapper) {
		for (int i = 0; i < mChildViews.size(); i++)
			mapper.applyToView(mChildViews.valueAt(i));
	}

	public void refresh(boolean reflow) {
		mReflow = reflow;
		mReflowChanged = true;
		mResetLayout = true;

		mScale = 1.0f;
		mXScroll = mYScroll = 0;

		requestLayout();
	}

	//======================interface=============================

	@Override
	public void onChildSetup(int i, View v) {}

	@Override
	public void onMoveToChild(int i) {}

	@Override
	public void onMoveOffChild(int i) {}

	@Override
	public void onSettle(View v) {};

	@Override
	public void onUnsettle(View v) {};

	@Override
	public void onNotInUse(View v) {};

	@Override
	public void onScaleChild(View v, Float scale) {};

	//======================run=============================
	public void run() {
		if (!mScroller.isFinished()) {
			mScroller.computeScrollOffset();
			int x = mScroller.getCurrX();
			int y = mScroller.getCurrY();
			mXScroll += x - mScrollerLastX;
			mYScroll += y - mScrollerLastY;
			mScrollerLastX = x;
			mScrollerLastY = y;
			requestLayout();
			mStepper.prod();
		}
		else if (!mUserInteracting) {
			View v = mChildViews.get(mCurrent);
			if (v != null)
				postSettle(v);
		}
	}

	//======================OnGestureListener=============================
	public boolean onDown(MotionEvent arg0) {
		mScroller.forceFinished(true);
		return true;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (mScaling)
			return true;

		View v = mChildViews.get(mCurrent);
		if (v != null) {
			Rect bounds = getScrollBounds(v);
			switch(directionOfTravel(velocityX, velocityY)) {
			case MOVING_LEFT:
				if (HORIZONTAL_SCROLLING && bounds.left >= 0) {
					View vl = mChildViews.get(mCurrent+1);

					if (vl != null) {
						slideViewOntoScreen(vl);
						return true;
					}
				}
				break;
			case MOVING_UP:
				if (!HORIZONTAL_SCROLLING && bounds.top >= 0) {
					View vl = mChildViews.get(mCurrent+1);

					if (vl != null) {
						slideViewOntoScreen(vl);
						return true;
					}
				}
				break;
			case MOVING_RIGHT:
				if (HORIZONTAL_SCROLLING && bounds.right <= 0) {
					View vr = mChildViews.get(mCurrent-1);

					if (vr != null) {
						slideViewOntoScreen(vr);
						return true;
					}
				}
				break;
			case MOVING_DOWN:
				if (!HORIZONTAL_SCROLLING && bounds.bottom <= 0) {
					View vr = mChildViews.get(mCurrent-1);

					if (vr != null) {
						slideViewOntoScreen(vr);
						return true;
					}
				}
				break;
			}
			mScrollerLastX = mScrollerLastY = 0;
			Rect expandedBounds = new Rect(bounds);
			expandedBounds.inset(-FLING_MARGIN, -FLING_MARGIN);

			if(withinBoundsInDirectionOfTravel(bounds, velocityX, velocityY)
					&& expandedBounds.contains(0, 0)) {
				mScroller.fling(0, 0, (int)velocityX, (int)velocityY, bounds.left, bounds.right, bounds.top, bounds.bottom);
				mStepper.prod();
			}
		}

		return true;
	}

	public void onLongPress(MotionEvent e) {
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		if (!mScaling) {
			mXScroll -= distanceX;
			mYScroll -= distanceY;
			requestLayout();
		}
		return true;
	}

	public void onShowPress(MotionEvent e) {
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}


	//======================OnScaleGestureListener=============================
	public boolean onScale(ScaleGestureDetector detector) {
		float previousScale = mScale;
		float scale_factor = mReflow ? REFLOW_SCALE_FACTOR : 1.0f;
		float min_scale = MIN_SCALE * scale_factor;
		float max_scale = MAX_SCALE * scale_factor;
		mScale = Math.min(Math.max(mScale * detector.getScaleFactor(), min_scale), max_scale);

		if (mReflow) {
			View v = mChildViews.get(mCurrent);
			if (v != null)
				onScaleChild(v, mScale);
		} else {
			float factor = mScale/previousScale;

			View v = mChildViews.get(mCurrent);
			if (v != null) {
				float currentFocusX = detector.getFocusX();
				float currentFocusY = detector.getFocusY();
				// Work out the focus point relative to the view top left
				int viewFocusX = (int)currentFocusX - (v.getLeft() + mXScroll);
				int viewFocusY = (int)currentFocusY - (v.getTop() + mYScroll);
				// Scroll to maintain the focus point
				mXScroll += viewFocusX - viewFocusX * factor;
				mYScroll += viewFocusY - viewFocusY * factor;

				if (mLastScaleFocusX>=0)
					mXScroll+=currentFocusX-mLastScaleFocusX;
				if (mLastScaleFocusY>=0)
					mYScroll+=currentFocusY-mLastScaleFocusY;

				mLastScaleFocusX=currentFocusX;
				mLastScaleFocusY=currentFocusY;
				requestLayout();
			}
		}
		return true;
	}

	public boolean onScaleBegin(ScaleGestureDetector detector) {
		mScaling = true;
		mXScroll = mYScroll = 0;
		mLastScaleFocusX = mLastScaleFocusY = -1;
		return true;
	}

	public void onScaleEnd(ScaleGestureDetector detector) {
		if (mReflow) {
			applyToChildren(new ViewMapper() {
				@Override
				public void applyToView(View view) {
					onScaleChild(view, mScale);
				}
			});
		}
		mScaling = false;
	}

	//======================onTouchEvent=============================
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		mScaleGestureDetector.onTouchEvent(event);
		mGestureDetector.onTouchEvent(event);

		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_DOWN) {
			mUserInteracting = true;
		}
		if ((event.getAction() & MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
			mUserInteracting = false;

			View v = mChildViews.get(mCurrent);
			if (v != null) {
				if (mScroller.isFinished()) {
					slideViewOntoScreen(v);
				}

				if (mScroller.isFinished()) {
					postSettle(v);
				}
			}
		}

		requestLayout();
		return true;
	}



	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		int n = getChildCount();
		for (int i = 0; i < n; i++)
			measureView(getChildAt(i));
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		super.onLayout(changed, left, top, right, bottom);

		try {
			onLayout2(changed, left, top, right, bottom);
		}
		catch (OutOfMemoryError e) {
			System.out.println("Out of memory during layout");
			if (!memAlert) {
				memAlert = true;
				AlertDialog alertDialog = ReaderActivity.getAlertBuilder().create();
				alertDialog.setMessage("Out of memory during layout");
				alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
							memAlert = false;
						}
					});
				alertDialog.show();
			}
		}
	}

	private boolean memAlert = false;

	private void onLayout2(boolean changed, int left, int top, int right, int bottom) {
		if (isInEditMode()) {
			return;
		}

		View cv = mChildViews.get(mCurrent);
		Point cvOffset;

		if (!mResetLayout) {
			if (cv != null) {
				boolean move;
				cvOffset = subScreenSizeOffset(cv);
				if (HORIZONTAL_SCROLLING)
					move = cv.getLeft() + cv.getMeasuredWidth() + cvOffset.x + GAP/2 + mXScroll < getWidth()/2;
				else
					move = cv.getTop() + cv.getMeasuredHeight() + cvOffset.y + GAP/2 + mYScroll < getHeight()/2;
				if (move && mCurrent + 1 < mAdapter.getCount()) {
					postUnsettle(cv);
					mStepper.prod();

					onMoveOffChild(mCurrent);
					mCurrent++;
					onMoveToChild(mCurrent);
				}

				if (HORIZONTAL_SCROLLING)
					move = cv.getLeft() - cvOffset.x - GAP/2 + mXScroll >= getWidth()/2;
				else
					move = cv.getTop() - cvOffset.y - GAP/2 + mYScroll >= getHeight()/2;
				if (move && mCurrent > 0) {
					postUnsettle(cv);
					mStepper.prod();

					onMoveOffChild(mCurrent);
					mCurrent--;
					onMoveToChild(mCurrent);
				}
			}

			int numChildren = mChildViews.size();
			int childIndices[] = new int[numChildren];
			for (int i = 0; i < numChildren; i++)
				childIndices[i] = mChildViews.keyAt(i);

			for (int i = 0; i < numChildren; i++) {
				int ai = childIndices[i];
				if (ai < mCurrent - 1 || ai > mCurrent + 1) {
					View v = mChildViews.get(ai);
					onNotInUse(v);
					mViewCache.add(v);
					removeViewInLayout(v);
					mChildViews.remove(ai);
				}
			}
		} else {
			mResetLayout = false;
			mXScroll = mYScroll = 0;

			int numChildren = mChildViews.size();
			for (int i = 0; i < numChildren; i++) {
				View v = mChildViews.valueAt(i);
				onNotInUse(v);
				mViewCache.add(v);
				removeViewInLayout(v);
			}
			mChildViews.clear();

			if (mReflowChanged) {
				mReflowChanged = false;
				mViewCache.clear();
			}

			mStepper.prod();
		}

		int cvLeft, cvRight, cvTop, cvBottom;
		boolean notPresent = (mChildViews.get(mCurrent) == null);
		cv = getOrCreateChild(mCurrent);
		cvOffset = subScreenSizeOffset(cv);
		if (notPresent) {
			cvLeft = cvOffset.x;
			cvTop  = cvOffset.y;
		} else {
			cvLeft = cv.getLeft() + mXScroll;
			cvTop  = cv.getTop()  + mYScroll;
		}
		mXScroll = mYScroll = 0;
		cvRight  = cvLeft + cv.getMeasuredWidth();
		cvBottom = cvTop  + cv.getMeasuredHeight();

		if (!mUserInteracting && mScroller.isFinished()) {
			Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
			cvRight  += corr.x;
			cvLeft   += corr.x;
			cvTop    += corr.y;
			cvBottom += corr.y;
		} else if (HORIZONTAL_SCROLLING && cv.getMeasuredHeight() <= getHeight()) {
			Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
			cvTop    += corr.y;
			cvBottom += corr.y;
		} else if (!HORIZONTAL_SCROLLING && cv.getMeasuredWidth() <= getWidth()) {
			Point corr = getCorrection(getScrollBounds(cvLeft, cvTop, cvRight, cvBottom));
			cvRight  += corr.x;
			cvLeft   += corr.x;
		}

		cv.layout(cvLeft, cvTop, cvRight, cvBottom);

		if (mCurrent > 0) {
			View lv = getOrCreateChild(mCurrent - 1);
			Point leftOffset = subScreenSizeOffset(lv);
			if (HORIZONTAL_SCROLLING)
			{
				int gap = leftOffset.x + GAP + cvOffset.x;
				lv.layout(cvLeft - lv.getMeasuredWidth() - gap,
						(cvBottom + cvTop - lv.getMeasuredHeight())/2,
						cvLeft - gap,
						(cvBottom + cvTop + lv.getMeasuredHeight())/2);
			} else {
				int gap = leftOffset.y + GAP + cvOffset.y;
				lv.layout((cvLeft + cvRight - lv.getMeasuredWidth())/2,
						cvTop - lv.getMeasuredHeight() - gap,
						(cvLeft + cvRight + lv.getMeasuredWidth())/2,
						cvTop - gap);
			}
		}

		if (mCurrent + 1 < mAdapter.getCount()) {
			View rv = getOrCreateChild(mCurrent + 1);
			Point rightOffset = subScreenSizeOffset(rv);
			if (HORIZONTAL_SCROLLING)
			{
				int gap = cvOffset.x + GAP + rightOffset.x;
				rv.layout(cvRight + gap,
						(cvBottom + cvTop - rv.getMeasuredHeight())/2,
						cvRight + rv.getMeasuredWidth() + gap,
						(cvBottom + cvTop + rv.getMeasuredHeight())/2);
			} else {
				int gap = cvOffset.y + GAP + rightOffset.y;
				rv.layout((cvLeft + cvRight - rv.getMeasuredWidth())/2,
						cvBottom + gap,
						(cvLeft + cvRight + rv.getMeasuredWidth())/2,
						cvBottom + gap + rv.getMeasuredHeight());
			}
		}

		invalidate();
	}


	//======================AdapterView=============================

	public View getView(int i) {
		return mChildViews.get(i);
	}

	public View getDisplayedView() {
		return mChildViews.get(mCurrent);
	}

	@Override
	public Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public View getSelectedView() {
		return null;
	}

	@Override
	public void setAdapter(Adapter adapter) {
		if (null!=mAdapter && adapter!=mAdapter) {
			if (adapter instanceof MuPDFPageAdapter){
				((MuPDFPageAdapter) adapter).releaseBitmaps();
			}
		}

		mAdapter = adapter;

		requestLayout();
	}

	@Override
	public void setSelection(int arg0) {
		throw new UnsupportedOperationException(getContext().getString(R.string.not_supported));
	}

	private View getCached() {
		if (mViewCache.size() == 0)
			return null;
		else
			return mViewCache.removeFirst();
	}

	private View getOrCreateChild(int i) {
		View v = mChildViews.get(i);
		if (v == null) {
			v = mAdapter.getView(i, getCached(), this);
			addAndMeasureChild(i, v);
			onChildSetup(i, v);
			onScaleChild(v, mScale);
		}

		return v;
	}

	private void addAndMeasureChild(int i, View v) {
		LayoutParams params = v.getLayoutParams();
		if (params == null) {
			params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		}
		addViewInLayout(v, 0, params, true);
		mChildViews.append(i, v);
		measureView(v);
	}

	private void measureView(View v) {
		v.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);

		if (!mReflow) {
		float scale = Math.min((float)getWidth()/(float)v.getMeasuredWidth(),
					(float)getHeight()/(float)v.getMeasuredHeight());
		v.measure(MeasureSpec.EXACTLY | (int)(v.getMeasuredWidth()*scale*mScale),
				MeasureSpec.EXACTLY | (int)(v.getMeasuredHeight()*scale*mScale));
		} else {
			v.measure(MeasureSpec.EXACTLY | (int)(v.getMeasuredWidth()),
					MeasureSpec.EXACTLY | (int)(v.getMeasuredHeight()));
		}
	}

	private Rect getScrollBounds(int left, int top, int right, int bottom) {
		int xmin = getWidth() - right;
		int xmax = -left;
		int ymin = getHeight() - bottom;
		int ymax = -top;

		if (xmin > xmax) xmin = xmax = (xmin + xmax)/2;
		if (ymin > ymax) ymin = ymax = (ymin + ymax)/2;

		return new Rect(xmin, ymin, xmax, ymax);
	}

	private Rect getScrollBounds(View v) {
		return getScrollBounds(v.getLeft() + mXScroll,
				               v.getTop() + mYScroll,
				               v.getLeft() + v.getMeasuredWidth() + mXScroll,
				               v.getTop() + v.getMeasuredHeight() + mYScroll);
	}

	private Point getCorrection(Rect bounds) {
		return new Point(Math.min(Math.max(0,bounds.left),bounds.right),
				         Math.min(Math.max(0,bounds.top),bounds.bottom));
	}

	private void postSettle(final View v) {
		post (new Runnable() {
			public void run () {
				onSettle(v);
			}
		});
	}

	private void postUnsettle(final View v) {
		post (new Runnable() {
			public void run () {
				onUnsettle(v);
			}
		});
	}

	private void slideViewOntoScreen(View v) {
		Point corr = getCorrection(getScrollBounds(v));
		if (corr.x != 0 || corr.y != 0) {
			mScrollerLastX = mScrollerLastY = 0;
			mScroller.startScroll(0, 0, corr.x, corr.y, 400);
			mStepper.prod();
		}
	}

	private Point subScreenSizeOffset(View v) {
		return new Point(Math.max((getWidth() - v.getMeasuredWidth())/2, 0),
				Math.max((getHeight() - v.getMeasuredHeight())/2, 0));
	}

	private static int directionOfTravel(float vx, float vy) {
		if (Math.abs(vx) > 2 * Math.abs(vy))
			return (vx > 0) ? MOVING_RIGHT : MOVING_LEFT;
		else if (Math.abs(vy) > 2 * Math.abs(vx))
			return (vy > 0) ? MOVING_DOWN : MOVING_UP;
		else
			return MOVING_DIAGONALLY;
	}

	private static boolean withinBoundsInDirectionOfTravel(Rect bounds, float vx, float vy) {
		switch (directionOfTravel(vx, vy)) {
		case MOVING_DIAGONALLY: return bounds.contains(0, 0);
		case MOVING_LEFT:       return bounds.left <= 0;
		case MOVING_RIGHT:      return bounds.right >= 0;
		case MOVING_UP:         return bounds.top <= 0;
		case MOVING_DOWN:       return bounds.bottom >= 0;
		default: throw new NoSuchElementException();
		}
	}
}

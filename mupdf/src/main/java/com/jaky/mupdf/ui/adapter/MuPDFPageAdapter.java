package com.jaky.mupdf.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jaky.mupdf.task.AsyncTask;
import com.jaky.mupdf.data.FilePicker;
import com.jaky.mupdf.core.MuPDFCore;
import com.jaky.mupdf.ui.views.pageview.MuPDFPageView;

public class MuPDFPageAdapter extends BaseAdapter {
	private final Context mContext;
	private final FilePicker.FilePickerSupport mFilePickerSupport;
	private final MuPDFCore mCore;
	private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();
	private       Bitmap mSharedHqBm;

	public MuPDFPageAdapter(Context c, FilePicker.FilePickerSupport filePickerSupport, MuPDFCore core) {
		mContext = c;
		mFilePickerSupport = filePickerSupport;
		mCore = core;
	}

	public int getCount() {
		return mCore.countPages();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}

	public void releaseBitmaps()
	{
		//  recycle and release the shared bitmap.
		if (mSharedHqBm!=null)
			mSharedHqBm.recycle();
		mSharedHqBm = null;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final MuPDFPageView pageView;
		if (convertView == null) {
			if (mSharedHqBm == null || mSharedHqBm.getWidth() != parent.getWidth() || mSharedHqBm.getHeight() != parent.getHeight())
				mSharedHqBm = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), Bitmap.Config.ARGB_8888);

			pageView = new MuPDFPageView(mContext, mFilePickerSupport, mCore, new Point(parent.getWidth(), parent.getHeight()), mSharedHqBm);
		} else {
			pageView = (MuPDFPageView) convertView;
		}

		PointF pageSize = mPageSizes.get(position);
		if (pageSize != null) {
			pageView.setPage(position, pageSize);
		} else {
			pageView.blank(position);
			AsyncTask<Void,Void,PointF> sizingTask = new AsyncTask<Void,Void,PointF>() {
				@Override
				protected PointF doInBackground(Void... arg0) {
					return mCore.getPageSize(position);
				}

				@Override
				protected void onPostExecute(PointF result) {
					super.onPostExecute(result);
					mPageSizes.put(position, result);
					if (pageView.getPage() == position) {
						pageView.setPage(position, result);
					}
				}
			};

			sizingTask.execute((Void)null);
		}
		return pageView;
	}
}

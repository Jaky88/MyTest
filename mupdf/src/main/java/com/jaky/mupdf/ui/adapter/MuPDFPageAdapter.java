package com.jaky.mupdf.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jaky.mupdf.R;
import com.jaky.mupdf.task.AsyncTask;
import com.jaky.mupdf.data.FilePicker;
import com.jaky.mupdf.core.MuPDFCore;
import com.jaky.mupdf.ui.views.pageview.MuPDFPageView;

public class MuPDFPageAdapter extends BaseAdapter {
    private final Context mContext;
    private final FilePicker.FilePickerSupport mFilePickerSupport;
    private final MuPDFCore mCore;
    private final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();
    private Bitmap mEmptyHqBmp;
    private LayoutInflater mInflater;

    public MuPDFPageAdapter(Context c, FilePicker.FilePickerSupport filePickerSupport, MuPDFCore core) {
        this.mContext = c;
        this.mInflater = LayoutInflater.from(c);
        this.mFilePickerSupport = filePickerSupport;
        this.mCore = core;
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

    public void releaseBitmaps() {
        if (mEmptyHqBmp != null)
            mEmptyHqBmp.recycle();
        mEmptyHqBmp = null;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final MuPDFPageView pageView;
        if (convertView == null) {
            if (mEmptyHqBmp == null || mEmptyHqBmp.getWidth() != parent.getWidth() || mEmptyHqBmp.getHeight() != parent.getHeight()) {
                //创建空图
                Log.d("========","=====================parent.getWidth()====="+parent.getWidth() +"=====parent.getHeight()===="+ parent.getHeight());
                mEmptyHqBmp = Bitmap.createBitmap(parent.getWidth(), parent.getHeight(), Bitmap.Config.ARGB_8888);
            }

//            pageView = (MuPDFPageView) mInflater.inflate(R.layout.item_pdf_page, null);

            //绘制页面
            pageView = new MuPDFPageView(mContext, mFilePickerSupport, mCore, new Point(parent.getWidth(), parent.getHeight()), mEmptyHqBmp);
        } else {
            pageView = (MuPDFPageView) convertView;
        }

        PointF pageSize = mPageSizes.get(position);
        if (pageSize != null) {
            Log.d("========","=====================pageSize.x===="+pageSize.x +"=====pageSize.y==="+ pageSize.y);
            pageView.setPage(position, pageSize);
        } else {
            pageView.blank(position);
            AsyncTask<Void, Void, PointF> sizingTask = new AsyncTask<Void, Void, PointF>() {
                @Override
                protected PointF doInBackground(Void... arg0) {
                    return mCore.getPageSize(position);
                }

                @Override
                protected void onPostExecute(PointF result) {
                    super.onPostExecute(result);
                    Log.d("========","=====================result.x===="+result.x +"=====result.y==="+ result.y);
                    mPageSizes.put(position, result);
                    if (pageView.getPage() == position) {
                        pageView.setPage(position, result);
                    }
                }
            };

            sizingTask.execute((Void) null);
        }
        return pageView;
    }
}

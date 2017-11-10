package com.onyx.test.mytest.binding;

import android.content.Context;
import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.view.View;

import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.manager.ConfigManager;

import java.io.File;

/**
 * Created by jaky on 2017/11/4 0004.
 */

public class FragmentTab01Model extends BaseObservable {

    private Context context;
    private ReaderSlideshowBean readerSlideshowBean;

    public FragmentTab01Model(Context context) {
        this.context = context;
        this.readerSlideshowBean = ConfigManager.getConfig(context).getReaderSlideshowBean();
    }

    @Bindable
    public ReaderSlideshowBean getReaderSlideshowBean() {
        return readerSlideshowBean;
    }

    public void setReaderSlideshowBean(ReaderSlideshowBean readerSlideshowBean) {
        this.readerSlideshowBean = readerSlideshowBean;
    }

    public void onSettingsClick(View view) {
        setReaderSlideshowBean(readerSlideshowBean);
        ConfigManager.saveConfig(context);
        File file = new File(readerSlideshowBean.getTestFilePath());
        Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(context, ViewDocumentUtils.autoSlideShowIntent(file,
                readerSlideshowBean.getSlideshowTotalPage(), readerSlideshowBean.getSlideshowInterval()));
    }
}

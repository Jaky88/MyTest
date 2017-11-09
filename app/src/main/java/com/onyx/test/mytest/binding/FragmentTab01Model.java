package com.onyx.test.mytest.binding;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.mytest.BR;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.manager.ConfigManager;

import java.io.File;

/**
 * Created by jaky on 2017/11/4 0004.
 */

public class FragmentTab01Model extends BaseObservable {

    private Fragment fragment;
    private ReaderSlideshowBean readerSlideshowBean;

    public FragmentTab01Model(Fragment fragment) {
        this.fragment = fragment;
        this.readerSlideshowBean = ConfigManager.getConfig(fragment.getActivity()).getReaderSlideshowBean();
    }

    @Bindable
    public ReaderSlideshowBean getReaderSlideshowBean() {
        return readerSlideshowBean;
    }

    public void setReaderSlideshowBean(ReaderSlideshowBean readerSlideshowBean) {
        this.readerSlideshowBean = readerSlideshowBean;
        notifyPropertyChanged(BR.readerSlideshowBean);
    }

    public void onSettingsClick(View view) {
        setReaderSlideshowBean(readerSlideshowBean);
        ConfigManager.saveConfig(fragment.getActivity());
        File file = new File(readerSlideshowBean.getTestFilePath());
        Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(fragment.getActivity(), ViewDocumentUtils.autoSlideShowIntent(file,
                readerSlideshowBean.getSlideshowTotalPage(), readerSlideshowBean.getSlideshowInterval()));
    }

    public void onSelectFileClick(View view) {
        new LFilePicker().withSupportFragment(fragment)
                .withRequestCode(Constant.REQUESTCODE_FROM_FRAGMENT)
                .withTitle("选择文件")
                .withTitleColor("#FF000000")
                .withMutilyMode(false)
                .withFileFilter(new String[]{".txt", ".pdf", ".epub", ".fb2", ".djvu"})
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }
}

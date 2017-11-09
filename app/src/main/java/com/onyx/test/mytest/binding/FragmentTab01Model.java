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
import com.onyx.test.mytest.model.AppConfig;
import com.onyx.test.mytest.model.bean.ConfigBean;

import java.io.File;

/**
 * Created by jaky on 2017/11/4 0004.
 */

public class FragmentTab01Model extends BaseObservable {

    private String testProjectName = "Kreader自动翻页测试";
    private int slideshowInterval = 20;
    private String testFilePath = "mnt/sdcard/test.pdf";
    private int slideshowTotalPage = 2000;
    private int slideshowStartPage = 0;
    private boolean isBootCompletedAtoTest = false;

    private Fragment fragment;
    private ConfigBean config;


    public FragmentTab01Model(Fragment f) {
        this.fragment = f;
        this.config = AppConfig.getConfig(f.getActivity());
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }

    public void onSettingsClick(View view) {
        config.setSlideFileName(testFilePath);
        config.setSlideInterval(slideshowInterval);
        config.setSlideTime(slideshowTotalPage);
        config.setBootUpLastDocumentOpenChecked(isBootCompletedAtoTest);
        AppConfig.saveConfig(fragment.getActivity());
        File file = new File(config.getSlideFileName());
        Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(fragment.getActivity(), ViewDocumentUtils.autoSlideShowIntent(file, config.getSlideTime(), config.getSlideInterval()));
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

    @Bindable
    public String getTestProjectName() {
        return testProjectName;
    }

    public void setTestProjectName(String testProjectName) {
        this.testProjectName = testProjectName;
    }

    @Bindable
    public int getSlideshowInterval() {
        return slideshowInterval;
    }

    public void setSlideshowInterval(int slideshowInterval) {
        this.slideshowInterval = slideshowInterval;
    }

    @Bindable
    public String getTestFilePath() {
        return testFilePath;
    }

    public void setTestFilePath(String testFilePath) {
        this.testFilePath = testFilePath;
    }

    @Bindable
    public int getSlideshowTotalPage() {
        return slideshowTotalPage;
    }

    public void setSlideshowTotalPage(int slideshowTotalPage) {
        this.slideshowTotalPage = slideshowTotalPage;
    }

    @Bindable
    public int getSlideshowStartPage() {
        return slideshowStartPage;
    }

    public void setSlideshowStartPage(int slideshowStartPage) {
        this.slideshowStartPage = slideshowStartPage;
    }

    @Bindable
    public boolean isBootCompletedAtoTest() {
        return isBootCompletedAtoTest;
    }

    public void setBootCompletedAtoTest(boolean bootCompletedAtoTest) {
        isBootCompletedAtoTest = bootCompletedAtoTest;
    }
}

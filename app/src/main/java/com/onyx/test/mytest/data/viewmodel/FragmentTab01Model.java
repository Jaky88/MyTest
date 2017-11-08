package com.onyx.test.mytest.data.viewmodel;

import android.content.Intent;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.mytest.config.AppConfig;
import com.onyx.test.mytest.config.ConfigBean;

import java.io.File;

/**
 * Created by jaky on 2017/11/4 0004.
 */

public class FragmentTab01Model extends BaseObservable {

    public String testProjectName = "Kreader自动翻页测试";
    public int slideshowInterval = 20;
    public String testFilePath = "mnt/sdcard/test.pdf";
    public int slideshowTotalPage = 2000;
    public int slideshowStartPage = 0;
    public boolean isBootCompletedAtoTest = false;
    public String btnSelectFile = "选择";
    public String btnSettings = "设置";
    public Fragment fragment;
    public ConfigBean config;


    public FragmentTab01Model(Fragment f, ConfigBean config) {
        this.fragment = f;
        this.config = config;
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

    @Bindable
    public String getBtnSelectFile() {
        return btnSelectFile;
    }

    public void setBtnSelectFile(String btnSelectFile) {
        this.btnSelectFile = btnSelectFile;
    }

    @Bindable
    public String getBtnSettings() {
        return btnSettings;
    }

    public void setBtnSettings(String btnSettings) {
        this.btnSettings = btnSettings;
    }
}

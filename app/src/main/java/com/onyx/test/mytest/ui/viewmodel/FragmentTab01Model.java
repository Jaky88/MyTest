package com.onyx.test.mytest.ui.viewmodel;

import android.content.Intent;
import android.databinding.Observable;
import android.databinding.ObservableField;
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

public class FragmentTab01Model implements Observable {

    private String testProjectName = "Kreader自动翻页测试";
    private ObservableField<String> testFilePath = new ObservableField<>("mnt/sdcard/test.pdf");
    private ObservableField<Integer> slideshowInterval = new ObservableField<>(20);
    private ObservableField<Integer> slideshowTotalPage = new ObservableField<>(2000);
    private ObservableField<Integer> slideshowStartPage = new ObservableField<>(0);
    private ObservableField<Boolean> isBootCompletedAtoTest = new ObservableField<>(false);
    private String btnSelectFile = "选择";
    private String btnSettings = "设置";
    private Fragment fragment;
    private ConfigBean config;

    public ObservableField<String> getTestFilePath() {
        return testFilePath;
    }

    public void setTestFilePath(ObservableField<String> testFilePath) {
        this.testFilePath = testFilePath;
    }

    public ObservableField<Integer> getSlideshowInterval() {
        return slideshowInterval;
    }

    public void setSlideshowInterval(ObservableField<Integer> slideshowInterval) {
        this.slideshowInterval = slideshowInterval;
    }

    public ObservableField<Integer> getSlideshowTotalPage() {
        return slideshowTotalPage;
    }

    public void setSlideshowTotalPage(ObservableField<Integer> slideshowTotalPage) {
        this.slideshowTotalPage = slideshowTotalPage;
    }

    public ObservableField<Integer> getSlideshowStartPage() {
        return slideshowStartPage;
    }

    public void setSlideshowStartPage(ObservableField<Integer> slideshowStartPage) {
        this.slideshowStartPage = slideshowStartPage;
    }

    public ObservableField<Boolean> getIsBootCompletedAtoTest() {
        return isBootCompletedAtoTest;
    }

    public void setIsBootCompletedAtoTest(ObservableField<Boolean> isBootCompletedAtoTest) {
        this.isBootCompletedAtoTest = isBootCompletedAtoTest;
    }

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
        config.setSlideFileName(testFilePath.get());
        config.setSlideInterval(slideshowInterval.get());
        config.setSlideTime(slideshowTotalPage.get());
        config.setBootUpLastDocumentOpenChecked(isBootCompletedAtoTest.get());
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

    public String getTestProjectName() {
        return testProjectName;
    }

    public void setTestProjectName(String testProjectName) {
        this.testProjectName = testProjectName;
    }

    public String getBtnSelectFile() {
        return btnSelectFile;
    }

    public void setBtnSelectFile(String btnSelectFile) {
        this.btnSelectFile = btnSelectFile;
    }

    public String getBtnSettings() {
        return btnSettings;
    }

    public void setBtnSettings(String btnSettings) {
        this.btnSettings = btnSettings;
    }

}

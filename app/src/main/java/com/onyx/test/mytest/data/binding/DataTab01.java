package com.onyx.test.mytest.data.binding;

import android.databinding.Observable;
import android.view.View;
import android.widget.Toast;

/**
 * Created by jaky on 2017/11/4 0004.
 */

public class DataTab01 implements Observable {

    private String testProjectName = "Kreader自动翻页测试";
    private int slideshowInterval = 20;
    private String testFilePath = "mnt/sdcard/test.pdf";
    private int slideshowTotalPage = 2000;
    private int slideshowStartPage = 0;
    private boolean isBootCompletedAtoTest = false;
    private String btnSelectFile = "选择";
    private String btnSettings = "设置";

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

    public String getTestProjectName() {
        return testProjectName;
    }

    public void setTestProjectName(String testProjectName) {
        this.testProjectName = testProjectName;
    }

    public int getSlideshowInterval() {
        return slideshowInterval;
    }

    public void setSlideshowInterval(int slideshowInterval) {
        this.slideshowInterval = slideshowInterval;
    }

    public String getTestFilePath() {
        return testFilePath;
    }

    public void setTestFilePath(String testFilePath) {
        this.testFilePath = testFilePath;
    }

    public int getSlideshowTotalPage() {
        return slideshowTotalPage;
    }

    public void setSlideshowTotalPage(int slideshowTotalPage) {
        this.slideshowTotalPage = slideshowTotalPage;
    }

    public int getSlideshowStartPage() {
        return slideshowStartPage;
    }

    public void setSlideshowStartPage(int slideshowStartPage) {
        this.slideshowStartPage = slideshowStartPage;
    }

    public boolean isBootCompletedAtoTest() {
        return isBootCompletedAtoTest;
    }

    public void setBootCompletedAtoTest(boolean bootCompletedAtoTest) {
        isBootCompletedAtoTest = bootCompletedAtoTest;
    }

    @Override
    public void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }

    @Override
    public void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {

    }

}

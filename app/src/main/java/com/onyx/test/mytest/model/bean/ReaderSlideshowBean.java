package com.onyx.test.mytest.model.bean;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

/**
 * Created by jaky on 2017/8/16.
 */

public class ReaderSlideshowBean extends BaseObservable {

    private boolean isBootCompletedAtoTest = true;
    private int slideshowInterval = 20;
    private int slideshowTotalPage = 2000;
    private int slideshowStartPage = 0;
    private String testFilePath = "mnt/sdcard/test.pdf";

    @Bindable
    public int getSlideshowStartPage() {
        return slideshowStartPage;
    }

    public void setSlideshowStartPage(int slideshowStartPage) {
        this.slideshowStartPage = slideshowStartPage;
    }

    @Bindable
    public int getSlideshowInterval() {
        return slideshowInterval;
    }

    public void setSlideshowInterval(int slideshowInterval) {
        this.slideshowInterval = slideshowInterval;
    }

    @Bindable
    public int getSlideshowTotalPage() {
        return slideshowTotalPage;
    }

    public void setSlideshowTotalPage(int slideshowTotalPage) {
        this.slideshowTotalPage = slideshowTotalPage;
    }

    @Bindable
    public String getTestFilePath() {
        return testFilePath;
    }

    public void setTestFilePath(String testFilePath) {
        this.testFilePath = testFilePath;
    }

    @Bindable
    public boolean isBootCompletedAtoTest() {
        return isBootCompletedAtoTest;
    }

    public void setBootCompletedAtoTest(boolean bootCompletedAtoTest) {
        this.isBootCompletedAtoTest = bootCompletedAtoTest;
    }
}

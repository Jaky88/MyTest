package com.onyx.test.mytest.model.bean;

/**
 * Created by jaky on 2017/8/16.
 */

public class ConfigBean {
    private boolean bootUpLastDocumentOpenChecked = true;
    private int slideInterval = 10;
    private int slideTime = 0;
    private String slideFileName = "test.pdf";

    public int getSlideInterval() {
        return slideInterval;
    }

    public void setSlideInterval(int slideInterval) {
        this.slideInterval = slideInterval;
    }

    public int getSlideTime() {
        return slideTime;
    }

    public void setSlideTime(int slideTime) {
        this.slideTime = slideTime;
    }

    public String getSlideFileName() {
        return slideFileName;
    }

    public void setSlideFileName(String slideFileName) {
        this.slideFileName = slideFileName;
    }

    public boolean isBootUpLastDocumentOpenChecked() {
        return bootUpLastDocumentOpenChecked;
    }

    public void setBootUpLastDocumentOpenChecked(boolean bootUpLastDocumentOpenChecked) {
        this.bootUpLastDocumentOpenChecked = bootUpLastDocumentOpenChecked;
    }
}

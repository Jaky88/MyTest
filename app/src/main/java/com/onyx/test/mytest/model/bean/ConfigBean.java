package com.onyx.test.mytest.model.bean;

import java.io.Serializable;

/**
 * Created by jaky on 2017/11/9 0009.
 */

public class ConfigBean implements Serializable{
    private ReaderSlideshowBean readerSlideshowBean;

    public ConfigBean(ReaderSlideshowBean readerSlideshowBean) {
        this.readerSlideshowBean = readerSlideshowBean;
    }

    public ReaderSlideshowBean getReaderSlideshowBean() {
        return readerSlideshowBean;
    }

    public void setReaderSlideshowBean(ReaderSlideshowBean readerSlideshowBean) {
        this.readerSlideshowBean = readerSlideshowBean;
    }
}

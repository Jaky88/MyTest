package com.jaky.data.bean;

/**
 * Created by Jack on 2017/12/3.
 */

public class AppConfig {

    private String beanName = AppConfig.class.getSimpleName();
    private String savePath = "";

    public AppConfig(String savePath) {
        this.savePath = savePath;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}

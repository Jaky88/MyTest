package com.onyx.test.mytest.translator.config;

/**
 * Created by lion on 2016/10/28.
 * 翻译平台
 */
public enum TranslatePlatform {
    YOUDAO("youdao"),
    GOOGLE("google"),
    BAIDU("baidu");

    private String value;

    TranslatePlatform(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

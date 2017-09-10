package com.onyx.test.styletest.translator.entity;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/10 0010,3:26
 * @Version: V1.0
 * @Description: TODO
 */

public class GoogleParams implements Params {

    private String key;
    private String srcLanguage;
    private String targetLanguage;
    private String encode;

    public GoogleParams(String key, String srcLanguage, String targetLanguage, String encode) {
        this.key = key;
        this.srcLanguage = srcLanguage;
        this.targetLanguage = targetLanguage;
        this.encode = encode;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSrcLanguage() {
        return srcLanguage;
    }

    public void setSrcLanguage(String srcLanguage) {
        this.srcLanguage = srcLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    @Override
    public String toString() {
        return "GoogleParams{" +
                "key='" + key + '\'' +
                ", srcLanguage='" + srcLanguage + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                ", encode='" + encode + '\'' +
                '}';
    }
}

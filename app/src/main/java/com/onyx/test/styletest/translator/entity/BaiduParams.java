package com.onyx.test.styletest.translator.entity;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/10 0010,2:06
 * @Version: V1.0
 * @Description: TODO
 */

public class BaiduParams implements Params{

    private String shortLanguage;
    private String targetLanguage;
    private String baiduAppId;
    private int randomInt;
    private String encode;
    private String sign;

    public BaiduParams(String shortLanguage, String targetLanguage, String baiduAppId, int randomInt, String encode, String sign) {
        this.shortLanguage = shortLanguage;
        this.targetLanguage = targetLanguage;
        this.baiduAppId = baiduAppId;
        this.randomInt = randomInt;
        this.encode = encode;
        this.sign = sign;
    }

    public String getShortLanguage() {
        return shortLanguage;
    }

    public void setShortLanguage(String shortLanguage) {
        this.shortLanguage = shortLanguage;
    }

    public String getTargetLanguage() {
        return targetLanguage;
    }

    public void setTargetLanguage(String targetLanguage) {
        this.targetLanguage = targetLanguage;
    }

    public String getBaiduAppId() {
        return baiduAppId;
    }

    public void setBaiduAppId(String baiduAppId) {
        this.baiduAppId = baiduAppId;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public int getRandomInt() {
        return randomInt;
    }

    public void setRandomInt(int randomInt) {
        this.randomInt = randomInt;
    }

    @Override
    public String toString() {
        return "BaiduParams{" +
                "shortLanguage='" + shortLanguage + '\'' +
                ", targetLanguage='" + targetLanguage + '\'' +
                ", baiduAppId='" + baiduAppId + '\'' +
                ", randomInt=" + randomInt +
                ", encode='" + encode + '\'' +
                ", sign='" + sign + '\'' +
                '}';
    }
}

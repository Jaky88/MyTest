package com.onyx.test.mytest.translator.entity;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/10 0010,3:54
 * @Version: V1.0
 * @Description: TODO
 */

public class YouDaoParams implements Params {
    private String keyfrom;
    private String key;
    private String type;
    private String doctype;
    private String version;
    private String encode;

    public YouDaoParams(String keyfrom, String key, String type, String doctype, String version, String encode) {
        this.keyfrom = keyfrom;
        this.key = key;
        this.type = type;
        this.doctype = doctype;
        this.version = version;
        this.encode = encode;
    }

    public String getKeyfrom() {
        return keyfrom;
    }

    public void setKeyfrom(String keyfrom) {
        this.keyfrom = keyfrom;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDoctype() {
        return doctype;
    }

    public void setDoctype(String doctype) {
        this.doctype = doctype;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getEncode() {
        return encode;
    }

    public void setEncode(String encode) {
        this.encode = encode;
    }
}

package com.onyx.test.styletest.translator.entity;

import java.util.List;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: StyleTest
 * @Author: Jack
 * @Date: 2017/9/10 0010,12:41
 * @Version: V1.0
 * @Description: TODO
 */

public class BaiduResult {

    public String from;
    public String to;
    public List<TransResultBean> trans_result;

    public BaiduResult(String from, String to, List<TransResultBean> trans_result) {
        this.from = from;
        this.to = to;
        this.trans_result = trans_result;
    }

    public static class TransResultBean {
        public String src;
        public String dst;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<TransResultBean> getTrans_result() {
        return trans_result;
    }

    public void setTrans_result(List<TransResultBean> trans_result) {
        this.trans_result = trans_result;
    }
}

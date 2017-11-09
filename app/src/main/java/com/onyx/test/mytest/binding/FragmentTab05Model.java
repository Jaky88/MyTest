package com.onyx.test.mytest.binding;

import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;

import com.onyx.test.mytest.model.manager.ConfigManager;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.entity.DataUtil;

import java.util.List;

/**
 * @Copyright: Copyright © 2017 Onyx International Inc. All rights reserved.
 * @Project: MyTest
 * @Author: Jack
 * @Date: 2017/11/8 0008,21:59
 * @Version: V1.0
 * @Description: TODO
 */

public class FragmentTab05Model extends BaseObservable {
    private Fragment fragment;
    private ReaderSlideshowBean config;
    private List<Pair<String, String>> mDatas;

    public ReaderSlideshowBean getConfig() {
        return config;
    }

    public void setConfig(ReaderSlideshowBean config) {
        this.config = config;
    }

    @Bindable
    public List<Pair<String, String>> getDatas() {
        return mDatas;
    }

    public void setDatas(List<Pair<String, String>> datas) {
        mDatas = datas;
    }

    public FragmentTab05Model(Fragment fragment) {
        this.fragment = fragment;
        this.config = ConfigManager.getConfig(fragment.getActivity()).getReaderSlideshowBean();
        initData();
    }

    private void initData() {
        mDatas = DataUtil.getInstance(fragment.getActivity()).getData();
    }
}

package com.onyx.test.mytest.binding;

import android.databinding.BaseObservable;
import android.support.v4.app.Fragment;

import com.onyx.test.mytest.model.bean.ConfigBean;

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
    private ConfigBean config;

    public FragmentTab05Model(Fragment f, ConfigBean config) {
        this.fragment = f;
        this.config = config;
    }
}

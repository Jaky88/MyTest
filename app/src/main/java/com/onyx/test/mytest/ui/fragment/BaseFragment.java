package com.onyx.test.mytest.ui.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.onyx.test.mytest.model.AppConfig;
import com.onyx.test.mytest.model.bean.ConfigBean;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public abstract class BaseFragment<F extends ViewDataBinding> extends Fragment {

    protected F bindingView;
    protected ConfigBean config;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        config = AppConfig.getConfig(getActivity());
        bindingView = DataBindingUtil.inflate(inflater, getLayout(), container, false);
        bindData();
        return  bindingView.getRoot();
    }

    public abstract int getLayout();
    public abstract void bindData();
}

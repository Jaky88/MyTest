package com.onyx.test.mytest.view.fragment;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public abstract class BaseFragment<F extends ViewDataBinding> extends Fragment {

    protected F bindingView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        bindingView = DataBindingUtil.inflate(inflater, getLayout(), container, false);
        bindData();
        return  bindingView.getRoot();
    }

    public abstract int getLayout();
    public abstract void bindData();
}

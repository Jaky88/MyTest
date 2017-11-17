package com.onyx.test.mytest.view.fragment;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab06Model;
import com.onyx.test.mytest.databinding.FragmentTab6Binding;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab6Fragment extends BaseFragment<FragmentTab6Binding> {

    @Override
    public int getLayout() {
        return R.layout.fragment_tab6;
    }

    @Override
    public void bindData() {
        bindingView.setBean(new FragmentTab06Model(getActivity()));
    }
}

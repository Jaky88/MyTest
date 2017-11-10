package com.onyx.test.mytest.view.fragment;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab05Model;
import com.onyx.test.mytest.databinding.FragmentTab5Binding;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab5Fragment extends BaseFragment<FragmentTab5Binding> {

    @Override
    public int getLayout() {
        return R.layout.fragment_tab5;
    }

    @Override
    public void bindData() {
        bindingView.setBean(new FragmentTab05Model(getActivity()));
    }
}

package com.onyx.test.mytest.view.fragment;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab03Model;
import com.onyx.test.mytest.databinding.FragmentTab3Binding;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab3Fragment extends BaseFragment<FragmentTab3Binding> {

    @Override
    public int getLayout() {
        return R.layout.fragment_tab3;
    }

    @Override
    public void bindData() {
        bindingView.setBean(new FragmentTab03Model(getActivity()));
    }
}

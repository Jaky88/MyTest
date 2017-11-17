package com.onyx.test.mytest.view.fragment;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.databinding.FragmentTab2Binding;
import com.onyx.test.mytest.binding.FragmentTab02Model;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab2Fragment extends BaseFragment<FragmentTab2Binding> {

    @Override
    public int getLayout() {
        return R.layout.fragment_tab2;
    }

    @Override
    public void bindData() {
        bindingView.setBean(new FragmentTab02Model(getActivity()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bindingView.getBean().destroy();
    }
}

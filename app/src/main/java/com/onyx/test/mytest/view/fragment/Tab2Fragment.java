package com.onyx.test.mytest.view.fragment;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.databinding.FragmentTab2Binding;
import com.onyx.test.mytest.binding.FragmentTab02Model;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab2Fragment extends BaseFragment<FragmentTab2Binding> {
    private static final int REC_REQUESTCODE = 1;
    private FragmentTab02Model bean;

    @Override
    public int getLayout() {
        return R.layout.fragment_tab2;
    }

    @Override
    public void bindData() {
        bean = new FragmentTab02Model(Tab2Fragment.this, config);
        bindingView.setBean(bean);
    }

}

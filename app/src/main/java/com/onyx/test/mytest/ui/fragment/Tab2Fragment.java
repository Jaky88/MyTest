package com.onyx.test.mytest.ui.fragment;

import android.content.Intent;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.databinding.FragmentTab2Binding;
import com.onyx.test.mytest.model.utils.ShellUtils;
import com.onyx.test.mytest.model.utils.ShellUtils.CommandResult;
import com.onyx.test.mytest.viewmodel.FragmentTab02Model;
import com.onyx.test.mytest.viewmodel.FragmentTab01Model;

import butterknife.ButterKnife;
import butterknife.OnClick;

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

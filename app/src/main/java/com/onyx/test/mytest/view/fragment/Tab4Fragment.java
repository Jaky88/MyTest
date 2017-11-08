package com.onyx.test.mytest.view.fragment;

import android.widget.CompoundButton;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab04Model;
import com.onyx.test.mytest.databinding.FragmentTab4Binding;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab4Fragment extends BaseFragment<FragmentTab4Binding> {

    @Override
    public int getLayout() {
        return R.layout.fragment_tab4;
    }

    @Override
    public void bindData() {
        bindingView.setBean(new FragmentTab04Model(Tab4Fragment.this, config));
        bindingView.cbOpenWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bindingView.getBean().onWifiCheckedChanged(isChecked);
                bindingView.invalidateAll();
            }
        });
    }
}

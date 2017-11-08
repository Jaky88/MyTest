package com.onyx.test.mytest.ui.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.utils.Constant;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.config.AppConfig;
import com.onyx.test.mytest.config.ConfigBean;
import com.onyx.test.mytest.data.viewmodel.FragmentTab01Model;
import com.onyx.test.mytest.databinding.FragmentTab1Binding;

import java.util.List;


/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab1Fragment extends BaseFragment {

    private FragmentTab01Model bean;
    private FragmentTab1Binding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ConfigBean config = AppConfig.getConfig(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab1, container, false);
        bean = new FragmentTab01Model(Tab1Fragment.this, config);
        binding.setBean(bean);
        return binding.getRoot();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUESTCODE_FROM_FRAGMENT) {
                List<String> list = data.getStringArrayListExtra("paths");
                for (String s : list) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    binding.etFileName.setText(s);
                    bean.setTestFilePath(s);
                }
            }
        }
    }
}

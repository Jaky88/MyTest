package com.onyx.test.mytest.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.utils.Constant;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.databinding.FragmentTab1Binding;
import com.onyx.test.mytest.binding.FragmentTab01Model;

import java.util.List;


/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab1Fragment extends BaseFragment<FragmentTab1Binding> {

    @Override
    public int getLayout() {
        return R.layout.fragment_tab1;
    }

    @Override
    public void bindData() {
        bindingView.setBean(new FragmentTab01Model(Tab1Fragment.this, config));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUESTCODE_FROM_FRAGMENT) {
                List<String> list = data.getStringArrayListExtra("paths");
                for (String s : list) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    bindingView.getBean().setTestFilePath(s);
                    bindingView.notifyChange();
                }
            }
        }
    }
}

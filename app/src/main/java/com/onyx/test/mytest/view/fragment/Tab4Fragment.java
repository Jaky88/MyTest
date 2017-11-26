package com.onyx.test.mytest.view.fragment;

import android.app.Activity;
import android.content.Intent;
import android.widget.CompoundButton;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.binding.FragmentTab04Model;
import com.onyx.test.mytest.databinding.FragmentTab4Binding;

import java.util.List;

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
        bindingView.setBean(new FragmentTab04Model(getActivity()));
        bindingView.cbOpenWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                bindingView.getBean().onWifiCheckedChanged(isChecked);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUESTCODE_FROM_FRAGMENT) {
                List<String> list = data.getStringArrayListExtra("paths");
                for (String s : list) {
                    bindingView.edtFileName.setText(s);
                }
            }
        }
    }

    private void selectFile() {
        new LFilePicker().withSupportFragment(Tab4Fragment.this)
                .withRequestCode(Constant.REQUESTCODE_FROM_FRAGMENT)
                .withTitle("选择文件")
                .withTitleColor("#FF000000")
                .withMutilyMode(false)
                .withFileFilter(new String[]{".txt", ".pdf", ".epub", ".fb2", ".djvu"})
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }
}

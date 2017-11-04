package com.onyx.test.mytest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.mytest.R;
import com.onyx.test.mytest.config.AppConfig;
import com.onyx.test.mytest.config.ConfigBean;
import com.onyx.test.mytest.data.binding.DataTab01;
import com.onyx.test.mytest.data.binding.EventHandler;
import com.onyx.test.mytest.databinding.FragmentTab1Binding;

import java.io.File;
import java.util.List;


/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab1Fragment extends BaseFragment {


    ConfigBean config;
    private DataTab01 bean;
    private FragmentTab1Binding binding;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        config = AppConfig.getConfig(getActivity());
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab1, container, false);
        bean = new DataTab01();
        binding.setBean(bean);
        binding.setHandler(new EventHandler());
        return binding.getRoot();
    }


    private void selectFile() {
        new LFilePicker().withSupportFragment(Tab1Fragment.this)
                .withRequestCode(Constant.REQUESTCODE_FROM_FRAGMENT)
                .withTitle("选择文件")
                .withTitleColor("#FF000000")
                .withMutilyMode(false)
                .withFileFilter(new String[]{".txt", ".pdf", ".epub", ".fb2", ".djvu"})
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }

    private void setConfig() {

        config.setSlideFileName(bean.getTestFilePath());
        config.setSlideInterval(bean.getSlideshowInterval());
        config.setSlideTime(bean.getSlideshowTotalPage());
        config.setBootUpLastDocumentOpenChecked(bean.isBootCompletedAtoTest());
        AppConfig.saveConfig(getActivity());

        File file = new File(config.getSlideFileName());
        Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(getActivity(), ViewDocumentUtils.autoSlideShowIntent(file, config.getSlideTime(), config.getSlideInterval()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == Constant.REQUESTCODE_FROM_FRAGMENT) {
                List<String> list = data.getStringArrayListExtra("paths");
                for (String s : list) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    bean.setTestFilePath(s);
                }
            }
        }
    }
}

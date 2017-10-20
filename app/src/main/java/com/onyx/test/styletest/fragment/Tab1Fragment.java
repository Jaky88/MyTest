package com.onyx.test.styletest.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.leon.lfilepickerlibrary.LFilePicker;
import com.leon.lfilepickerlibrary.utils.Constant;
import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.StringUtils;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.styletest.R;
import com.onyx.test.styletest.config.AppConfig;
import com.onyx.test.styletest.config.ConfigBean;

import java.io.File;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab1Fragment extends BaseFragment {
    public static int REQUESTCODE_FROM_ACTIVITY = 1000;
    public static int REQUESTCODE_FROM_FRAGMENT = 1001;

    @Bind(R.id.et_slide_interval)
    EditText etSlideInterval;
    @Bind(R.id.et_slide_time)
    EditText etSlideTime;
    @Bind(R.id.btn_set)
    Button btnSet;
    @Bind(R.id.et_file_name)
    EditText etFileName;
    ConfigBean config;
    @Bind(R.id.btn_file_select)
    Button btnFileSelect;
    @Bind(R.id.cb_auto_open_book)
    CheckBox cbAutoOpenBook;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab01, null);
        ButterKnife.bind(this, view);
        config = AppConfig.getConfig(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.btn_file_select, R.id.btn_set})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_file_select:
                selectFile();
                break;
            case R.id.btn_set:
                setConfig();
                break;
        }
    }

    private void selectFile() {
//        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
//        intent.setType("*/*");
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
//        getActivity().startActivityForResult(intent, REQUESTCODE_FROM_ACTIVITY);
        new LFilePicker().withSupportFragment(Tab1Fragment.this)
                .withRequestCode(REQUESTCODE_FROM_FRAGMENT)
                .withTitle("选择文件")
                .withTitleColor("#FF000000")
                .withMutilyMode(false)
                .withFileFilter(new String[]{".txt", ".pdf",".epub",".fb2",".djvu"})
                .withBackIcon(Constant.BACKICON_STYLETHREE)
                .start();
    }

    private void setConfig() {
        String strInterval = etSlideInterval.getText().toString().trim();
        String strSlideTime = etSlideTime.getText().toString().trim();
        String strFileName = etFileName.getText().toString().trim();
        if (StringUtils.isNullOrEmpty(strInterval) || StringUtils.isNullOrEmpty(strSlideTime) || StringUtils.isNullOrEmpty(strFileName)) {
            Toast.makeText(getActivity(), "值不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        int interval = Integer.parseInt(strInterval);
        int time = Integer.parseInt(strSlideTime);

        config.setSlideFileName(strFileName);
        config.setSlideInterval(interval);
        config.setSlideTime(time);
        config.setBootUpLastDocumentOpenChecked(cbAutoOpenBook.isChecked());
        Log.d("======", "=======setConfig==========" + config.toString());
        AppConfig.saveConfig(getActivity());
        File file = new File( config.getSlideFileName());
        Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(getActivity(), ViewDocumentUtils.autoSlideShowIntent(file, Integer.MAX_VALUE, config.getSlideInterval()));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Log.d("========","========RESULT_OK=======");
            if (requestCode == REQUESTCODE_FROM_FRAGMENT) {
                List<String> list = data.getStringArrayListExtra("paths");
                for (String s : list) {
                    Toast.makeText(getActivity(), s, Toast.LENGTH_SHORT).show();
                    etFileName.setText(s);
                }
            }
        }
    }
}

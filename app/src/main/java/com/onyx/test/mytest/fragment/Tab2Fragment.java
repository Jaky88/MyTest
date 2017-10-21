package com.onyx.test.mytest.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.onyx.test.mytest.R;

import org.apache.commons.lang3.StringUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab2Fragment extends BaseFragment implements View.OnClickListener {
    private static final int REC_REQUESTCODE = 1;
    @Bind(R.id.src_file_path)
    EditText srcFilePath;
    @Bind(R.id.src_file_select)
    Button srcFileSelect;
    @Bind(R.id.dest_file_path)
    EditText destFilePath;
    @Bind(R.id.dest_file_select)
    Button destFileSelect;
    @Bind(R.id.start_replace)
    Button startReplace;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab02, null);
        ButterKnife.bind(this, view);
        initView();
        initEvent();
        return view;
    }

    private void initEvent() {
        srcFileSelect.setOnClickListener(this);
        destFileSelect.setOnClickListener(this);
        startReplace.setOnClickListener(this);
    }

    private void initView() {
        String src = srcFilePath.getText().toString().trim();
        String dest = destFilePath.getText().toString().trim();
        if (StringUtils.isNotBlank(src) && StringUtils.isNotBlank(dest)) {

        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.src_file_select:
                getSrcFile();
                break;
            case R.id.dest_file_select:
                getDestFile();
                break;
            case R.id.start_replace:
                startReplaceWork();
                break;

        }
    }

    private void startReplaceWork() {

    }

    public void getSrcFile() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getActivity().startActivityForResult(intent,REC_REQUESTCODE);
    }

    public void getDestFile() {
        Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getActivity().startActivityForResult(intent,REC_REQUESTCODE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}

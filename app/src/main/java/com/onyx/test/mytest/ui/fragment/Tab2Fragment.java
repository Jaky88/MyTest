package com.onyx.test.mytest.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.onyx.test.mytest.R;
import com.onyx.test.mytest.utils.ShellUtils;
import com.onyx.test.mytest.utils.ShellUtils.CommandResult;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab2Fragment extends BaseFragment {
    private static final int REC_REQUESTCODE = 1;
    @Bind(R.id.btn_atart_activity)
    Button btnAtartActivity;
    @Bind(R.id.tv_info)
    TextView tvInfo;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab02, null);
        ButterKnife.bind(this, view);
        initData();
        return view;
    }

    private void initData() {

    }


    private void startReplaceWork() {

    }

    public void getSrcFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getActivity().startActivityForResult(intent, REC_REQUESTCODE);
    }

    public void getDestFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        getActivity().startActivityForResult(intent, REC_REQUESTCODE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @OnClick(R.id.btn_atart_activity)
    public void onViewClicked() {
        CommandResult ret = ShellUtils.execCommand("cat /proc/partitions", false, true);
        System.out.printf("===========：%s", ret.successMsg);
        tvInfo.setText(ret.successMsg);
    }
}

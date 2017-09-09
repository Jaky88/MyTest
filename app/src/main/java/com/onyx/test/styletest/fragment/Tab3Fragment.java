package com.onyx.test.styletest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onyx.test.styletest.R;
import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.TranslateManager;
import com.onyx.test.styletest.translator.TranslatePlatform;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by jaky on 2017/9/7 0007.
 */

public class Tab3Fragment extends BaseFragment implements View.OnClickListener {
    @Bind(R.id.btn_translate)
    Button btnTranslate;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab03, null);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        btnTranslate.setOnClickListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_translate:
                translate();
                break;
        }
    }

    private void translate() {
        File currentPath = new File("/sdcard/translate");
        baidu(currentPath, false);
    }

    private static void youdao(File currentPath, boolean translateAllXml) {
        TranslateManager.getInstance().
                init(currentPath.getAbsolutePath(), translateAllXml, TranslatePlatform.YOUDAO);
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.EN);
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.JA);
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.ZH_TW);
    }

    private static void google(File currentPath, boolean translateAllXml) {
        TranslateManager.getInstance().
                init(currentPath.getAbsolutePath(), translateAllXml, TranslatePlatform.GOOGLE);
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.EN);
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.JA);
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.ZH_TW);
    }

    private static void googleAll(File currentPath, boolean translateAllXml) {
        TranslateManager.getInstance().
                init(currentPath.getAbsolutePath(), translateAllXml, TranslatePlatform.GOOGLE);
        TranslateManager.getInstance().translateAll(Language.ZH_CN);
    }

    private static void baidu(File currentPath, boolean translateAllXml) {
        Log.d("=========","=====translate=========baidu====");
        TranslateManager.getInstance().init(currentPath.getAbsolutePath(), translateAllXml, TranslatePlatform.BAIDU);
//        TranslateManager.getInstance().translate(Language.EN, Language.ES);//英语转西班牙语
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.EN);
//        TranslateManager.getInstance().translate(Language.ZH_CN, Language.JA);
//        TranslateManager.getInstance().translate(Language.ZH_CN, Language.ZH_TW);
    }
}

package com.onyx.test.styletest.fragment;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.onyx.test.styletest.R;
import com.onyx.test.styletest.translator.TranslateManager;
import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.config.TranslatePlatform;

import java.io.File;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        String base = Environment.getExternalStorageDirectory().getAbsolutePath();
        final File currentPath = new File(base + "/translate");
        btnTranslate.setEnabled(false);
        Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
//                baidu(currentPath, false);
                google(currentPath, false);
                subscriber.onNext("OK!");
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        Log.d("====", "========onError========");
                        btnTranslate.setEnabled(true);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("====", "========" + s + "========");
                        btnTranslate.setEnabled(true);

                    }
                });

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

package com.onyx.test.mytest.binding;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.onyx.test.mytest.model.manager.ConfigManager;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.translator.TranslateManager;
import com.onyx.test.mytest.translator.config.Language;
import com.onyx.test.mytest.translator.config.TranslatePlatform;

import java.io.File;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by jaky on 2017/11/8 0008.
 */

public class FragmentTab03Model extends BaseObservable {

    private Context context;
    private ReaderSlideshowBean config;
    private String translatePath = "/sdcard/translate";

    public FragmentTab03Model(Context context) {
        this.context = context;
        this.config = ConfigManager.getConfig(context).getReaderSlideshowBean();
    }

    @Bindable
    public String getTranslatePath() {
        return translatePath;
    }

    public void setTranslatePath(String translatePath) {
        this.translatePath = translatePath;
    }

    public void onTranslateClick(View view) {
        String base = Environment.getExternalStorageDirectory().getAbsolutePath();
        translatePath = base + "/translate";
        final File currentPath = new File(translatePath);
//        btnTranslate.setEnabled(false);
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
//                        btnTranslate.setEnabled(true);
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d("====", "========" + s + "========");
//                        btnTranslate.setEnabled(true);

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
        TranslateManager.getInstance().init(currentPath.getAbsolutePath(), translateAllXml, TranslatePlatform.BAIDU);
//        TranslateManager.getInstance().translate(Language.EN, Language.ES);//英语转西班牙语
        TranslateManager.getInstance().translate(Language.ZH_CN, Language.EN);
//        TranslateManager.getInstance().translate(Language.ZH_CN, Language.JA);
//        TranslateManager.getInstance().translate(Language.ZH_CN, Language.ZH_TW);
    }
}

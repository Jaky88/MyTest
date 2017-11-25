package com.onyx.test.mytest.view.app;

import android.app.Application;

//import com.raizlabs.android.dbflow.readerSlideshowBean.DatabaseHolder;
//import com.raizlabs.android.dbflow.readerSlideshowBean.FlowConfig;
//import com.raizlabs.android.dbflow.readerSlideshowBean.FlowManager;

/**
 * Created by jaky on 2017/9/30 0030.
 */

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
//        FlowManager.init(new FlowConfig.Builder(this).build());
    }
}

package com.onyx.test.mytest.view.app;

import com.onyx.android.sdk.reader.ReaderBaseApp;
//import com.raizlabs.android.dbflow.config.DatabaseHolder;
//import com.raizlabs.android.dbflow.config.FlowConfig;
//import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by jaky on 2017/9/30 0030.
 */

public class MyApplication extends ReaderBaseApp {

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
//        FlowManager.init(new FlowConfig.Builder(this).build());
    }
}

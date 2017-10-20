package com.onyx.test.styletest;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by jaky on 2017/9/30 0030.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
         FlowManager.init(new FlowConfig.Builder(this).build());
    }
}

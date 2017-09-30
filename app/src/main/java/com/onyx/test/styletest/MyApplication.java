package com.onyx.test.styletest;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;
import com.squareup.leakcanary.LeakCanary;

/**
 * Created by jaky on 2017/9/30 0030.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        FlowManager.init(getApplicationContext());
    }
}

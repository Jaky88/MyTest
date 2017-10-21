package com.onyx.test.mytest;

import android.app.Application;

/**
 * Created by jaky on 2017/9/30 0030.
 */

public class MyApplication extends Application{

    @Override
    public void onCreate() {
        super.onCreate();
//        LeakCanary.install(this);
//         FlowManager.init(getApplicationContext());
    }
}

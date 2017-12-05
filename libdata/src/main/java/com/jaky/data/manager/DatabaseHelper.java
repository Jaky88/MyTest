package com.jaky.data.manager;

import android.content.Context;

import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created by Jack on 2017/12/3.
 */

public class DatabaseHelper {

    public DatabaseHelper(Context context) {
        FlowManager.init(new FlowConfig.Builder(context).build());
    }
}

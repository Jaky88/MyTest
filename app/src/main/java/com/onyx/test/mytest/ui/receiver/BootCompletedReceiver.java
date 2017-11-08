package com.onyx.test.mytest.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.mytest.model.AppConfig;
import com.onyx.test.mytest.model.bean.ConfigBean;

import java.io.File;

/**
 * Created by jaky on 2017/10/20 0020.
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {

        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d("=========", "======ACTION_BOOT_COMPLETED========");
            ConfigBean config = AppConfig.getConfig(context.getApplicationContext());
            if (config.isBootUpLastDocumentOpenChecked()) {
                processBootCompletedTask(context, config);
            }
        }
    }

    private void processBootCompletedTask(Context context, ConfigBean config) {
        File file = new File(config.getSlideFileName());
        Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(context, ViewDocumentUtils.autoSlideShowIntent(file, config.getSlideTime(), config.getSlideInterval()));
    }
}

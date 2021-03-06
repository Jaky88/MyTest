package com.onyx.test.styletest.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.styletest.config.AppConfig;
import com.onyx.test.styletest.config.ConfigBean;

import java.io.File;

/**
 * Created by jaky on 2017/10/20 0020.
 */

public class BootCompletedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        Log.d("=========", "======onReceive========");
        String action = intent.getAction();
        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            Log.d("=========", "======ACTION_BOOT_COMPLETED========");
            ConfigBean config = AppConfig.getConfig(context.getApplicationContext());
            if (config.isBootUpLastDocumentOpenChecked()) {
                Log.d("=========", "==============" + config.toString());
                File file = new File(config.getSlideFileName());
                Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
                in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ActivityUtil.startActivitySafely(context, ViewDocumentUtils.autoSlideShowIntent(file, Integer.MAX_VALUE, config.getSlideInterval()));
            }
        }
    }
}

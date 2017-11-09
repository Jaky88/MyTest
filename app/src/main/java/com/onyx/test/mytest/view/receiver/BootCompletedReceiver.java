package com.onyx.test.mytest.view.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.onyx.android.sdk.utils.ActivityUtil;
import com.onyx.android.sdk.utils.ViewDocumentUtils;
import com.onyx.test.mytest.model.manager.ConfigManager;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;

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
            ReaderSlideshowBean config = ConfigManager.getConfig(context.getApplicationContext()).getReaderSlideshowBean();
            if (config.isBootCompletedAtoTest()) {
                processBootCompletedTask(context, config);
            }
        }
    }

    private void processBootCompletedTask(Context context, ReaderSlideshowBean config) {
        File file = new File(config.getTestFilePath());
        Intent in = ViewDocumentUtils.viewActionIntentWithMimeType(file);
        in.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ActivityUtil.startActivitySafely(context, ViewDocumentUtils.autoSlideShowIntent(file, config.getSlideshowTotalPage(), config.getSlideshowInterval()));
    }
}

package com.onyx.test.mytest.model.constants;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by jaky on 2017/4/20.
 */

public class Constant {

    public static final String LOG_FILE_NAME = "mytest.log";
    public static final String CONFIG_FILE_NAME = "readerSlideshowBean.json";


    public static String getConfigPath(Context context) {
        return getDiskCacheDir(context) + File.separator + CONFIG_FILE_NAME;
    }

    public static String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }
}

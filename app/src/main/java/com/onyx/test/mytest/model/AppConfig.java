package com.onyx.test.mytest.model;

import android.content.Context;
import android.util.Log;

import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.utils.FileUtil;
import com.onyx.test.mytest.model.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jaky on 2017/8/16.
 */

public class AppConfig {

    private static ReaderSlideshowBean config;
    private static final String TAG = AppConfig.class.getSimpleName();

    public static ReaderSlideshowBean getConfig(Context context) {
        if (config == null) {
            config = readConfig(context);
        }
        return config;
    }

    public static boolean saveConfig(Context context) {
        String json = JsonUtil.objectToJson(getConfig(context));
        if (StringUtils.isBlank(json)) {
            return false;
        }
        Log.d(TAG, "=========saveConfig=====" + json);
        return FileUtil.saveContentToFile(json, Constant.getConfigPath(context));
    }

    private static ReaderSlideshowBean readConfig(Context context) {
        String content = FileUtil.readContentFromFile(Constant.getConfigPath(context));
        if (StringUtils.isBlank(content)) {
            return new ReaderSlideshowBean();
        }

        return JsonUtil.jsonToObject(content, ReaderSlideshowBean.class);
    }

}

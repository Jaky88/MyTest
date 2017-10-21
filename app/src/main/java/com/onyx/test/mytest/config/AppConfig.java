package com.onyx.test.mytest.config;

import android.content.Context;
import android.util.Log;

import com.onyx.test.mytest.utils.FileUtil;
import com.onyx.test.mytest.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jaky on 2017/8/16.
 */

public class AppConfig {

    private static ConfigBean config;
    private static final String TAG = AppConfig.class.getSimpleName();

    public static ConfigBean getConfig(Context context) {
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

    private static ConfigBean readConfig(Context context) {
        String content = FileUtil.readContentFromFile(Constant.getConfigPath(context));
        if (StringUtils.isBlank(content)) {
            return new ConfigBean();
        }

        return JsonUtil.jsonToObject(content, ConfigBean.class);
    }

}

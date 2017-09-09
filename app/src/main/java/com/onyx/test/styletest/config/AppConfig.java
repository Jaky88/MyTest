package com.onyx.test.styletest.config;

import android.content.Context;

import com.onyx.test.styletest.utils.FileUtil;
import com.onyx.test.styletest.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jaky on 2017/8/16.
 */

public class AppConfig {

    private static ConfigBean config;

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

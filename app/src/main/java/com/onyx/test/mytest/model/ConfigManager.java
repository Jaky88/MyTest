package com.onyx.test.mytest.model;

import android.content.Context;
import android.util.Log;

import com.onyx.test.mytest.model.bean.ConfigBean;
import com.onyx.test.mytest.model.bean.ReaderSlideshowBean;
import com.onyx.test.mytest.model.utils.FileUtil;
import com.onyx.test.mytest.model.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by jaky on 2017/8/16.
 */

public class ConfigManager {

    private static final String TAG = ConfigManager.class.getSimpleName();
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
        Log.d(TAG, "=========saveConfig=====" + json);
        return FileUtil.saveContentToFile(json, Constant.getConfigPath(context));
    }

    private static ConfigBean readConfig(Context context) {
        String content = FileUtil.readContentFromFile(Constant.getConfigPath(context));
        if (StringUtils.isBlank(content)) {
            return new ConfigBean(new ReaderSlideshowBean());
        }
        ConfigBean configBean = JsonUtil.jsonToObject(content, ConfigBean.class);
        if(configBean != null){
            if(configBean.getReaderSlideshowBean() == null) {
                configBean.setReaderSlideshowBean(new ReaderSlideshowBean());
            }
            return configBean;
        }
        configBean = new ConfigBean(new ReaderSlideshowBean());
        return configBean;
    }
}

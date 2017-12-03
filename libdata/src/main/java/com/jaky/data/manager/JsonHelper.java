package com.jaky.data.manager;

import android.content.Context;
import android.os.Environment;

import com.jaky.data.bean.AppConfig;
import com.jaky.utils.FileUtil;
import com.jaky.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Jack on 2017/12/3.
 */

public class JsonHelper {

    private static String BEAN_APPCONFIG = AppConfig.class.getSimpleName();
    private Map<String, Object> beanMap = new ConcurrentHashMap<>();
    private String savePath = Environment.getDataDirectory().getAbsolutePath();
    private AppConfig appConfig;
    private Context context;

    public JsonHelper(Context context) {
        this.context = context;
        getSavePath();
        initBeanMap();

    }

    private void initBeanMap() {
        beanMap.put(BEAN_APPCONFIG, null);
    }

    public void loadBean() {
        for (String key : beanMap.keySet()) {
            if (BEAN_APPCONFIG.equals(key)) {
                appConfig = (AppConfig) beanMap.get(key);
                if (appConfig == null) {
                    appConfig = loadBeanInternal(savePath, new AppConfig());
                    beanMap.put(BEAN_APPCONFIG, appConfig);
                }
            }
        }
    }

    private <Bean> Bean loadBeanInternal(String savePath, Bean defBean) {
        String content = FileUtil.readContentFromFile(savePath);
        if (StringUtils.isBlank(content)) {
            return defBean;
        }
        Bean bean = (Bean) JsonUtil.jsonToObject(content, defBean.getClass());
        if (bean != null) {
            return bean;
        }
        return defBean;
    }

    public <Bean> boolean saveBean(Bean bean) {
        return saveBean(bean, getSavePath());
    }

    public <Bean> boolean saveBean(Bean bean, String savePath) {
        String json = JsonUtil.objectToJson(bean);
        if (StringUtils.isBlank(json)) {
            return false;
        }
        return FileUtil.saveContentToFile(json, savePath);
    }


    public AppConfig getAppConfig() {
        return appConfig;
    }

    public void setAppConfig(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String getSavePath() {
        if (StringUtils.isEmpty(savePath)) {
            savePath = context.getCacheDir().getAbsolutePath();
        }
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }
}

package com.jaky.data.manager;

import android.content.Context;
import android.os.Environment;

import com.jaky.data.bean.AppConfig;
import com.jaky.utils.FileUtil;
import com.jaky.utils.JsonUtil;

import org.apache.commons.lang3.StringUtils;

import java.io.File;

/**
 * Created by Jack on 2017/12/3.
 */

public class JsonHelper {

    private static final String BEAN_APPCONFIG = AppConfig.class.getSimpleName();
    private String saveDir = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
    private static final String FILE_EXTENSION = ".json";


    private AppConfig appConfig;
    private Context context;

    public JsonHelper(Context context) {
        this.context = context;
    }

    public void loadBean() {
        appConfig = loadBean(getSavePath(BEAN_APPCONFIG), new AppConfig(saveDir));
    }

    public <Bean> Bean loadBean(String fileName, Bean defBean) {
        String content = FileUtil.readContentFromFile(getSavePath(fileName));
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
        return saveBean(bean, getSaveDir());
    }

    public <Bean> boolean saveBean(Bean bean, String fileName) {
        String json = JsonUtil.objectToJson(bean);
        if (StringUtils.isBlank(json)) {
            return false;
        }
        return FileUtil.saveContentToFile(json, getSavePath(fileName));
    }


    public AppConfig getAppConfig() {
        return appConfig;
    }


    public String getSaveDir() {
        if (StringUtils.isEmpty(saveDir)) {
            saveDir = context.getCacheDir().getAbsolutePath() + File.separator;
        }
        return saveDir;
    }

    public void setSaveDir(String saveDir) {
        if(!saveDir.endsWith("/")) {
            saveDir += File.separator;
        }
        this.saveDir = saveDir;
    }

    private String getSavePath(String fileName) {
        return getSaveDir() + fileName + FILE_EXTENSION;
    }

}

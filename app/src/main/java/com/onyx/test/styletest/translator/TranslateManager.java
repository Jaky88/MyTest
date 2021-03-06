package com.onyx.test.styletest.translator;

import android.util.Log;

import com.onyx.test.styletest.translator.config.Constants;
import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.config.TranslatePlatform;
import com.onyx.test.styletest.translator.core.XMLTranslator;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Created by lion on 2016/10/28.
 * 翻译管理器，统一调度管理翻译器
 */
public class TranslateManager {

    //翻译扫描路径
    private String translatePath = "";
    private boolean translateAllXml = false;
    private TranslatePlatform platform;

    private static final String XML_FILE_NAME = "strings.xml";

    private static TranslateManager instance;

    public static TranslateManager getInstance() {
        if (instance == null) {
            instance = new TranslateManager();
        }
        return instance;
    }

    public void init(String translatePath, boolean translateAllXml, TranslatePlatform platform) {
        this.translatePath = translatePath;
        this.translateAllXml = translateAllXml;
        this.platform = platform;
    }

    public void init(String translatePath, boolean translateAllXml) {
        this.translatePath = translatePath;
        this.translateAllXml = translateAllXml;
        this.platform = TranslatePlatform.GOOGLE;
    }

    public void translate(Language src, Language dest) {
        if (translateAllXml) {
            File file = new File(translatePath);
            File[] files = file.listFiles();
            if (files != null) {
                for (File temp : files) {
                    if (temp.getName().endsWith(".xml")) {
                        innerTranslate(temp.getAbsolutePath(), src, dest);
                    }
                }
            }
        } else {
            innerTranslate(translatePath + File.separator + XML_FILE_NAME, src, dest);
        }
    }

    private void innerTranslate(String path, Language src, Language target) {
        Log.d("=========","=====innerTranslate=================");
        Class<? extends XMLTranslator> translatorClass = Constants.platformConfig.get(platform);
        try {
            Constructor<? extends XMLTranslator> constructor = translatorClass.getDeclaredConstructor(String.class);
            XMLTranslator translator = constructor.newInstance(path);
            if (target == null) {
                List<Language> languages = translator.getSupportLanguage();
                for (Language language : languages) {
                    if (language != src) {
                        translator.translate(src, language);
                    }
                }
            } else {
                translator.translate(src, target);
            }
//        } catch (NoSuchMethodException | IllegalAccessException |
//                InstantiationException | InvocationTargetException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void translateAll(Language src) {
        translate(src, null);
    }

    public void setTranslatePath(String translatePath) {
        this.translatePath = translatePath;
    }

    public String getTranslatePath() {
        return translatePath;
    }

    public void setTranslateAllXml(boolean translateAllXml) {
        this.translateAllXml = translateAllXml;
    }

    public boolean isTranslateAllXml() {
        return translateAllXml;
    }

    public TranslatePlatform getPlatform() {
        return platform;
    }

    public void setPlatform(TranslatePlatform platform) {
        this.platform = platform;
    }
}

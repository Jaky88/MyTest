package com.onyx.test.styletest.translator.core;

import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.entity.Params;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by lion on 2016/9/23.
 * 翻译公共接口，以后拓展就可以不局限为xml翻译
 */
public interface Translator {

    void translate(Language src, Language target);

    String onTranslateFinished(String result);

    List<Language> getSupportLanguage();

    public Call<ResponseBody> getNetTranslate(Params param);

    Params initParams(String content, Language src, Language target);

    String handleJsonString(String result);

    String handleXMLString(String result);
}

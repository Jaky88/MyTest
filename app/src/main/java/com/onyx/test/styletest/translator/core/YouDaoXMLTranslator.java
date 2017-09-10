package com.onyx.test.styletest.translator.core;

import com.alibaba.fastjson.JSON;
import com.onyx.test.styletest.translator.config.Constants;
import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.entity.Params;
import com.onyx.test.styletest.translator.entity.YouDaoParams;
import com.onyx.test.styletest.translator.network.RetrofitWrapper;
import com.onyx.test.styletest.translator.network.TranslateService;
import com.onyx.test.styletest.translator.entity.YouDaoTranslateResult;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * Created by lion on 2016/10/28.
 */
public class YouDaoXMLTranslator extends XMLTranslator {

    private static final List<Language> languages = new ArrayList<>();

    static {
        languages.add(Language.ZH_CN);
        languages.add(Language.EN);
    }

    public YouDaoXMLTranslator(String filePath) {
        super(filePath);
    }

    @Override
    public String onTranslateFinished(String result) {
        return handleJsonString(result);
    }

    @Override
    public List<Language> getSupportLanguage() {
        return languages;
    }

    @Override
    public Call<ResponseBody> getNetTranslate(Params param) {
        YouDaoParams params = (YouDaoParams) param;
        TranslateService sevice = (TranslateService) RetrofitWrapper.getInstance("Baidu").create(TranslateService.class);
        return sevice.getYouDaoTranslation(
                params.getKeyfrom(), params.getKey(),
                params.getType(), params.getDoctype(),
                params.getVersion(), params.getEncode());
    }

    @Override
    public Params initParams(String content, Language src, Language target) {
        String encode = content;
        try {
            encode = URLEncoder.encode(content, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } finally {
            return new YouDaoParams("FishMatser", Constants.YOUDAO_API_KEY,
                    "data", "json", "1.1", encode);
        }
    }

    @Override
    public String handleJsonString(String result) {
        YouDaoTranslateResult json = JSON.parseObject(result, YouDaoTranslateResult.class);
        if (json != null && json.getTranslation() != null &&
                json.getTranslation().size() > 0) {
            return json.getTranslation().get(0);
        }

        return null;
    }

    @Override
    public String handleXMLString(String result) {
        return null;
    }
}

package com.onyx.test.styletest.translator.core;

import com.alibaba.fastjson.JSON;
import com.onyx.test.styletest.translator.config.Constants;
import com.onyx.test.styletest.translator.config.Language;
import com.onyx.test.styletest.translator.entity.BaiduParams;
import com.onyx.test.styletest.translator.entity.BaiduTranslateResult;
import com.onyx.test.styletest.translator.entity.Params;
import com.onyx.test.styletest.translator.network.RetrofitWrapper;
import com.onyx.test.styletest.translator.network.TranslateService;
import com.onyx.test.styletest.translator.utils.MD5Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import okhttp3.ResponseBody;
import retrofit2.Call;

public class BaiduXMLTranslator extends XMLTranslator {

    private static final List<Language> languages = new ArrayList<>();

    static {
        languages.add(Language.ZH_CN);
        languages.add(Language.ZH_TW);
        languages.add(Language.EN);
        languages.add(Language.JA);
    }

    public BaiduXMLTranslator(String filePath) {
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
        BaiduParams params = (BaiduParams) param;
        TranslateService sevice = (TranslateService) RetrofitWrapper.getInstance("Baidu").create(TranslateService.class);
        return sevice.getBaiduTranslation(
                params.getShortLanguage(), params.getTargetLanguage(),
                params.getBaiduAppId(), params.getRandomInt() + "",
                params.getEncode(), params.getSign());
    }

    @Override
    public Params initParams(String content, Language src, Language target) {
        Random random = new Random();
        int randomInt = random.nextInt();
        return new BaiduParams(getShortLanguage(src), getShortLanguage(target),
                Constants.BAIDU_APP_ID, randomInt, content,
                sign(content, randomInt));
    }

    private static String getShortLanguage(Language language) {
        if (language.getValue().equals("zh-cn")) {
            return "zh";
        } else if (language.getValue().equals("zh-tw")) {
            return "cht";
        } else if (language.getValue().equals("ja")) {
            return "jp";
        } else {
            return language.getValue();
        }
    }

    private static String sign(String content, int randomInt) {
        String signParams = Constants.BAIDU_APP_ID + content + randomInt + Constants.BAIDU_APP_SECRET;
        return MD5Utils.getMD5Code(signParams);
    }

    @Override
    public String handleJsonString(String result) {
        BaiduTranslateResult jsonResult = JSON.parseObject(result, BaiduTranslateResult.class);
        if (jsonResult != null &&
                jsonResult.getTrans_result() != null &&
                jsonResult.getTrans_result().get(0) != null) {
            return jsonResult.getTrans_result().get(0).getDst();
        }
        return null;
    }

    @Override
    public String handleXMLString(String result) {
        return null;
    }

}

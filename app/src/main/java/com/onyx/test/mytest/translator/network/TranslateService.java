package com.onyx.test.mytest.translator.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jaky on 2017/9/9 0009.
 */

public interface TranslateService {
    @GET("api/trans/vip/translate")
    Call<ResponseBody> getBaiduTranslation(@Query("from") String srcLanguage,
                                          @Query("to") String targetLanguage,
                                          @Query("appid") String appId,
                                          @Query("salt") String randomInt,
                                          @Query("q") String encode,
                                          @Query("sign") String sign);

    @GET("language/translate/v2")
    Call<ResponseBody> getGoogleTranslation(@Query("key") String key,
                                      @Query("source") String srcLanguage,
                                      @Query("target") String targetLanguage,
                                      @Query("q") String encode);

    @GET("openapi.do")
    Call<ResponseBody> getYouDaoTranslation(@Query("keyfrom") String keyfrom,
                                      @Query("key") String key,
                                      @Query("type") String type,
                                      @Query("doctype") String doctype,
                                      @Query("version") String version,
                                      @Query("q") String encode);

}

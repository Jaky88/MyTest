package com.onyx.test.styletest.translator.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jaky on 2017/9/9 0009.
 */

public interface TranslateService {
    @GET("translate")
    Call<String> getTop250(
            @Query("src") int start,
            @Query("dest") int count);
}

package com.onyx.test.styletest.translator.network;

import android.content.Context;

import com.onyx.test.styletest.translator.config.Constants;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitWrapper {
    private static RetrofitWrapper instance;
    private Context mContext;
    private Retrofit retrofit;

    private RetrofitWrapper(String platform) {
        retrofit = new Retrofit.Builder().baseUrl(Constants.getBaseUrl(platform))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static RetrofitWrapper getInstance(String platform) {
        if (instance == null) {
            synchronized (RetrofitWrapper.class) {
                if (instance == null) {
                    instance = new RetrofitWrapper(platform);
                }
            }
        }
        return instance;
    }

    public <T> T create(final Class<T> service) {
        return retrofit.create(service);
    }

}


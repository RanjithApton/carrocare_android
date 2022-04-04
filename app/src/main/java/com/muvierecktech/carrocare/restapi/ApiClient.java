package com.muvierecktech.carrocare.restapi;


import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //public static final String BASE_URL = "https://www.carrocare.in/Android_API/";
    public static final String BASE_URL = "https://www.carrocare.in/test/Android_API/api-1.2.6/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5,TimeUnit.MINUTES)
                    .addInterceptor(new BasicAuthInterceptor("RegalCarWashAdmin","TechAdmin@Muviereck"))
                    //.addInterceptor(new BasicAuthInterceptor("carroadmin","Muthu06*"))
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)

                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }
        return retrofit;
    }

}

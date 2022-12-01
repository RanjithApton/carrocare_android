package com.muvierecktech.carrocare.restapi;


import androidx.annotation.NonNull;

import com.muvierecktech.carrocare.common.Constant;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    public static final String BASE_URL = "https://www.carrocare.in/Android_API/api-1.2.10/";
    //public static final String BASE_URL = "https://www.carrocare.in/test/Android_API/api-1.2.10/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(httpLoggingInterceptor)
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5,TimeUnit.MINUTES)
                    //.addInterceptor(new BasicAuthInterceptor("RegalCarWashAdmin","TechAdmin@Muviereck"))
                    .addInterceptor(new Interceptor() {
                        @NonNull
                        @Override
                        public Response intercept(@NonNull Chain chain) throws IOException {
                            Request authenticatedRequest = chain.request().newBuilder()
                                    .header("Accept","application/json")
                                    .header("Authorization", "Bearer " + createJWT("RLAG3QAILVRTIS1Q694JVUK1S6AA", "CarrocareAuth"))
                                    .build();
                            return chain.proceed(authenticatedRequest);
                        }
                    })
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

    public static String createJWT(String issuer, String subject) {
        try {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
            long nowMillis = System.currentTimeMillis();
            Date now = new Date(nowMillis);
            byte[] apiKeySecretBytes = Constant.JWT_KEY.getBytes();
            Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
            JwtBuilder builder = Jwts.builder()
                    .setIssuedAt(now)
                    .setSubject(subject)
                    .setIssuer(issuer)
                    .signWith(signatureAlgorithm, signingKey);

            return builder.compact();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}

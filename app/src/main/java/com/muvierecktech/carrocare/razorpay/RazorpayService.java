package com.muvierecktech.carrocare.razorpay;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface RazorpayService {

    @Headers("Content-Type: application/json")
    @POST("subscriptions")
    Call<SubscriptionResponse> createSubscription(@Body SubscriptionRequest subscriptionRequest);

    @Headers("Content-Type: application/json")
    @POST("plans")
    Call<PlanResponse> createPlan(@Body PlanRequest planRequest);
}

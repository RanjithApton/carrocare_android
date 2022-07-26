package com.muvierecktech.carrocare.restapi;

import com.google.gson.JsonObject;
import com.muvierecktech.carrocare.model.ApartmentList;
import com.muvierecktech.carrocare.model.DoorStepCarWash;
import com.muvierecktech.carrocare.model.LoginDetails;
import com.muvierecktech.carrocare.model.MakeModelList;
import com.muvierecktech.carrocare.model.OneTimeWashCheckout;
import com.muvierecktech.carrocare.model.OrdersList;
import com.muvierecktech.carrocare.model.ParkingareaList;
import com.muvierecktech.carrocare.model.ServicePriceList;
import com.muvierecktech.carrocare.model.SettingsList;
import com.muvierecktech.carrocare.model.SliderList;
import com.muvierecktech.carrocare.model.VehicleDetails;
import com.muvierecktech.carrocare.model.VehicleExtraList;
import com.muvierecktech.carrocare.model.VehicleWashList;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiInterface {
    @FormUrlEncoded
    @POST("login.php")
    Call<LoginDetails> login(@Field("email") String email, @Field("password") String password);

    @FormUrlEncoded
    @POST("site_dtl.php")
    Call<SettingsList> getSettings(@Field("app_update") String app_update);

    @FormUrlEncoded
    @POST("login-verify.php")
    Call<JsonObject> loginverify(@Field("firebase_instance_id") String firebase_instance_id,
                                 @Field("device_name") String device_name,
                                 @Field("device_model") String device_model,
                                 @Field("os_version") String os_version,
                                 @Field("email") String email);

    @FormUrlEncoded
    @POST("send_login_otp.php")
    Call<JsonObject> loginotp(@Field("mobile") String mobile,
                              @Field("name") String name,
                              @Field("email") String email);

    @FormUrlEncoded
    @POST("register.php")
    Call<LoginDetails> register(@Field("mobile") String mobile,
                                @Field("password") String password,
                                @Field("name") String name,
                                @Field("email") String email,
                                @Field("device_id") String device_id,
                                @Field("device_name") String device_name,
                                @Field("device_model") String device_model,
                                @Field("os_version") String os_version
    );
    @FormUrlEncoded
    @POST("forgot_password_otp.php")
    Call<JsonObject> forgot(@Field("mobile") String mobile);

    @FormUrlEncoded
    @POST("forgot_password_update.php")
    Call<JsonObject> forgotupdate(@Field("mobile") String mobile,@Field("password") String password);

    @GET("slider.php")
    Call<SliderList> slider();

    @FormUrlEncoded
    @POST("services_price.php")
    Call<ServicePriceList> Service(@Field("service") String mobile);

    @FormUrlEncoded
    @POST("disinfection.php")
    Call<ServicePriceList> ServiceDisinsfection(@Field("action") String action);

    @FormUrlEncoded
    @POST("profile_details.php")
    Call<LoginDetails> profile(@Field("token") String token,@Field("customer_id") String customer_id);

    @FormUrlEncoded
    @POST("profile_update.php")
    Call<JsonObject> profileupdate(@Field("token") String token,@Field("customer_id") String customer_id,@Field("apartment_name") String apartmant_name,
                                   @Field("apartment_building") String apartmant_building,@Field("flat_no") String flat_no,
                                   @Field("address") String address,@Field("latitude") String latitude,
                                   @Field("longitude") String longitude);

    @GET("apartment_list.php")
    Call<ApartmentList> apartmentList();

    @GET("parking_area.php")
    Call<ParkingareaList> parkingareaList();

    @FormUrlEncoded
    @POST("vehicle_details.php")
    Call<VehicleDetails> vehicledetails(@Field("customer_id") String customer_id, @Field("token") String token, @Field("category") String category );

    @FormUrlEncoded
    @POST("vehicle_details.php")
    Call<VehicleDetails> myvehicledetails(@Field("customer_id") String customer_id, @Field("token") String token);


    @FormUrlEncoded
    @POST("service_customer_form.php")
    Call<JsonObject> serviceForm(@Field("name") String name, @Field("mobile") String mobile,
                                 @Field("email") String email , @Field("address_line") String address_line ,
                                 @Field("landmark") String landmark , @Field("city") String city ,
                                 @Field("state") String state , @Field("country") String country ,
                                 @Field("pincode") String pincode, @Field("vehicle_type") String vehicle_type,
                                 @Field("make") String make, @Field("model") String model,
                                 @Field("category") String category, @Field("form") String form);

    @FormUrlEncoded
    @POST("car_make_model.php")
    Call<MakeModelList> Makemodel(@Field("vehicle_category") String vehicle_category);

    @FormUrlEncoded
    @POST("vehicle_add.php")
    Call<JsonObject> vechileAdd(@Field("vehicle_type") String vehicle_type, @Field("category") String category,
                                @Field("make") String make , @Field("model") String model,
                                @Field("vehicle_no") String vehicle_no , @Field("color") String color,
                                @Field("apartment_name") String apartment_name, @Field("parking_lot_no") String parking_lot_no,
                                @Field("parking_area") String parking_area, @Field("preferred_schedule") String preferred_schedule,
                                @Field("preferred_time") String preferred_time, @Field("customer_id") String customer_id,
                                @Field("token") String token);
    @FormUrlEncoded
    @POST("order_list.php")
    Call<OrdersList> orderlist(@Field("token") String token, @Field("customer_id") String customer_id);

    @FormUrlEncoded
    @POST("onetime_wash_checkout.php")
    Call<OneTimeWashCheckout> onetime_wash(@Field("customer_id") String customer_id,
                                           @Field("pack_amount") String pack_amount,
                                           @Field("vehicle_id") String vehicle_id,
                                           @Field("service_type") String service_type);

    @FormUrlEncoded
    @POST("contact_form.php")
    Call<JsonObject> conatctFrom(@Field("subject") String subject,@Field("message") String message,@Field("name") String name,
                                 @Field("email") String email,@Field("mobile") String mobile);
    @FormUrlEncoded
    @POST("validate_checkout.php")
    Call<JsonObject> checkValidation(@Field("customer_id") String customer_id, @Field("vehicle_id") String vehicle_id, @Field("service_type") String service_type);
    @FormUrlEncoded
    @POST("create_subscription.php")
    Call<JsonObject> getSubcription(@Field("plan_id") String plan_id, @Field("customer_id") String customer_id,
                                    @Field("vehicle_id") String vehicle_id, @Field("token") String token, @Field("no_of_count") String no_of_count);
    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> saveOrder(@Field("action") String action, @Field("order_id") String order_id,
                               @Field("customer_id") String customer_id, @Field("token") String token, @Field("vehicle_id") String vehicle_id,
                               @Field("service_type") String service_type, @Field("tot_amt") String tot_amt,@Field("plan_id") String plan_id,
                               @Field("subscription_id") String subscription_id);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> createOrderId(@Field("action") String action,
                                   @Field("amount") String amount
    );

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> saveOrderOneTime(@Field("action") String action,
                                      @Field("order_id") String order_id,
                                      @Field("rzp_order_id") String rzp_order_id,
                                      @Field("customer_id") String customer_id,
                                      @Field("token") String token,
                                      @Field("pack_type") String pack_type,
                                      @Field("pack_amount") String pack_amount,
                                      @Field("vehicle_id") String vehicle_id,
                                      @Field("service_type") String service_type,
                                      @Field("sub_tot_amt") String sub_tot_amt,
                                      @Field("gst") String gst,
                                      @Field("gst_amount") String gst_amount,
                                      @Field("tot_amt") String tot_amt,
                                      @Field("schedule_date") String schedule_date,
                                      @Field("schedule_time") String schedule_time);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> saveWashOrderOneTime(@Field("action") String action,
                                          @Field("payment_id") String payment_id,
                                          @Field("rzp_order_id") String rzp_order_id,
                                          @Field("customer_id") String customer_id,
                                          @Field("token") String token,
                                          @Field("pack_amount") String pack_amount,
                                          @Field("vehicle_id") String vehicle_id,
                                          @Field("paid_months") String paid_months,
                                          @Field("fine_amount") String fine_amount,
                                          @Field("sub_tot_amt") String sub_tot_amt,
                                          @Field("gst") String gst,
                                          @Field("gst_amount") String gst_amount,
                                          @Field("tot_amt") String tot_amt,
                                          @Field("service_type") String service_type);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> saveAddOnOrderOneTime(@Field("action") String action,
                                           @Field("payment_id") String payment_id,
                                           @Field("rzp_order_id") String rzp_order_id,
                                           @Field("customer_id") String customer_id,
                                           @Field("token") String token,
                                           @Field("pack_amount") String pack_amount,
                                           @Field("vehicle_id") String vehicle_id,
                                           @Field("paid_months") String paid_months,
                                           @Field("fine_amount") String fine_amount,
                                           @Field("sub_tot_amt") String sub_tot_amt,
                                           @Field("gst") String gst,
                                           @Field("gst_amount") String gst_amount,
                                           @Field("tot_amt") String tot_amt,
                                           @Field("service_type") String service_type,
                                           @Field("schedule_date") String schedule_date,
                                           @Field("schedule_time") String schedule_time);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> tempOrderOneTime(@Field("action") String action,
                                      @Field("rzp_order_id") String rzp_order_id,
                                      @Field("customer_id") String customer_id,
                                      @Field("token") String token,
                                      @Field("pack_type") String pack_type,
                                      @Field("pack_amount") String pack_amount,
                                      @Field("vehicle_id") String vehicle_id,
                                      @Field("service_type") String service_type,
                                      @Field("sub_tot_amt") String sub_tot_amt,
                                      @Field("gst") String gst,
                                      @Field("gst_amount") String gst_amount,
                                      @Field("tot_amt") String tot_amt,
                                      @Field("schedule_date") String schedule_date,
                                      @Field("schedule_time") String schedule_time,
                                      @Field("success_action") String success_action);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> tempWashOrderOneTime(@Field("action") String action,
                                          @Field("rzp_order_id") String rzp_order_id,
                                          @Field("customer_id") String customer_id,
                                          @Field("token") String token,
                                          @Field("pack_amount") String pack_amount,
                                          @Field("vehicle_id") String vehicle_id,
                                          @Field("paid_months") String paid_months,
                                          @Field("fine_amount") String fine_amount,
                                          @Field("sub_tot_amt") String sub_tot_amt,
                                          @Field("gst") String gst,
                                          @Field("gst_amount") String gst_amount,
                                          @Field("tot_amt") String tot_amt,
                                          @Field("service_type") String service_type,
                                          @Field("success_action") String success_action);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> tempAddOnOrderOneTime(@Field("action") String action,
                                           @Field("rzp_order_id") String rzp_order_id,
                                           @Field("customer_id") String customer_id,
                                           @Field("token") String token,
                                           @Field("pack_amount") String pack_amount,
                                           @Field("vehicle_id") String vehicle_id,
                                           @Field("paid_months") String paid_months,
                                           @Field("fine_amount") String fine_amount,
                                           @Field("sub_tot_amt") String sub_tot_amt,
                                           @Field("gst") String gst,
                                           @Field("gst_amount") String gst_amount,
                                           @Field("tot_amt") String tot_amt,
                                           @Field("service_type") String service_type,
                                           @Field("schedule_date") String schedule_date,
                                           @Field("schedule_time") String schedule_time,
                                           @Field("success_action") String success_action);

    @FormUrlEncoded
    @POST("help_and_support.php")
    Call<JsonObject> helpandsupport(@Field("type") String type, @Field("question") String question,
                                    @Field("customer_id") String customer_id, @Field("token") String token);
    @FormUrlEncoded
    @POST("customer_wash_details.php")
    Call<VehicleWashList> washDetails(@Field("customer_id") String customer_id, @Field("token") String token,@Field("vehicle_id") String vehicle_id,@Field("order_id") String order_id);

    @FormUrlEncoded
    @POST("internal_clean_schedule.php")
    Call<JsonObject> interSchedule(@Field("customer_id") String customer_id, @Field("token") String token,@Field("vehicle_id") String vehicle_id,@Field("order_id") String order_id
            ,@Field("schedule_date") String schedule_date,@Field("schedule_time") String schedule_time,@Field("comment_box") String comment_box,@Field("date_type") String date_type
            ,@Field("id") String id);

    @FormUrlEncoded
    @POST("internal_clean_schedule_new.php")
    Call<JsonObject> interScheduleNew(@Field("customer_id") String customer_id,
                                      @Field("token") String token,
                                      @Field("vehicle_id") String vehicle_id,
                                      @Field("order_id") String order_id,
                                      @Field("schedule_date1") String schedule_date1,
                                      @Field("schedule_time1") String schedule_time1,
                                      @Field("comment_box1") String comment_box1,
                                      @Field("schedule_date2") String schedule_date2,
                                      @Field("schedule_time2") String schedule_time2,
                                      @Field("comment_box2") String comment_box2,
                                      @Field("id") String id
    );

    @FormUrlEncoded
    @POST("extrainterior.php")
    Call<VehicleExtraList> extraDetails(@Field("customer_id") String customer_id, @Field("token") String token, @Field("vehicle_id") String vehicle_id);

    @FormUrlEncoded
    @POST("cancel_subscription.php")
    Call<JsonObject> cancelSubs(@Field("token") String token,@Field("vehicle_id") String vehicle_id,@Field("order_id") String order_id);

    @GET("razorpay_mode.php")
    Call<JsonObject> getMode();

    @FormUrlEncoded
    @POST("vehicle_add.php")
    Call<JsonObject> vechileAddDoorstep(@Field("vehicle_type") String vehicle_type,
                                        @Field("category") String category,
                                        @Field("make") String make ,
                                        @Field("model") String model,
                                        @Field("vehicle_no") String vehicle_no ,
                                        @Field("color") String color,
//                                        @Field("address") String address,
//                                        @Field("latitude") String latitude,
//                                        @Field("longitude") String longitude,
                                        @Field("customer_id") String customer_id,
                                        @Field("token") String token);

    @FormUrlEncoded
    @POST("doorstep_details.php")
    Call<DoorStepCarWash> doorStepService(@Field("action") String action, @Field("type") String type);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> tempOrderDoorStep(@Field("action") String action,
                                       @Field("rzp_order_id") String rzp_order_id,
                                       @Field("customer_id") String customer_id,
                                       @Field("token") String token,
                                       @Field("pack_type") String pack_type,
                                       @Field("pack_amount") String pack_amount,
                                       @Field("vehicle_id") String vehicle_id,
                                       @Field("service_type") String service_type,
                                       @Field("sub_tot_amt") String sub_tot_amt,
                                       @Field("gst") String gst,
                                       @Field("gst_amount") String gst_amount,
                                       @Field("tot_amt") String tot_amt,
                                       @Field("schedule_date") String schedule_date,
                                       @Field("schedule_time") String schedule_time,
                                       @Field("success_action") String success_action,
                                       @Field("address") String address,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> saveOrderDoorStep(@Field("action") String action,
                                       @Field("order_id") String order_id,
                                       @Field("rzp_order_id") String rzp_order_id,
                                       @Field("customer_id") String customer_id,
                                       @Field("token") String token,
                                       @Field("pack_type") String pack_type,
                                       @Field("pack_amount") String pack_amount,
                                       @Field("vehicle_id") String vehicle_id,
                                       @Field("service_type") String service_type,
                                       @Field("tot_amt") String tot_amt,
                                       @Field("gst") String gst,
                                       @Field("gst_amount") String gst_amount,
                                       @Field("sub_tot_amt") String sub_tot_amt,
                                       @Field("schedule_date") String schedule_date,
                                       @Field("schedule_time") String schedule_time,
                                       @Field("address") String address,
                                       @Field("latitude") String latitude,
                                       @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("save_order.php")
    Call<JsonObject> saveOrderDoorStepCOD(@Field("action") String action,
                                          @Field("customer_id") String customer_id,
                                          @Field("token") String token,
                                          @Field("pack_type") String pack_type,
                                          @Field("pack_amount") String pack_amount,
                                          @Field("vehicle_id") String vehicle_id,
                                          @Field("service_type") String service_type,
                                          @Field("sub_tot_amt") String sub_tot_amt,
                                          @Field("gst") String gst,
                                          @Field("gst_amount") String gst_amount,
                                          @Field("tot_amt") String tot_amt,
                                          @Field("schedule_date") String schedule_date,
                                          @Field("schedule_time") String schedule_time,
                                          @Field("address") String address,
                                          @Field("latitude") String latitude,
                                          @Field("longitude") String longitude);

    @FormUrlEncoded
    @POST("cancel_cod_order.php")
    Call<JsonObject> cancelCodOrder(@Field("order_id") String order_id,
                                    @Field("customer_id") String customer_id,
                                    @Field("reason") String reason);


}

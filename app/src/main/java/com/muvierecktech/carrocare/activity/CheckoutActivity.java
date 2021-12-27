package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.content.Intent;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.common.SessionManager;
import com.muvierecktech.carrocare.databinding.ActivityCheckoutBinding;
import com.razorpay.Checkout;
import com.razorpay.PaymentData;
import com.razorpay.PaymentResultWithDataListener;

import org.json.JSONObject;

import java.util.HashMap;

public class CheckoutActivity extends AppCompatActivity implements PaymentResultWithDataListener {
    ActivityCheckoutBinding binding;
    String pacakagetype,vehicletype,subscriptiontype,servicetype,vehicleid,customerid,razorpay_customer_id,orderid,payment_type,carprice;
    String custmob,custemail;
    SessionManager sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_checkout);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_checkout);
        sessionManager= new SessionManager(this);
        HashMap<String,String> hashMap = sessionManager.getUserDetails();
        custemail = hashMap.get(SessionManager.KEY_USEREMAIL);
        custmob = hashMap.get(SessionManager.KEY_USERMOBILE);

        Intent intent = getIntent();
        pacakagetype = intent.getStringExtra("package_type");
        vehicletype = intent.getStringExtra("vehicle_type");
        subscriptiontype = intent.getStringExtra("subscription_type");
        servicetype = intent.getStringExtra("service_type");
        vehicleid = intent.getStringExtra("vehicle_id");
        customerid = intent.getStringExtra("customer_id");
        carprice = intent.getStringExtra("amount");

        razorpay_customer_id = intent.getStringExtra("razorpay_customer_id");
        orderid = intent.getStringExtra("razorpay_order_id");
        payment_type = intent.getStringExtra("payment_type");
        startPaymentMonth();
    }
    private void startPaymentMonth() {
        Checkout checkout = new Checkout();
        checkout.setKeyID(Constant.RAZOR_PAY_KEY_VALUE);
//        checkout.setKeyID("rzp_test_tcKV4hRzb6g7Z5");
//        checkout.setKeyID("rzp_live_2var7Se0NvE8Al");
        checkout.setImage(R.drawable.logo5234);

        try {
            JSONObject options = new JSONObject();
            options.put("name", R.string.app_name+"");
            options.put("description", servicetype);
            options.put("currency", "INR");
            options.put("customer_id", razorpay_customer_id);
            options.put("order_id", orderid);
//            options.put("token", "token_FfbnnuM1z1TEmE");
            options.put("recurring", "1");
//            if (s.equalsIgnoreCase("0")){
//                options.put("subscription_id", subscriptionId);
//            }
            double amount  = Double.parseDouble(carprice);
            amount = amount * 100;
            Log.e("AMOUNTRZP", String.valueOf(amount));
            options.put("amount", amount);

            JSONObject preFill = new JSONObject();
            preFill.put("email",custemail);
            preFill.put("contact",custmob);
            options.put("prefill", preFill);
            checkout.open(CheckoutActivity.this, options);
            Log.e("OPTIONS",options.toString());
        } catch (Exception e) {
            Log.d("PaymentOptionActivity", "Error in starting Razorpay Checkout", e);
        }
    }

    @Override
    public void onPaymentSuccess(String razorpayPaymentId, PaymentData paymentData) {
        loadWeb(paymentData.getOrderId(),paymentData.getPaymentId());
    }

    @Override
    public void onPaymentError(int i, String s, PaymentData paymentData) {
        Log.d("PaymentError", "Error in Razorpay Checkout"+s);
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void loadWeb(String orderId, String paymentId) {
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.getSettings().setLoadsImagesAutomatically(true);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
//        binding.webview.clearCache(true);
//        CookieManager cookieManager = CookieManager.getInstance();
//        cookieManager.setAcceptThirdPartyCookies(binding.webview,true);
        binding.webview.getSettings().setUseWideViewPort(true);
        binding.webview.getSettings().setLoadWithOverviewMode(true);
        binding.webview.getSettings().setDomStorageEnabled(true); // Add this
        binding.webview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        binding.webview.getSettings().setSupportMultipleWindows(true);
        binding.webview.getSettings().setAllowUniversalAccessFromFileURLs(true);
        binding.webview.getSettings().setMixedContentMode( WebSettings.MIXED_CONTENT_ALWAYS_ALLOW );
        binding.webview.addJavascriptInterface(new PaymentInterface(),"PaymentInterface");
        binding.webview.setWebViewClient(new MyBrowser());

        binding.webview.loadUrl("https://www.carrocare.in/Android_API/webview_checkout.php?p_type="+pacakagetype+"&v_type="+vehicletype+"&su_type="+subscriptiontype+"&se_type="+servicetype+"&v_id="+vehicleid+"&customer_id="+customerid+"&razorpay_customer_id="+razorpay_customer_id+"&razorpay_payment_id="+paymentId+"&razorpay_order_id="+orderId+"&payment_type="+payment_type+"");
//        binding.webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg)
//            {
//                WebView newWebView = new WebView(CheckoutActivity.this);
//                view.addView(newWebView);
//                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
//                transport.setWebView(newWebView);
//                resultMsg.sendToTarget();
//
//                newWebView.setWebViewClient(new WebViewClient() {
//                    @Override
//                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
////                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
////                        browserIntent.setData(Uri.parse(url));
////                        startActivity(browserIntent);
//                        view.loadUrl(url);
//                        return true;
//                    }
//
//                    @Override
//                    public void onPageFinished(WebView view, String url) {
//                        Log.e("URLfinis",url);
//                        if(url.contains("https://www.regalcarwash.in/Android_API/failure_page.php")) {
//                           finish();
//                        }else if(url.contains("https://www.regalcarwash.in/Android_API/success_page.php")) {
//                            Intent i = new Intent(CheckoutActivity.this, MainActivity.class);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
//                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
//                            startActivity(i);
//                            finish();
//                        }
//                    }
//
//                    @Override
//                    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
//                        super.onReceivedError(view, request, error);
//                        Log.e("ERROR",error.getDescription().toString());
//                    }
//                });
//                return true;
//
//            }
//        });
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            progressdialog.dismiss();
            Log.e("URLfinis",url);
            if(url.contains("https://www.carrocare.in/Android_API/failure_page.php")) {
                finish();
            }else if(url.contains("https://www.carrocare.in/Android_API/success_page.php")) {
                Intent i = new Intent(CheckoutActivity.this, MyOrdersActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
                finish();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.e("ERROR",error.getDescription().toString());
        }
    }

    private class PaymentInterface {
        @JavascriptInterface
        public void success(String data){
            Log.e("Success",data);
        }

        @JavascriptInterface
        public void error(String data){
            Log.e("Error",data);
        }
    }
}
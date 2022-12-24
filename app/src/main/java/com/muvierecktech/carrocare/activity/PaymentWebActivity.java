package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import android.net.Uri;

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
import com.muvierecktech.carrocare.databinding.ActivityPaymentWebBinding;

public class PaymentWebActivity extends AppCompatActivity {
    ActivityPaymentWebBinding binding;
    String pacakagetype, vehicletype, subscriptiontype, servicetype, vehicleid, customerid, carprice;
    ProgressDialog progressdialog;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_payment_web);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_web);

        Intent intent = getIntent();
        pacakagetype = intent.getStringExtra("package_type");
        vehicletype = intent.getStringExtra("vehicle_type");
        subscriptiontype = intent.getStringExtra("subscription_type");
        servicetype = intent.getStringExtra("service_type");
        vehicleid = intent.getStringExtra("vehicle_id");
        customerid = intent.getStringExtra("customer_id");
        carprice = intent.getStringExtra("amount");

        if (pacakagetype.equalsIgnoreCase("bike") || pacakagetype.equalsIgnoreCase("Bike") || pacakagetype.equalsIgnoreCase(Constant.BIKE)) {
            pacakagetype = "Bike";
        } else if (pacakagetype.equalsIgnoreCase("hatchback") || pacakagetype.equalsIgnoreCase("Hatchback") || pacakagetype.equalsIgnoreCase(Constant.HATCHBACK)) {
            pacakagetype = "Hatchback";
        } else if (pacakagetype.equalsIgnoreCase("sedan") || pacakagetype.equalsIgnoreCase("Sedan") || pacakagetype.equalsIgnoreCase(Constant.SEDAN)) {
            pacakagetype = "Sedan";
        } else if (pacakagetype.equalsIgnoreCase("suv") || pacakagetype.equalsIgnoreCase("SUV") || pacakagetype.equalsIgnoreCase("Suv") || pacakagetype.equalsIgnoreCase(Constant.SUV)) {
            pacakagetype = "SUV";
        }
//        progressdialog = new ProgressDialog(PaymentWebActivity.this);
//        progressdialog.setMessage("Please Wait....");
//        progressdialog.show();
//        progressdialog.setCancelable(false);

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
        binding.webview.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        binding.webview.addJavascriptInterface(new PaymentInterface(), "PaymentInterface");
        binding.webview.setWebViewClient(new MyBrowser());

        binding.webview.loadUrl("https://www.carrocare.in/Android_API/webview_checkout.php?p_type=" + pacakagetype + "&v_type=" + vehicletype + "&su_type=" + subscriptiontype + "&se_type=" + servicetype + "&v_id=" + vehicleid + "&customer_id=" + customerid + "");
//        binding.webview.setWebChromeClient(new WebChromeClient() {
//            @Override
//            public boolean onCreateWindow(WebView view, boolean dialog, boolean userGesture, Message resultMsg)
//            {
//                WebView newWebView = new WebView(PaymentWebActivity.this);
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
//                        Uri uri = Uri.parse(url);
//                        String razorpay_customer_id = uri.getQueryParameter("razorpay_customer_id");
//                        String order_id = uri.getQueryParameter("order_id");
//                        Intent intent = new Intent(PaymentWebActivity.this,CheckoutActivity.class);
//                        intent.putExtra("package_type",pacakagetype);
//                        intent.putExtra("vehicle_type",vehicletype);
//                        intent.putExtra("subscription_type","Monthly");
//                        intent.putExtra("service_type",servicetype);
//                        intent.putExtra("vehicle_id",vehicleid);
//                        intent.putExtra("customer_id",customerid);
//                        intent.putExtra("amount",carprice);
//                        intent.putExtra("razorpay_customer_id",razorpay_customer_id);
//                        intent.putExtra("razorpay_order_id",order_id);
//                        startActivity(intent);
////                        progressdialog.dismiss();
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
            if (url.indexOf("carrocare.in") > -1) return false;

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            Log.e("URL", url);
//            view.addJavascriptInterface(new Object()
//            {
//                @JavascriptInterface
//                public void performClick() throws Exception
//                {
//                    Log.d("LOGIN::", "Clicked");
//                    Toast.makeText(PaymentWebActivity.this, "Login clicked", Toast.LENGTH_LONG).show();
//                }
//            }, "login");
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
//            progressdialog.dismiss();
            Log.e("URLfinis", url);
            if (url.contains("https://www.carrocare.in/Android_API/emandate_authentication.php?")) {
                Uri uri = Uri.parse(url);
                String razorpay_customer_id = uri.getQueryParameter("razorpay_customer_id");
                String order_id = uri.getQueryParameter("order_id");
                String payment_type = uri.getQueryParameter("payment_type");
                Intent intent = new Intent(PaymentWebActivity.this, CheckoutActivity.class);
                intent.putExtra("package_type", pacakagetype);
                intent.putExtra("vehicle_type", vehicletype);
                intent.putExtra("subscription_type", "Monthly");
                intent.putExtra("service_type", servicetype);
                intent.putExtra("vehicle_id", vehicleid);
                intent.putExtra("customer_id", customerid);
                intent.putExtra("amount", carprice);
                intent.putExtra("razorpay_customer_id", razorpay_customer_id);
                intent.putExtra("razorpay_order_id", order_id);
                intent.putExtra("payment_type", payment_type);
                startActivity(intent);
                finish();
            }
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.e("ERROR", error.getDescription().toString());
        }
    }

    private class PaymentInterface {
        @JavascriptInterface
        public void success(String data) {
            Log.e("Success", data);
        }

        @JavascriptInterface
        public void error(String data) {
            Log.e("Error", data);
        }
    }
}
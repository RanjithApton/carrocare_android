package com.muvierecktech.carrocare.activity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.databinding.ActivityWebviewBinding;

public class WebviewActivity extends AppCompatActivity {
    ActivityWebviewBinding binding;
    ProgressDialog progressdialog;
    String header,link;
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this,R.layout.activity_webview);

        Intent intent = getIntent();
        header = intent.getStringExtra("headername");
        link = intent.getStringExtra("link");

        binding.headerName.setText(header+"");

        progressdialog = new ProgressDialog(WebviewActivity.this);
        progressdialog.setMessage("Please Wait....");
        progressdialog.show();
        progressdialog.setCancelable(false);
        binding.webview.setWebViewClient(new MyBrowser());

        binding.webview.getSettings().setLoadsImagesAutomatically(true);
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        binding.webview.loadUrl(link);

        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            progressdialog.dismiss();
        }
    }

}
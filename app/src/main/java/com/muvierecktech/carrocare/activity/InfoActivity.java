package com.muvierecktech.carrocare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.databinding.ActivityInfoBinding;


public class InfoActivity extends AppCompatActivity {
    ActivityInfoBinding binding;
    String headername,description;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_info);

        Intent intent = getIntent();
        headername = intent.getStringExtra("headername");
        description = intent.getStringExtra("description");
        binding.headerName.setText(headername);
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        binding.description.setText(HtmlCompat.fromHtml(description+"",0));
    }
}
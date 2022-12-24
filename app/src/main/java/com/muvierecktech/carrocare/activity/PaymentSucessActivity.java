package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.databinding.ActivityPaymentSucessBinding;

public class PaymentSucessActivity extends AppCompatActivity {
    ActivityPaymentSucessBinding binding;
    String status, amount, type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_payment_sucess);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_payment_sucess);

        status = getIntent().getStringExtra("status");
        amount = getIntent().getStringExtra("amount");
        type = getIntent().getStringExtra("type");

        binding.sucessAmount.setText("Total : ₹ " + amount);
        binding.failureAmount.setText("Total : ₹ " + amount);

        if (status.equalsIgnoreCase("success")) {
            binding.sucessScreen.setVisibility(View.VISIBLE);
            if (type.equalsIgnoreCase("cod")) {
                binding.codText.setVisibility(View.VISIBLE);
            } else {
                binding.codText.setVisibility(View.GONE);
            }
        } else if (status.equalsIgnoreCase("failure")) {
            binding.failureScreen.setVisibility(View.VISIBLE);
        }

        binding.sucessDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PaymentSucessActivity.this, MyOrdersActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });

        binding.successHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PaymentSucessActivity.this, DoorStepServiceActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });

        binding.failureDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PaymentSucessActivity.this, MyOrdersActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });

        binding.failureHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(PaymentSucessActivity.this, DoorStepServiceActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(PaymentSucessActivity.this, MainActivity.class));
        finish();
    }
}
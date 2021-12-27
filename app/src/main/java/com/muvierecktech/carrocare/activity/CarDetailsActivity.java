package com.muvierecktech.carrocare.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.databinding.DataBindingUtil;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.common.Constant;

import com.muvierecktech.carrocare.databinding.ActivityCarDetailsBinding;
import com.squareup.picasso.Picasso;

public class CarDetailsActivity extends AppCompatActivity {
    ActivityCarDetailsBinding binding;
    String carname,carprice,cardesc,carimage,carid,header;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_car_details);

        Intent intent = getIntent();
        carname = intent.getStringExtra("carname");
        carprice = intent.getStringExtra("carprice");
        cardesc = intent.getStringExtra("cardesc");
        carimage = intent.getStringExtra("carimage");
        carid = intent.getStringExtra("carid");
        header = intent.getStringExtra("header");

        if (header.equalsIgnoreCase(Constant.MACHINEPOLISH)){
            binding.booknow.setText("Proceed");
        }
        binding.carName.setText(carname);
        binding.headerName.setText(header);
        binding.carDesc.setText(HtmlCompat.fromHtml(cardesc,0));
        binding.carPrice.setText("Total amount \n ₹ "+carprice);
        Picasso.get()
                .load(carimage)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(binding.carImg);

        binding.booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.booknow.getText().equals("Proceed")){
                    Intent intent1 = new Intent(CarDetailsActivity.this, ConfirmFormActivity.class);
                    startActivity(intent1);
                }else {
                    Intent intent = new Intent(CarDetailsActivity.this, VehicleListActivity.class);
                    intent.putExtra("carname",carname);
                    intent.putExtra("carprice",carprice);
                    intent.putExtra("cardesc",cardesc);
                    intent.putExtra("carimage",carimage);
                    intent.putExtra("carid",carid);
                    intent.putExtra("header",header);
                    startActivity(intent);
                }
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
package com.muvierecktech.carrocare.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.Toast;

import com.muvierecktech.carrocare.R;
import com.muvierecktech.carrocare.adapter.ReminderAdapter;
import com.muvierecktech.carrocare.adapter.VechicleCategoryAdapter;
import com.muvierecktech.carrocare.common.Constant;
import com.muvierecktech.carrocare.databinding.ActivityMyRemainderBinding;

import java.util.Calendar;

public class MyRemainderActivity extends AppCompatActivity implements View.OnClickListener {
    public ActivityMyRemainderBinding binding;

    String remainderType[] = {"Services Remainder", "Bike/Car Insurance Remainder"};
    String serviceCount[] = {"1", "2", "3", "4", "5"};

    String insuranceBrand[] = {"Bajaj Allianz Car insurance",
            "Bharti axa Car insurance",
            "Cholamandalam Car insurance",
            "Digit Car Insurance",
            "Edelweiss Car Insurance",
            "Future Generali Car Insurance",
            "HDFC ERGO Car Insurance",
            "IFFCO Tokio Car Insurance",
            "Kotak Mahindra Car Insurance",
            "Liberty Car Insurance",
            "National Car Insurance",
            "New India Assurance Car Insurance",
            "Oriental Car Insurance",
            "Universal Sompo Car Insurance",
            "Reliance Car Insurance",
            "Royal Sundaram Car Insurance",
            "SBI Car Insurance",
            "Shriram Car Insurance",
            "Tata AIG Car Insurance",
            "United India Car Insurance",
            "Raheja QBE Car Insurance"};

    String setRemainder[] = {"1 Month", "2 Weeks", "1 Week", "3 Days", "1 Day"};

    DatePickerDialog picker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_my_remainder);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_remainder);

        binding.reminderEdt.setOnClickListener(this);
        binding.serviceCountEdt.setOnClickListener(this);
        binding.setSerReminder.setOnClickListener(this);
        binding.back.setOnClickListener(this);
        binding.serCancel.setOnClickListener(this);
        binding.insCancel.setOnClickListener(this);
        binding.lastSerDateEdt.setOnClickListener(this);
        binding.nextSerDateEdt.setOnClickListener(this);
        binding.insBrndEdt.setOnClickListener(this);
        binding.setInsReminder.setOnClickListener(this);
        binding.paidDateEdt.setOnClickListener(this);
        binding.renewalDateEdt.setOnClickListener(this);

        binding.serSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.serviceCountEdt.getText().toString().length() > 0 &&
                        binding.lastSerDateEdt.getText().toString().length() > 0 &&
                        binding.lastDriKmsEdt.getText().toString().length() > 0 &&
                        binding.nextSerDateEdt.getText().toString().length() > 0 &&
                        binding.nextDriKmsEdt.getText().toString().length() > 0 &&
                        binding.setSerReminder.getText().toString().length() > 0) {
                    Toast.makeText(MyRemainderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MyRemainderActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.insSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.insBrndEdt.getText().toString().length() > 0 &&
                        binding.policyNoEdt.getText().toString().length() > 0 &&
                        binding.engineeNoEdt.getText().toString().length() > 0 &&
                        binding.paidDateEdt.getText().toString().length() > 0 &&
                        binding.paidAmountEdt.getText().toString().length() > 0 &&
                        binding.vecRegEdt.getText().toString().length() > 0 &&
                        binding.renewalDateEdt.getText().toString().length() > 0 &&
                        binding.setInsReminder.getText().toString().length() > 0) {
                    Toast.makeText(MyRemainderActivity.this, "Success", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MyRemainderActivity.this, Constant.DETAILS, Toast.LENGTH_SHORT).show();
                }
            }
        });


    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.last_ser_date_edt:
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MyRemainderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.lastSerDateEdt.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year, month, day);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
                break;
            case R.id.next_ser_date_edt:
                final Calendar cldr1 = Calendar.getInstance();
                int day1 = cldr1.get(Calendar.DAY_OF_MONTH);
                int month1 = cldr1.get(Calendar.MONTH);
                int year1 = cldr1.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MyRemainderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.nextSerDateEdt.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year1, month1, day1);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
                break;
            case R.id.reminder_edt:
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.reminTypeRc.setVisibility(View.VISIBLE);
                ReminderAdapter reminderAdapter = new ReminderAdapter(MyRemainderActivity.this, remainderType, "remainderType");
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(MyRemainderActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.reminTypeRc.setLayoutManager(linearLayoutManager);
                binding.reminTypeRc.setAdapter(reminderAdapter);
                break;
            case R.id.service_count_edt:
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.serCountRc.setVisibility(View.VISIBLE);
                ReminderAdapter reminderAdapter1 = new ReminderAdapter(MyRemainderActivity.this, serviceCount, "serviceCount");
                LinearLayoutManager linearLayoutManager1 = new LinearLayoutManager(MyRemainderActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.serCountRc.setLayoutManager(linearLayoutManager1);
                binding.serCountRc.setAdapter(reminderAdapter1);
                break;
            case R.id.set_ser_reminder:
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.setRemainderRc.setVisibility(View.VISIBLE);
                ReminderAdapter reminderAdapter2 = new ReminderAdapter(MyRemainderActivity.this, setRemainder, "setRemainder");
                LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(MyRemainderActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.setRemainderRc.setLayoutManager(linearLayoutManager2);
                binding.setRemainderRc.setAdapter(reminderAdapter2);
                break;
            case R.id.ins_brnd_edt:
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.insBrndRc.setVisibility(View.VISIBLE);
                ReminderAdapter reminderAdapter3 = new ReminderAdapter(MyRemainderActivity.this, insuranceBrand, "insuranceBrand");
                LinearLayoutManager linearLayoutManager3 = new LinearLayoutManager(MyRemainderActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.insBrndRc.setLayoutManager(linearLayoutManager3);
                binding.insBrndRc.setAdapter(reminderAdapter3);
                break;
            case R.id.paid_date_edt:
                final Calendar cldr2 = Calendar.getInstance();
                int day2 = cldr2.get(Calendar.DAY_OF_MONTH);
                int month2 = cldr2.get(Calendar.MONTH);
                int year2 = cldr2.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MyRemainderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.paidDateEdt.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year2, month2, day2);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
                break;
            case R.id.renewal_date_edt:
                final Calendar cldr3 = Calendar.getInstance();
                int day3 = cldr3.get(Calendar.DAY_OF_MONTH);
                int month3 = cldr3.get(Calendar.MONTH);
                int year3 = cldr3.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(MyRemainderActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                binding.renewalDateEdt.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);
                            }
                        }, year3, month3, day3);
                picker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                picker.show();
                break;
            case R.id.set_ins_reminder:
                binding.apartRl.setVisibility(View.VISIBLE);
                binding.setRemainderRc.setVisibility(View.VISIBLE);
                ReminderAdapter reminderAdapter4 = new ReminderAdapter(MyRemainderActivity.this, setRemainder, "setRemainderInsurance");
                LinearLayoutManager linearLayoutManager4 = new LinearLayoutManager(MyRemainderActivity.this, LinearLayoutManager.VERTICAL, false);
                binding.setRemainderRc.setLayoutManager(linearLayoutManager4);
                binding.setRemainderRc.setAdapter(reminderAdapter4);
                break;
            case R.id.back:
                finish();
                break;
            case R.id.ser_cancel:
                finish();
                break;
            case R.id.ins_cancel:
                finish();
                break;
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
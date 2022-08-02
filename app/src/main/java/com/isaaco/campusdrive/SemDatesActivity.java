package com.isaaco.campusdrive;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

public class SemDatesActivity extends AppCompatActivity {
    TextView tv_startDate;
    TextView tv_endDate;
    MaterialButton btn_submitDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sem_dates);
        tv_startDate = findViewById(R.id.tv_semStartDate);
        tv_endDate = findViewById(R.id.tv_semEndDate);
        btn_submitDate = findViewById(R.id.btn_submitDate);

        tv_startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String start = "start";
                openDatePickerDialog(start);
            }
        });
        tv_endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String end = "end";
                openDatePickerDialog(end);
            }
        });
    }

    private void openDatePickerDialog(String date) {
        Context mContext = getApplicationContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
        View customLayout = inflater.inflate(R.layout.dialog_calendar,findViewById(R.id.ll_caledar));
        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
        builder.setView(customLayout);
        DatePicker datepicker = customLayout.findViewById(R.id.calendar);
        MaterialButton btn_selectDate = customLayout.findViewById(R.id.select_date);

        String datePicked = datepicker.getDayOfMonth() +"/"+datepicker.getMonth()+"/"+ datepicker.getYear();
        AlertDialog alert = builder.create();
        alert.show();

        btn_selectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(date.equals("start")){
                    tv_startDate.setText(datePicked);
                    alert.dismiss();
                }else if(date.equals("end")){
                    tv_endDate.setText(datePicked);
                    alert.dismiss();
                }
            }
        });


    }
}
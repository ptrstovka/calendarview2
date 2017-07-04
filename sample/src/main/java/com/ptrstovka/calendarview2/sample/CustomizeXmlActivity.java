package com.ptrstovka.calendarview2.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ptrstovka.calendarview2.CalendarView2;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomizeXmlActivity extends AppCompatActivity {

    @BindView(R.id.calendarView)
    CalendarView2 widget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customization);
        ButterKnife.bind(this);
    }

}

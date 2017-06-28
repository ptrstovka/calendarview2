package com.prolificinteractive.materialcalendarview.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

public class FeatureTestActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;

    private static final String TAG = "FeatureTestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_test);

        calendarView = (MaterialCalendarView) findViewById(R.id.calendar_view);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_MULTIPLE);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                widget.invalidateDecorator(date);
            }
        });

        calendarView.addDecorator(new CurrentDayDecorator(calendarView));

    }

    private class CurrentDayDecorator extends DayViewDecorator {

        private MaterialCalendarView view;

        public CurrentDayDecorator(MaterialCalendarView view) {
            this.view = view;
        }

        boolean isSelected = false;

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            boolean result = DateUtils.isToday(day.getDate().getTime());
            isSelected = view.getSelectedDates().contains(day);
            return result;
        }

        @Override
        public void decorate(DayViewFacade view) {
            if (isSelected) {
                view.addSpan(new DotSpan(8, Color.WHITE));
            } else {
                view.addSpan(new DotSpan(8, Color.BLACK));
            }
        }

        @Override
        protected boolean shouldBeCached() {
            return false;
        }
    }

}

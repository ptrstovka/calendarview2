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
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.prolificinteractive.materialcalendarview.CalendarDay.from;
import static com.prolificinteractive.materialcalendarview.Range.range;

@SuppressWarnings("unused")
public class FeatureTestActivity extends AppCompatActivity {

    private static final String TAG = "FeatureTestActivity";

    @BindView(R.id.calendar_view)
    MaterialCalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_test);
        ButterKnife.bind(this);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        selectRange();

//        Range range = range(from(2017, Calendar.JULY, 4), from(2017, Calendar.JULY, 10));
//        for (CalendarDay calendarDay : range.days()) {
//            Log.d(TAG, "onCreate: " + calendarDay.toString());
//        }
    }

    @OnClick(R.id.calendar_action_button)
    public void onActionButtonClick() {
        selectRange();
    }

    private void selectRange() {
        calendarView.select(
                range(from(2017, Calendar.JULY, 4), from(2017, Calendar.JULY, 10)),
                range(from(2017, Calendar.JULY, 17), from(2017, Calendar.JULY, 21))
        );
    }

    private void addCurrentDayDecorator() {
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget,
                                       @NonNull CalendarDay date, boolean selected) {
                widget.invalidateDecorator(date);
            }
        });

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                widget.invalidateDecorators();
            }
        });

        calendarView.addDecorator(new CurrentDayDecorator(calendarView));
    }

    private class CurrentDayDecorator extends DayViewDecorator {

        private MaterialCalendarView view;

        CurrentDayDecorator(MaterialCalendarView view) {
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

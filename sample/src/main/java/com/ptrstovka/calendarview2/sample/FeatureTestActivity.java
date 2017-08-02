package com.ptrstovka.calendarview2.sample;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;

import com.ptrstovka.calendarview2.CalendarDay;
import com.ptrstovka.calendarview2.DayViewDecorator;
import com.ptrstovka.calendarview2.DayViewFacade;
import com.ptrstovka.calendarview2.CalendarView2;
import com.ptrstovka.calendarview2.OnDateSelectedListener;
import com.ptrstovka.calendarview2.OnRangeSelectedListener;
import com.ptrstovka.calendarview2.Range;
import com.ptrstovka.calendarview2.spans.DotSpan;

import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.ptrstovka.calendarview2.CalendarDay.from;
import static com.ptrstovka.calendarview2.Range.range;
import static java.util.Arrays.asList;

@SuppressWarnings("unused")
public class FeatureTestActivity extends AppCompatActivity {

    private static final String TAG = "FeatureTestActivity";

    @BindView(R.id.calendar_view)
    CalendarView2 calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feature_test);
        ButterKnife.bind(this);
        calendarView.setSelectionMode(CalendarView2.SELECTION_MODE_NONE);
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
        List<Range> ranges = asList(
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 5)),
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 3)),
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 7))
        );

        calendarView.select(ranges);
    }

    private void addCurrentDayDecorator() {
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull CalendarView2 widget,
                                       @NonNull CalendarDay date, boolean selected) {
                widget.invalidateDecorator(date);
            }
        });

        calendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull CalendarView2 widget, @NonNull List<CalendarDay> dates) {
                widget.invalidateDecorators();
            }
        });

        calendarView.addDecorator(new CurrentDayDecorator(calendarView));
    }

    private class CurrentDayDecorator extends DayViewDecorator {

        private CalendarView2 view;

        CurrentDayDecorator(CalendarView2 view) {
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

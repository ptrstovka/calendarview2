package com.ptrstovka.calendarview2;

import android.support.annotation.NonNull;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by: Peter Štovka <stovka.peter@gmail.com>
 * Created at: 7/3/17.
 */

public class RangeDaySelectionCalculator {

    @NonNull
    private final Range range;

    private int firstDayOfWeek;
    private int lastDayOfWeek;

    private RangeDaySelectionCalculator(@NonNull Range range) {
        this.range = range;

        firstDayOfWeek = range.from.getCalendar().getFirstDayOfWeek();
        if (firstDayOfWeek == Calendar.SUNDAY) {
            lastDayOfWeek = Calendar.SATURDAY;
        } else {
            lastDayOfWeek = Calendar.SUNDAY;
        }
    }

    private Map<CalendarDay, Integer> calculateResults() {
        Map<CalendarDay, Integer> map = new HashMap<>();
        for (CalendarDay calendarDay : range.days()) {
            RangeDaySelectionResult result = getResultForDay(calendarDay);
            map.put(result.getDay(), result.getSelection());
        }
        return map;
    }

    private RangeDaySelectionResult getResultForDay(CalendarDay day) {

        int result;

        if (isFirstDay(day)) {
            if (isLastDayInRow(day)) {
                result = DayView.SELECTION_NORMAL;
            } else {
                result = DayView.SELECTION_FIRST;
            }
        } else if (isMiddleDay(day)) {
            if (isFirstDayInRow(day)) {
                result = DayView.SELECTION_RANGE_LEFT;
            } else if (isLastDayInRow(day)) {
                result = DayView.SELECTION_RANGE_RIGHT;
            } else { // middle day
                result = DayView.SELECTION_RANGE;
            }
        } else { // last day
            if (isFirstDayInRow(day)) {
                result = DayView.SELECTION_NORMAL;
            } else {
                result = DayView.SELECTION_LAST;
            }
        }

        return new RangeDaySelectionResult(day, result);
    }

    private boolean isFirstDayInRow(CalendarDay day) {
        return day.getCalendar().get(Calendar.DAY_OF_WEEK) == firstDayOfWeek;
    }

    private boolean isLastDayInRow(CalendarDay day) {
        return day.getCalendar().get(Calendar.DAY_OF_WEEK) == lastDayOfWeek;
    }

    private boolean isFirstDay(CalendarDay day) {
        return day.equals(range.from);
    }

    private boolean isLastDay(CalendarDay day) {
        return day.equals(range.to);
    }

    private boolean isMiddleDay(CalendarDay day) {
        return !isFirstDay(day) && !isLastDay(day);
    }

    public static Map<CalendarDay, Integer> getSelectionsFor(Range range) {
        RangeDaySelectionCalculator calculator = new RangeDaySelectionCalculator(range);
        return calculator.calculateResults();
    }

}

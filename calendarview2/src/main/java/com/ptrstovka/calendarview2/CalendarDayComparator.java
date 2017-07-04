package com.ptrstovka.calendarview2;

import java.util.Comparator;

/**
 * Created by: Peter Štovka <stovka.peter@gmail.com>
 * Created at: 6/29/17.
 */

public class CalendarDayComparator implements Comparator<CalendarDay> {

    @Override
    public int compare(CalendarDay first, CalendarDay second) {

        // 1 first > sec
        // 0 first = sec
        // -1 first < sec

        if (first.isAfter(second)) {
            return 1;
        }

        if (first.isBefore(second)) {
            return -1;
        }

        return 0;
    }

}

package com.prolificinteractive.materialcalendarview;

/**
 * Created by: Peter Štovka <stovka.peter@gmail.com>
 * Created at: 6/30/17.
 */

public class Range {

    CalendarDay from;
    CalendarDay to;

    public Range(CalendarDay from, CalendarDay to) {
        this.from = from;
        this.to = to;
    }

    public static Range range(CalendarDay from, CalendarDay to) {
        return new Range(from, to);
    }

}

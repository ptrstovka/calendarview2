package com.ptrstovka.calendarview2;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * @author Peter Štovka <stovka.peter@gmail.com>
 */

public class Range {

    @NonNull CalendarDay from;
    @NonNull CalendarDay to;

    private Range(@NonNull CalendarDay from, @NonNull CalendarDay to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "[From: %d/%d/%d - To: %d/%d/%d]",
                from.getDay(), from.getMonth(), from.getYear(),
                to.getDay(), to.getMonth(), to.getYear());
    }

    boolean isInRange(@NonNull CalendarDay day) {
        return from.equals(day) || to.equals(day) || (from.isBefore(day) && to.isAfter(day));
    }

    public List<CalendarDay> days() {
        List<CalendarDay> calendarDays = new ArrayList<>();
        Calendar from = Calendar.getInstance();
        this.from.copyTo(from);
        Calendar to = Calendar.getInstance();
        this.to.copyTo(to);

        while (from.before(to) || from.equals(to)) {
            calendarDays.add(CalendarDay.from(from));
            from.add(Calendar.DAY_OF_YEAR, 1);
        }

        return calendarDays;
    }

    public static Range range(CalendarDay from, CalendarDay to) {

        if (from == null) {
            throw new NullPointerException("Date from cannot be null.");
        }

        if (to == null) {
            throw new NullPointerException("Date to cannot be null.");
        }

        if (from.isBefore(to)) {
            return new Range(from, to);
        } else {
            return new Range(to, from);
        }
    }

}

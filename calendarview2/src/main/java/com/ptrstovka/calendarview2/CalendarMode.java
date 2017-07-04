package com.ptrstovka.calendarview2;

@Experimental
public enum CalendarMode {

    MONTHS(6),
    WEEKS(1);

    final int visibleWeeksCount;

    CalendarMode(int visibleWeeksCount) {
        this.visibleWeeksCount = visibleWeeksCount;
    }
}

package com.ptrstovka.calendarview2;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import static com.ptrstovka.calendarview2.CalendarDay.max;
import static com.ptrstovka.calendarview2.Range.range;

/**
 * @author Peter Štovka <stovka.peter@gmail.com>
 */

class RangeOverlapMerger {

    @NonNull
    private final List<Range> ranges;

    private RangeOverlapMerger(@NonNull List<Range> ranges) {
        this.ranges = ranges;
    }

    private List<Range> joinOverlappingRanges() {
        List<Range> range = RangeSorter.sort(ranges);
        List<Range> result = new ArrayList<>();
        Range previous = range.get(0);

        for (int i = 0; i < range.size(); i++) {
            Range current = range.get(i);
            if (current.from.isAfter(previous.to)) {
                result.add(previous);
                previous = current;
            } else {
                previous = range(previous.from, max(previous.to, current.to));
            }
        }

        result.add(previous);
        return result;
    }

    static List<Range> join(List<Range> ranges) {
        RangeOverlapMerger calculator = new RangeOverlapMerger(ranges);
        return calculator.joinOverlappingRanges();
    }
}

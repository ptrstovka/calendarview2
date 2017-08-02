package com.ptrstovka.calendarview2;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.ptrstovka.calendarview2.Range.range;

/**
 * Created by: Peter Štovka <stovka.peter@gmail.com>
 * Created at: 7/3/17.
 */

class RangeOverlapCalculator {

    @NonNull
    private final List<Range> ranges;

    private RangeOverlapCalculator(@NonNull List<Range> ranges) {
        this.ranges = ranges;
    }

    private List<Range> joinOverlappingRanges() {
        List<Range> result = RangeSorter.sort(ranges);
        while (areOverlapping(result)) {
            result = joinOneOverlappingRange(result);
        }
        return result;
    }

    private List<Range> joinOneOverlappingRange(List<Range> ranges) {
        List<Range> joined = new ArrayList<>();
        for (int i = 0, j = 1; j < ranges.size(); i++, j++) {
            Range first = ranges.get(i);
            Range second = ranges.get(j);
            if (areOverlapping(first, second)) {
                joined.add(joinRanges(first, second));
                i += 1;
                j += 1;

                if (ranges.size() == (j + 1)) {
                    joined.add(ranges.get(ranges.size() - 1));
                }
            } else {
                joined.add(ranges.get(i));
            }
        }
        return joined;
    }

    private Range joinRanges(Range one, Range two) {
        return range(one.from, two.to);
    }

    private boolean areOverlapping(List<Range> ranges) {
        if (ranges.isEmpty() || ranges.size() == 1) {
            return false;
        }
        for (int i = 0, j = 1; j < ranges.size(); i++, j++) {
            if (areOverlapping(ranges.get(i), ranges.get(j))) {
                return true;
            }
        }
        return false;
    }

    private boolean areOverlapping(Range rangeOne, Range rangeTwo) {
        for (CalendarDay calendarDay : rangeOne.days()) {
            if (rangeTwo.isInRange(calendarDay)) {
                return true;
            }
        }
        return false;
    }

    static List<Range> join(List<Range> range) {
        RangeOverlapCalculator calculator = new RangeOverlapCalculator(range);
        return calculator.joinOverlappingRanges();
    }

}

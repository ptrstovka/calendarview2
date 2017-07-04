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
        List<Range> result = new ArrayList<>(ranges);
        Collections.sort(result, new RangeComparator());
        while (areOverlapping(result)) {
            result = joinOneOverlappingRange(result);
        }
        return result;
    }

    private List<Range> joinOneOverlappingRange(List<Range> ranges) {
        List<Range> splitted = new ArrayList<>();
        for (int i = 0, j = 1; j < ranges.size(); i++, j++) {
            if (areOverlapping(ranges.get(i), ranges.get(j))) {
                splitted.add(joinRanges(ranges.get(i), ranges.get(j)));
                i += 1;
                j += 1;
            } else {
                splitted.add(ranges.get(i));
            }
        }
        return splitted;
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

    private class RangeComparator implements Comparator<Range> {

        @Override
        public int compare(Range o1, Range o2) {
            if (o1.from.equals(o2.from)) {
                return 0;
            }
            if (o1.from.isBefore(o2.from)) {
                return -1;
            } else {
                return 1;
            }
        }

    }

    static List<Range> join(List<Range> range) {
        RangeOverlapCalculator calculator = new RangeOverlapCalculator(range);
        return calculator.joinOverlappingRanges();
    }

}

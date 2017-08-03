package com.ptrstovka.calendarview2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Peter Štovka <stovka.peter@gmail.com>
 */

class RangeSorter {

    private List<Range> ranges = new ArrayList<>();

    private RangeSorter(List<Range> ranges) {
        this.ranges.addAll(ranges);
    }

    private void sortRanges() {
        Collections.sort(ranges, new RangeComparator());
    }

    static List<Range> sort(List<Range> list) {
        RangeSorter sorter = new RangeSorter(list);
        sorter.sortRanges();
        return sorter.ranges;
    }

    private class RangeComparator implements Comparator<Range> {

        private final int SAME = 0;
        private final int FIRST_HIGHER = 1;
        private final int SECOND_HIGHER = -1;

        @Override
        public int compare(Range first, Range second) {
            if (first.from.equals(second.from)) {
                if (first.to.equals(second.to)) {
                    return SAME;
                } else if (first.to.isBefore(second.to)) {
                    return SECOND_HIGHER;
                } else {
                    return FIRST_HIGHER;
                }
            }

            if (first.from.isBefore(second.from)) {
                return SECOND_HIGHER;
            } else {
                return FIRST_HIGHER;
            }
        }
    }
}

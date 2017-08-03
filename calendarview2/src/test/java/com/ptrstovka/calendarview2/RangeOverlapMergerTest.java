package com.ptrstovka.calendarview2;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static com.ptrstovka.calendarview2.CalendarDay.from;
import static com.ptrstovka.calendarview2.Range.range;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * @author Peter Štovka <stovka.peter@gmail.com>
 */
public class RangeOverlapMergerTest {

    @Test
    public void should_join_overlapping_ranges() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 2), from(2017, Calendar.JANUARY, 5))
        );

        assertEquals(2, ranges.size());
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(1, result.size());

        Range range = result.get(0);
        assertEquals(1, range.from.getDay());
        assertEquals(Calendar.JANUARY, range.from.getMonth());
        assertEquals(2017, range.from.getYear());

        assertEquals(5, range.to.getDay());
        assertEquals(Calendar.JANUARY, range.to.getMonth());
        assertEquals(2017, range.to.getYear());
    }

    @Test
    public void should_join_overlaping_ranges_to_one() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 5)),
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 3)),
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 7))
        );

        assertEquals(3, ranges.size());
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(1, result.size());
        Range range = result.get(0);

        assertEquals(1, range.from.getDay());
        assertEquals(Calendar.AUGUST, range.from.getMonth());
        assertEquals(2017, range.from.getYear());

        assertEquals(7, range.to.getDay());
        assertEquals(Calendar.AUGUST, range.to.getMonth());
        assertEquals(2017, range.to.getYear());
    }

    @Test
    public void should_join_overlaping_ranges_to_two() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 5)),
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 3)),
                range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 7)),
                range(from(2017, Calendar.AUGUST, 10), from(2017, Calendar.AUGUST, 27))
        );

        assertEquals(4, ranges.size());
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(2, result.size());
        Range range1 = result.get(0);

        assertEquals(1, range1.from.getDay());
        assertEquals(Calendar.AUGUST, range1.from.getMonth());
        assertEquals(2017, range1.from.getYear());

        assertEquals(7, range1.to.getDay());
        assertEquals(Calendar.AUGUST, range1.to.getMonth());
        assertEquals(2017, range1.to.getYear());

        Range range2 = result.get(1);
        assertEquals(10, range2.from.getDay());
        assertEquals(Calendar.AUGUST, range2.from.getMonth());
        assertEquals(2017, range2.from.getYear());

        assertEquals(27, range2.to.getDay());
        assertEquals(Calendar.AUGUST, range2.to.getMonth());
        assertEquals(2017, range2.to.getYear());
    }

    @Test
    public void should_not_join_ranges() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 6), from(2017, Calendar.JANUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(result.size(), 2);
    }

    @Test
    public void should_join_overlaping_ranges_within_same_days() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 4), from(2017, Calendar.JANUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(result.size(), 1);

        Range range = result.get(0);
        assertEquals(1, range.from.getDay());
        assertEquals(Calendar.JANUARY, range.from.getMonth());
        assertEquals(2017, range.from.getYear());

        assertEquals(9, range.to.getDay());
        assertEquals(Calendar.JANUARY, range.to.getMonth());
        assertEquals(2017, range.to.getYear());
    }

    @Test
    public void should_not_join_overlaping_ranges_on_next_day() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 5), from(2017, Calendar.JANUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(result.size(), 2);
    }

    @Test
    public void should_join_overlaping_ranges_over_month() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 25)),
                range(from(2017, Calendar.JANUARY, 15), from(2017, Calendar.FEBRUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(result.size(), 1);

        Range range = result.get(0);
        assertEquals(1, range.from.getDay());
        assertEquals(Calendar.JANUARY, range.from.getMonth());
        assertEquals(2017, range.from.getYear());

        assertEquals(9, range.to.getDay());
        assertEquals(Calendar.FEBRUARY, range.to.getMonth());
        assertEquals(2017, range.to.getYear());
    }

    @Test
    public void should_join_overlaping_ranges_over_months() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.FEBRUARY, 4)),
                range(from(2017, Calendar.FEBRUARY, 2), from(2017, Calendar.MARCH, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(result.size(), 1);

        Range range = result.get(0);
        assertEquals(1, range.from.getDay());
        assertEquals(Calendar.JANUARY, range.from.getMonth());
        assertEquals(2017, range.from.getYear());

        assertEquals(9, range.to.getDay());
        assertEquals(Calendar.MARCH, range.to.getMonth());
        assertEquals(2017, range.to.getYear());
    }

    @Test
    public void should_join_more_ranges_to_one() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 3), from(2017, Calendar.JANUARY, 9)),
                range(from(2017, Calendar.JANUARY, 5), from(2017, Calendar.JANUARY, 19)),
                range(from(2017, Calendar.JANUARY, 2), from(2017, Calendar.JANUARY, 18)),
                range(from(2017, Calendar.JANUARY, 7), from(2017, Calendar.JANUARY, 25))
        );

        assertEquals(ranges.size(), 5);
        List<Range> result = RangeOverlapMerger.join(ranges);
        assertEquals(result.size(), 1);

        Range range = result.get(0);
        assertEquals(1, range.from.getDay());
        assertEquals(Calendar.JANUARY, range.from.getMonth());
        assertEquals(2017, range.from.getYear());

        assertEquals(25, range.to.getDay());
        assertEquals(Calendar.JANUARY, range.to.getMonth());
        assertEquals(2017, range.to.getYear());
    }

}
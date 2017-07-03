package com.prolificinteractive.materialcalendarview;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static com.prolificinteractive.materialcalendarview.CalendarDay.from;
import static com.prolificinteractive.materialcalendarview.Range.range;
import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

/**
 * Created by: Peter Štovka <stovka.peter@gmail.com>
 * Created at: 7/3/17.
 */
public class RangeOverlapCalculatorTest {

    @Test
    public void should_join_overlapping_ranges() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 2), from(2017, Calendar.JANUARY, 5))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapCalculator.join(ranges);
        assertEquals(result.size(), 1);
    }

    @Test
    public void should_not_join_ranges() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 6), from(2017, Calendar.JANUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapCalculator.join(ranges);
        assertEquals(result.size(), 2);
    }

    @Test
    public void should_join_overlaping_ranges_within_same_days() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 4), from(2017, Calendar.JANUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapCalculator.join(ranges);
        assertEquals(result.size(), 1);
    }

    @Test
    public void should_not_join_overlaping_ranges_on_next_day() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 4)),
                range(from(2017, Calendar.JANUARY, 5), from(2017, Calendar.JANUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapCalculator.join(ranges);
        assertEquals(result.size(), 2);
    }

    @Test
    public void should_join_overlaping_ranges_over_month() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.JANUARY, 25)),
                range(from(2017, Calendar.JANUARY, 15), from(2017, Calendar.FEBRUARY, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapCalculator.join(ranges);
        assertEquals(result.size(), 1);
    }

    @Test
    public void should_join_overlaping_ranges_over_months() throws Exception {
        List<Range> ranges = asList(
                range(from(2017, Calendar.JANUARY, 1), from(2017, Calendar.FEBRUARY, 4)),
                range(from(2017, Calendar.FEBRUARY, 2), from(2017, Calendar.MARCH, 9))
        );

        assertEquals(ranges.size(), 2);
        List<Range> result = RangeOverlapCalculator.join(ranges);
        assertEquals(result.size(), 1);
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
        List<Range> result = RangeOverlapCalculator.join(ranges);
        assertEquals(result.size(), 1);
    }

}
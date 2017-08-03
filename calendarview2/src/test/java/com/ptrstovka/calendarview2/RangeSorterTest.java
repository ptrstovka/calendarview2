package com.ptrstovka.calendarview2;

import org.junit.Test;

import java.util.Calendar;
import java.util.List;

import static com.ptrstovka.calendarview2.CalendarDay.from;
import static com.ptrstovka.calendarview2.Range.range;
import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * @author Peter Štovka <stovka.peter@gmail.com>
 */
public class RangeSorterTest {

    @Test
    public void should_sort_ranges() throws Exception {
        Range first = range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 3));
        Range second = range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 5));
        Range third = range(from(2017, Calendar.AUGUST, 1), from(2017, Calendar.AUGUST, 7));
        Range fourth = range(from(2017, Calendar.AUGUST, 10), from(2017, Calendar.AUGUST, 27));

        List<Range> result = RangeSorter.sort(asList(second, first, third, fourth));
        List<Range> expected = asList(first, second, third, fourth);
        assertEquals(expected, result);
    }
}
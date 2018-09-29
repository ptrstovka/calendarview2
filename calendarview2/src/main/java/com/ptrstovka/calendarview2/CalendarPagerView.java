package com.ptrstovka.calendarview2;

import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.ptrstovka.calendarview2.CalendarView2.ShowOtherDates;
import com.ptrstovka.calendarview2.format.DayFormatter;
import com.ptrstovka.calendarview2.format.WeekDayFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.ptrstovka.calendarview2.CalendarView2.SHOW_DEFAULTS;
import static com.ptrstovka.calendarview2.CalendarView2.showOtherMonths;
import static com.ptrstovka.calendarview2.RangeDaySelectionCalculator.getSelectionsFor;
import static java.util.Calendar.DATE;

abstract class CalendarPagerView extends ViewGroup implements View.OnClickListener {

    protected static final int DEFAULT_DAYS_IN_WEEK = 7;
    protected static final int DEFAULT_MAX_WEEKS = 6;
    protected static final int DAY_NAMES_ROW = 1;
    private static final Calendar tempWorkingCalendar = CalendarUtils.getInstance();
    private final ArrayList<WeekDayView> weekDayViews = new ArrayList<>();
    private final ArrayList<DecoratorResult> decoratorResults = new ArrayList<>();
    @ShowOtherDates
    protected int showOtherDates = SHOW_DEFAULTS;
    private CalendarView2 mcv;
    private CalendarDay firstViewDay;
    private CalendarDay minDate = null;
    private CalendarDay maxDate = null;
    private int firstDayOfWeek;

    private final Collection<DayView> dayViews = new ArrayList<>();

    public CalendarPagerView(@NonNull CalendarView2 view,
                             CalendarDay firstViewDay,
                             int firstDayOfWeek) {
        super(view.getContext());
        this.mcv = view;
        this.firstViewDay = firstViewDay;
        this.firstDayOfWeek = firstDayOfWeek;

        setClipChildren(false);
        setClipToPadding(false);

        buildWeekDays(resetAndGetWorkingCalendar());
        buildDayViews(dayViews, resetAndGetWorkingCalendar());
    }

    private void buildWeekDays(Calendar calendar) {
        for (int i = 0; i < DEFAULT_DAYS_IN_WEEK; i++) {
            WeekDayView weekDayView = new WeekDayView(getContext(), CalendarUtils.getDayOfWeek(calendar));
            weekDayViews.add(weekDayView);
            addView(weekDayView);
            calendar.add(DATE, 1);
        }
    }

    protected void addDayView(Collection<DayView> dayViews, Calendar calendar) {
        CalendarDay day = CalendarDay.from(calendar);
        DayView dayView = new DayView(getContext(), day);
        dayView.setOnClickListener(this);
        dayViews.add(dayView);
        addView(dayView, new LayoutParams());

        calendar.add(DATE, 1);
    }

    protected Calendar resetAndGetWorkingCalendar() {
        getFirstViewDay().copyTo(tempWorkingCalendar);
        //noinspection ResourceType
        tempWorkingCalendar.setFirstDayOfWeek(getFirstDayOfWeek());
        int dow = CalendarUtils.getDayOfWeek(tempWorkingCalendar);
        int delta = getFirstDayOfWeek() - dow;
        //If the delta is positive, we want to remove a week
        boolean removeRow = showOtherMonths(showOtherDates) ? delta >= 0 : delta > 0;
        if (removeRow) {
            delta -= DEFAULT_DAYS_IN_WEEK;
        }
        tempWorkingCalendar.add(DATE, delta);
        return tempWorkingCalendar;
    }

    protected int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    protected abstract void buildDayViews(Collection<DayView> dayViews, Calendar calendar);

    protected abstract boolean isDayEnabled(CalendarDay day);

    void setDayViewDecorators(List<DecoratorResult> results) {
        this.decoratorResults.clear();
        if (results != null) {
            this.decoratorResults.addAll(results);
        }
        invalidateDecorators();
    }

    public void setWeekDayTextAppearance(int taId) {
        for (WeekDayView weekDayView : weekDayViews) {
            weekDayView.setTextAppearance(getContext(), taId);
        }
    }

    public void setDateTextAppearance(int taId) {
        for (DayView dayView : dayViews) {
            dayView.setTextAppearance(getContext(), taId);
        }
    }

    public void setDayCirclePadding(int padding) {
        for (DayView dayView : dayViews) {
            dayView.setCirclePadding(padding);
        }
    }

    public void setShowOtherDates(@ShowOtherDates int showFlags) {
        this.showOtherDates = showFlags;
        updateUi();
    }

    public void setSelectionEnabled(boolean selectionEnabled) {
        for (DayView dayView : dayViews) {
            dayView.setOnClickListener(selectionEnabled ? this : null);
            dayView.setClickable(selectionEnabled);
        }
    }

    public void setSelectionColor(int color) {
        for (DayView dayView : dayViews) {
            dayView.setSelectionColor(color);
        }
    }

    public void setWeekDayFormatter(WeekDayFormatter formatter) {
        for (WeekDayView dayView : weekDayViews) {
            dayView.setWeekDayFormatter(formatter);
        }
    }

    public void setDayFormatter(DayFormatter formatter) {
        for (DayView dayView : dayViews) {
            dayView.setDayFormatter(formatter);
        }
    }

    public void setMinimumDate(CalendarDay minDate) {
        this.minDate = minDate;
        updateUi();
    }

    public void setMaximumDate(CalendarDay maxDate) {
        this.maxDate = maxDate;
        updateUi();
    }

    public void setSelectedDates(Collection<CalendarDay> dates) {
        setSelectedDates(dates, false);
    }

    public void setSelectedDates(Collection<CalendarDay> dates, boolean onlyAdd) {

        if ((dates == null) || (dates.isEmpty() && !onlyAdd)) {
            for (DayView dayView : dayViews) {
                dayView.setSelection(DayView.SELECTION_NORMAL);
                dayView.setChecked(false);
            }

            postInvalidate();
            return;
        }

        if ((dates.size() == 1) && !onlyAdd) {
            for (DayView dayView : dayViews) {
                if (dates.contains(dayView.getDate())) {
                    dayView.setSelection(DayView.SELECTION_NORMAL);
                    dayView.setChecked(true);
                    break;
                }
            }

            postInvalidate();
            return;
        }

        ArrayList<CalendarDay> list = new ArrayList<>(dates);
        Collections.sort(list, new CalendarDayComparator());

        CalendarDay first = list.get(0);
        CalendarDay last = list.get(list.size() - 1);

        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();
            boolean isChecked = list.contains(day);

            if (!isChecked && !onlyAdd) {
                dayView.setSelection(DayView.SELECTION_NORMAL);
                dayView.setChecked(false);
                continue;
            }

            dayView.setChecked(true);

            boolean isFirstDayInRow = false;
            boolean isMiddleDayInRow = false;
            boolean isLastDayInRow = false;

            int lastDayOfWeek = firstDayOfWeek == Calendar.SUNDAY ?
                    Calendar.SATURDAY : Calendar.SUNDAY;
            int currentDayOfWeek = day.getCalendar().get(Calendar.DAY_OF_WEEK);

            if (firstDayOfWeek == currentDayOfWeek) {
                isFirstDayInRow = true;
            } else if (lastDayOfWeek == currentDayOfWeek) {
                isLastDayInRow = true;
            } else {
                isMiddleDayInRow = true;
            }

            boolean isFirstDay = first.equals(day);
            boolean isLastDay = last != null && last.equals(day);
            boolean isMiddleDay = !isFirstDay && !isLastDay;

            if (isFirstDay && isFirstDayInRow) {
                dayView.setSelection(DayView.SELECTION_FIRST);
            } else if (isFirstDay && isMiddleDayInRow) {
                dayView.setSelection(DayView.SELECTION_FIRST);
            } else if (isFirstDay && isLastDay) {
                dayView.setSelection(DayView.SELECTION_NORMAL);
            } else if (isMiddleDay && isFirstDayInRow) {
                dayView.setSelection(DayView.SELECTION_RANGE_LEFT);
            } else if (isMiddleDay && isMiddleDayInRow) {
                dayView.setSelection(DayView.SELECTION_RANGE);
            } else if (isMiddleDay && isLastDayInRow) {
                dayView.setSelection(DayView.SELECTION_RANGE_RIGHT);
            } else if (isLastDay && isFirstDayInRow) {
                dayView.setSelection(DayView.SELECTION_NORMAL);
            } else if (isLastDay && isMiddleDayInRow) {
                dayView.setSelection(DayView.SELECTION_LAST);
            } else if (isLastDay && isLastDayInRow) {
                dayView.setSelection(DayView.SELECTION_LAST);
            }
        }
        postInvalidate();
    }

    private static final String TAG = "CalendarPagerView";

    protected void updateUi() {
        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();
            dayView.setupSelection(
                    showOtherDates, day.isInRange(minDate, maxDate), isDayEnabled(day));
        }
        postInvalidate();
    }

    protected void invalidateDecorator(CalendarDay day) {
        for (DayView dayView : dayViews) {
            if (dayView.getDate().equals(day)) {
                DayViewFacade facade = new DayViewFacade();
                facade.reset();

                for (DecoratorResult decoratorResult : decoratorResults) {
                    applyDecorator(decoratorResult, dayView, facade);
                }

                dayView.applyFacade(facade);
                break;
            }
        }
    }

    private void applyDecorator(DecoratorResult result, DayView dayView, DayViewFacade facade) {
        if (result.decorator.shouldDecorate(dayView.getDate())){
            if (result.decorator.shouldDecorate(dayView.getDate())) {

                    if (!result.decorator.isCached()) {
                        result.result.reset();
                        result.decorator.decorate(result.result);
                    }

                result.result.applyTo(facade);
            }
        }
    }

    protected void invalidateDecorators() {
        final DayViewFacade facadeAccumulator = new DayViewFacade();
        for (DayView dayView : dayViews) {
            facadeAccumulator.reset();
            for (DecoratorResult result : decoratorResults) {
                applyDecorator(result, dayView, facadeAccumulator);
            }
            dayView.applyFacade(facadeAccumulator);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof DayView) {
            final DayView dayView = (DayView) v;
            mcv.onDateClicked(dayView);
        }
    }

    /*
     * Custom ViewGroup Code
     */

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final int specWidthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int specWidthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int specHeightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int specHeightMode = MeasureSpec.getMode(heightMeasureSpec);

        //We expect to be somewhere inside a MaterialCalendarView, which should measure EXACTLY
        if (specHeightMode == MeasureSpec.UNSPECIFIED || specWidthMode == MeasureSpec.UNSPECIFIED) {
            throw new IllegalStateException("CalendarPagerView should never be left to decide it's size");
        }

        //The spec width should be a correct multiple
        final int measureTileWidth = specWidthSize / DEFAULT_DAYS_IN_WEEK;
        final int measureTileHeight = specHeightSize / getRows();

        //Just use the spec sizes
        setMeasuredDimension(specWidthSize, specHeightSize);

        int count = getChildCount();

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileWidth,
                    MeasureSpec.EXACTLY
            );

            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(
                    measureTileHeight,
                    MeasureSpec.EXACTLY
            );

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    /**
     * Return the number of rows to display per page
     * @return
     */
    protected abstract int getRows();

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int count = getChildCount();

        final int parentLeft = 0;

        int childTop = 0;
        int childLeft = parentLeft;

        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);

            final int width = child.getMeasuredWidth();
            final int height = child.getMeasuredHeight();

            child.layout(childLeft, childTop, childLeft + width, childTop + height);

            childLeft += width;

            //We should warp every so many children
            if (i % DEFAULT_DAYS_IN_WEEK == (DEFAULT_DAYS_IN_WEEK - 1)) {
                childLeft = parentLeft;
                childTop += height;
            }

        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams();
    }

    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams();
    }


    @Override
    public void onInitializeAccessibilityEvent(@NonNull AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(CalendarPagerView.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(@NonNull AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(CalendarPagerView.class.getName());
    }

    protected CalendarDay getFirstViewDay() {
        return firstViewDay;
    }

    /**
     * Simple layout params class for MonthView, since every child is the same size
     */
    protected static class LayoutParams extends MarginLayoutParams {

        /**
         * {@inheritDoc}
         */
        public LayoutParams() {
            //noinspection ResourceType
            super(WRAP_CONTENT, WRAP_CONTENT);
        }
    }

    // ----------------- custom code ------------------ //

    public void selectRange(Range range) {
        Log.d(TAG, "selectRanges: called select range on pager view: " + range.toString());

        Map<CalendarDay, Integer> results = getSelectionsFor(range);

        for (DayView dayView : dayViews) {
            CalendarDay day = dayView.getDate();

            if (range.isInRange(day)) {
                dayView.setChecked(true);

                Integer selection = results.get(day);

                if (selection != null) {
                    //noinspection WrongConstant
                    dayView.setSelection(selection);
                }
            }
        }
    }

}

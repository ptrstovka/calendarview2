package com.ptrstovka.calendarview2;

/**
 * Decorate Day views with drawables and text manipulation
 */
public abstract class DayViewDecorator {

    /**
     * Determine if a specific day should be decorated
     *
     * @param day {@linkplain CalendarDay} to possibly decorate
     * @return true if this decorator should be applied to the provided day
     */
    abstract public boolean shouldDecorate(CalendarDay day);

    /**
     * Set decoration options onto a facade to be applied to all relevant days
     *
     * @param view View to decorate
     */
    abstract public void decorate(DayViewFacade view);

    protected boolean shouldBeCached() {
        return true;
    }

    boolean isCached() {
        return this.shouldBeCached();
    }

}

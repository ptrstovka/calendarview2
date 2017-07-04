package com.ptrstovka.calendarview2.sample.decorators;

import android.app.Activity;
import android.graphics.drawable.Drawable;

import com.ptrstovka.calendarview2.CalendarDay;
import com.ptrstovka.calendarview2.DayViewDecorator;
import com.ptrstovka.calendarview2.DayViewFacade;
import com.ptrstovka.calendarview2.sample.R;

/**
 * Use a custom selector
 */
public class MySelectorDecorator extends DayViewDecorator {

    private final Drawable drawable;

    public MySelectorDecorator(Activity context) {
        drawable = context.getResources().getDrawable(R.drawable.my_selector);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(drawable);
    }
}

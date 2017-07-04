package com.ptrstovka.calendarview2;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.text.SpannableString;
import android.text.Spanned;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckedTextView;

import com.ptrstovka.calendarview2.CalendarView2.ShowOtherDates;
import com.ptrstovka.calendarview2.format.DayFormatter;
import com.ptrstovka.calendarview2.utils.ObjectHelper;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

import static com.ptrstovka.calendarview2.CalendarView2.SHOW_DEFAULTS;
import static com.ptrstovka.calendarview2.CalendarView2.showDecoratedDisabled;
import static com.ptrstovka.calendarview2.CalendarView2.showOtherMonths;
import static com.ptrstovka.calendarview2.CalendarView2.showOutOfRange;
import static com.ptrstovka.calendarview2.utils.ColorUtils.lighter;
import static com.ptrstovka.calendarview2.utils.ColorUtils.transparent;

/**
 * Display one day of a {@linkplain CalendarView2}
 */
@SuppressLint("ViewConstructor")
class DayView extends CheckedTextView {

    private CalendarDay date;
    private int selectionColor = Color.GRAY;
    private int transparentSelectionColor = transparent(Color.GRAY, 0.5f);

    private final int fadeTime;
    private Drawable customBackground = null;
    private Drawable selectionDrawable;
    private Drawable mCircleDrawable;
    private DayFormatter formatter = DayFormatter.DEFAULT;

    private boolean isInRange = true;
    private boolean isInMonth = true;
    private boolean isDecoratedDisabled = false;

    private int circlePadding = 0;

    @Selection
    private int selection = SELECTION_NORMAL;

    public static final int SELECTION_NORMAL = 309;
    public static final int SELECTION_FIRST = 568;
    public static final int SELECTION_LAST = 270;
    public static final int SELECTION_RANGE = 574;
    public static final int SELECTION_RANGE_LEFT = 843;
    public static final int SELECTION_RANGE_RIGHT = 893;

    private Rect rangeRect = new Rect();
    private Rect leftRect = new Rect();
    private Rect rightRect = new Rect();
    private Paint rectPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    @Retention(RetentionPolicy.SOURCE)
    @IntDef(flag = true, value = {
            SELECTION_FIRST, SELECTION_LAST, SELECTION_NORMAL, SELECTION_RANGE,
            SELECTION_RANGE_LEFT, SELECTION_RANGE_RIGHT
    })
    public @interface Selection {}

    @ShowOtherDates
    private int showOtherDates = SHOW_DEFAULTS;

    public DayView(Context context, CalendarDay day) {
        super(context);

        fadeTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        setSelectionColor(this.selectionColor);

        setSelection(SELECTION_NORMAL);

        setGravity(Gravity.CENTER);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            setTextAlignment(TEXT_ALIGNMENT_CENTER);
        }

        setDay(day);
    }

    public void setSelection(@Selection int selection) {
        this.selection = selection;

//        switch (this.selection) {
//            case SELECTION_NORMAL:
//                selectionColor = primaryColor; // klasika
//                break;
//            case SELECTION_FIRST:
//                selectionColor = Color.RED; // tmavy a svetly rect na lavej strane
//                break;
//            case SELECTION_RANGE:
//                selectionColor = Color.GREEN; // cely svetly
//                break;
//            case SELECTION_LAST:
//                selectionColor = Color.YELLOW; // tmavy a svetly rect na pravej strane
//                break;
//            case SELECTION_RANGE_LEFT:
//                selectionColor = Color.BLACK; // svetly -> na lavej strane
//                break;
//            case SELECTION_RANGE_RIGHT:
//                selectionColor = Color.CYAN; // svetly <- na pravej strane
//                break;
//        }

        regenerateBackground();
    }

    public void setDay(CalendarDay date) {
        this.date = date;
        setText(getLabel());
    }

    public void setCirclePadding(int padding) {
        circlePadding = padding;
        requestLayout();
    }

    /**
     * Set the new label formatter and reformat the current label. This preserves current spans.
     *
     * @param formatter new label formatter
     */
    public void setDayFormatter(DayFormatter formatter) {
        this.formatter = formatter == null ? DayFormatter.DEFAULT : formatter;
        CharSequence currentLabel = getText();
        Object[] spans = null;
        if (currentLabel instanceof Spanned) {
            spans = ((Spanned) currentLabel).getSpans(0, currentLabel.length(), Object.class);
        }
        SpannableString newLabel = new SpannableString(getLabel());
        if (spans != null) {
            for (Object span : spans) {
                newLabel.setSpan(span, 0, newLabel.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        setText(newLabel);
    }

    @NonNull
    public String getLabel() {
        return formatter.format(date);
    }

    public void setSelectionColor(int color) {
        this.selectionColor = color;
        this.transparentSelectionColor = lighter(color, 0.5f);
        regenerateBackground();
    }

    /**
     * @param drawable custom selection drawable
     */
    public void setSelectionDrawable(Drawable drawable) {
        if (ObjectHelper.equals(drawable, selectionDrawable)) {
            return;
        }

        if (drawable == null) {
            this.selectionDrawable = null;
        } else {
            this.selectionDrawable = drawable.getConstantState().newDrawable(getResources());
        }
        regenerateBackground();
    }

    /**
     * @param drawable background to draw behind everything else
     */
    public void setCustomBackground(Drawable drawable) {

        if (ObjectHelper.equals(drawable, customBackground)) {
            return;
        }

        if (drawable == null) {
            this.customBackground = null;
        } else {
            this.customBackground = drawable.getConstantState().newDrawable(getResources());
        }
        invalidate();
    }

    public CalendarDay getDate() {
        return date;
    }

    private void setEnabled() {
        boolean enabled = isInMonth && isInRange && !isDecoratedDisabled;
        super.setEnabled(isInRange && !isDecoratedDisabled);

        boolean showOtherMonths = showOtherMonths(showOtherDates);
        boolean showOutOfRange = showOutOfRange(showOtherDates) || showOtherMonths;
        boolean showDecoratedDisabled = showDecoratedDisabled(showOtherDates);

        boolean shouldBeVisible = enabled;

        if (!isInMonth && showOtherMonths) {
            shouldBeVisible = true;
        }

        if (!isInRange && showOutOfRange) {
            shouldBeVisible |= isInMonth;
        }

        if (isDecoratedDisabled && showDecoratedDisabled) {
            shouldBeVisible |= isInMonth && isInRange;
        }

        if (!isInMonth && shouldBeVisible) {
            setTextColor(getTextColors().getColorForState(
                    new int[]{-android.R.attr.state_enabled}, Color.GRAY));
        }
        setVisibility(shouldBeVisible ? View.VISIBLE : View.INVISIBLE);
    }

    protected void setupSelection(@ShowOtherDates int showOtherDates, boolean inRange, boolean inMonth) {
        this.showOtherDates = showOtherDates;
        this.isInMonth = inMonth;
        this.isInRange = inRange;
        setEnabled();
    }

    private final Rect tempRect = new Rect();
    private final Rect circleDrawableRect = new Rect();

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        drawStateBackground(canvas);
        if (customBackground != null) {
            customBackground.setBounds(tempRect);
            customBackground.setState(getDrawableState());
            customBackground.draw(canvas);
        }

        mCircleDrawable.setBounds(circleDrawableRect);
        mCircleDrawable.draw(canvas);

        super.onDraw(canvas);
    }

    private void drawStateBackground(Canvas canvas) {

//        if (selection == SELECTION_NORMAL) {
//            rectPaint.setColor(Color.TRANSPARENT);
//            canvas.drawRect(rangeRect, rectPaint);
//            return;
//        }

        if (selection == SELECTION_RANGE) {
            rectPaint.setColor(transparentSelectionColor);
            canvas.drawRect(rangeRect, rectPaint);
            return;
        }

        if (selection == SELECTION_RANGE_LEFT || selection == SELECTION_FIRST) {
            rectPaint.setColor(transparentSelectionColor);
            canvas.drawRect(rightRect, rectPaint);
        }

        if (selection == SELECTION_RANGE_RIGHT || selection == SELECTION_LAST) {
            rectPaint.setColor(transparentSelectionColor);
            canvas.drawRect(leftRect, rectPaint);
        }

    }

    private void regenerateBackground() {
        if (selectionDrawable != null) {
            setBackgroundDrawable(selectionDrawable);
        } else {
            mCircleDrawable = generateBackground(selection, selectionColor,
                    transparentSelectionColor, fadeTime, circleDrawableRect);
            setBackgroundDrawable(mCircleDrawable);
        }
    }

    private static Drawable generateBackground(@Selection int selection, int color, int transparentColor, int fadeTime, Rect bounds) {
        StateListDrawable drawable = new StateListDrawable();
        drawable.setExitFadeDuration(fadeTime);

        int resultColor = (selection == SELECTION_NORMAL || selection == SELECTION_FIRST || selection == SELECTION_LAST)
                        ? color : transparentColor;

        drawable.addState(new int[]{android.R.attr.state_checked}, generateCircleDrawable(resultColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable.addState(new int[]{android.R.attr.state_pressed}, generateRippleDrawable(resultColor, bounds));
        } else {
            drawable.addState(new int[]{android.R.attr.state_pressed}, generateCircleDrawable(transparent(resultColor, 0.5f)));
        }

        drawable.addState(new int[]{}, generateCircleDrawable(Color.TRANSPARENT));

        return drawable;
    }

    private static Drawable generateCircleDrawable(final int color) {
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(color);
        return drawable;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static Drawable generateRippleDrawable(final int color, Rect bounds) {
        ColorStateList list = ColorStateList.valueOf(color);
        Drawable mask = generateCircleDrawable(Color.WHITE);
        RippleDrawable rippleDrawable = new RippleDrawable(list, null, mask);
//        API 21
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
            rippleDrawable.setBounds(bounds);
        }

//        API 22. Technically harmless to leave on for API 21 and 23, but not worth risking for 23+
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1) {
            int center = (bounds.left + bounds.right) / 2;
            rippleDrawable.setHotspotBounds(center, bounds.top, center, bounds.bottom);
        }

        return rippleDrawable;
    }

    /**
     * @param facade apply the facade to us
     */
    void applyFacade(DayViewFacade facade) {
        this.isDecoratedDisabled = facade.areDaysDisabled();
        setEnabled();

        setCustomBackground(facade.getBackgroundDrawable());
        setSelectionDrawable(facade.getSelectionDrawable());

        // Facade has spans
        List<DayViewFacade.Span> spans = facade.getSpans();
        if (!spans.isEmpty()) {
            String label = getLabel();
            SpannableString formattedLabel = new SpannableString(getLabel());
            for (DayViewFacade.Span span : spans) {
                formattedLabel.setSpan(span.span, 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            setText(formattedLabel);
        }
        // Reset in case it was customized previously
        else {
            setText(getLabel());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (changed) {
            invalidateBackground(right - left, bottom - top);
        }
    }

    private void invalidateBackground(int width, int height) {
        calculateBounds(width, height);
        regenerateBackground();
    }

    private void calculateBounds(int width, int height) {

        int totalWidth = width - (2*circlePadding);
        int totalHeight = height - (2* circlePadding);

        final int radius = Math.min(totalHeight, totalWidth);
        final int offset = Math.abs(totalHeight - totalWidth) / 2;

        // Lollipop platform bug. Circle drawable offset needs to be half of normal offset
        final int circleOffset = Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP ? offset / 2 : offset;

        if (totalWidth >= totalHeight) {
            tempRect.set(offset, 0, radius + offset, totalHeight);
            circleDrawableRect.set(
                    circleOffset + circlePadding,
                    circlePadding,
                    radius + circleOffset + circlePadding,
                    totalHeight + circlePadding
            );
        } else {
            tempRect.set(0, offset, totalWidth, radius + offset);
            circleDrawableRect.set(
                    circlePadding,
                    circleOffset + circlePadding,
                    totalWidth + circlePadding,
                    radius + circleOffset + circlePadding
            );
        }

        // here calculates rect
        rangeRect.set(0, circlePadding, width, height - circlePadding);
        leftRect.set(0, circlePadding, width / 2, height - circlePadding);
        rightRect.set(width / 2, circlePadding, width, height - circlePadding);
    }
}

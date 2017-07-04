package com.ptrstovka.calendarview2.utils;

import static android.graphics.Color.alpha;
import static android.graphics.Color.argb;
import static android.graphics.Color.blue;
import static android.graphics.Color.green;
import static android.graphics.Color.red;
import static java.lang.Math.round;

/**
 * Created by: Peter Štovka <stovka.peter@gmail.com>
 * Created at: 6/28/17.
 */

public class ColorUtils {

    public static int transparent(int color, float alpha) {
        return argb(round(alpha(color) * alpha), red(color), green(color), blue(color));
    }

    public static int lighter(int color, float factor) {
        int red = (int) ((red(color) * (1 - factor) / 255 + factor) * 255);
        int green = (int) ((green(color) * (1 - factor) / 255 + factor) * 255);
        int blue = (int) ((blue(color) * (1 - factor) / 255 + factor) * 255);
        return argb(alpha(color), red, green, blue);
    }

}

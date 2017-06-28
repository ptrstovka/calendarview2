package com.prolificinteractive.materialcalendarview.utils;

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

}

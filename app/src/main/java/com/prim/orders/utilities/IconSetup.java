package com.prim.orders.utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class IconSetup {
    public static Drawable addMarginToDrawable(Drawable drawable, int margin) {
        if (drawable == null) {
            return null;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();

        Bitmap bitmapWithMargin = Bitmap.createBitmap(width + margin, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapWithMargin);

        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);

        return new BitmapDrawable(bitmapWithMargin);
    }
}

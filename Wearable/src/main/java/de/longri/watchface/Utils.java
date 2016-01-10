package de.longri.watchface;


import android.graphics.*;
import android.graphics.ColorMatrix;


/**
 * Utility and fast math functions.
 * <p/>
 * Thanks to Riven on JavaGaming.org for the basis of sin/cos/atan2/floor/ceil.
 *
 * @author Nathan Sweet
 */
public final class Utils {

    private static Rect bounds = new Rect();

    public static void drawString(Canvas canvas, final Paint paint, final String str, int x, int y) {
        final String[] lines = str.split("\n");

        int yOff = 0;
        for (int i = 0; i < lines.length; ++i) {
            canvas.drawText(lines[i], x, y + yOff, paint);
            paint.getTextBounds(lines[i], 0, lines[i].length(), bounds);
            yOff += bounds.height();
        }
    }

    static public final float PI = 3.1415927f;
    static private final int SIN_BITS = 14; // 16KB. Adjust for accuracy.
    static private final int SIN_MASK = ~(-1 << SIN_BITS);
    static private final int SIN_COUNT = SIN_MASK + 1;

    static private final float radFull = PI * 2;
    static private final float degFull = 360;
    static private final float radToIndex = SIN_COUNT / radFull;
    static private final float degToIndex = SIN_COUNT / degFull;

    /**
     * multiply by this to convert from radians to degrees
     */
    static public final float radiansToDegrees = 180f / PI;
    /**
     * multiply by this to convert from degrees to radians
     */
    static public final float degreesToRadians = PI / 180;


    static private class Sin {
        static final float[] table = new float[SIN_COUNT];

        static {
            for (int i = 0; i < SIN_COUNT; i++)
                table[i] = (float) Math.sin((i + 0.5f) / SIN_COUNT * radFull);
            for (int i = 0; i < 360; i += 90)
                table[(int) (i * degToIndex) & SIN_MASK] = (float) Math.sin(i * degreesToRadians);
        }
    }

    /**
     * Returns the sine in radians from a lookup table.
     */
    static public float sin(float radians) {
        return Sin.table[(int) (radians * radToIndex) & SIN_MASK];
    }

    /**
     * Returns the cosine in radians from a lookup table.
     */
    static public float cos(float radians) {
        return Sin.table[(int) ((radians + PI / 2) * radToIndex) & SIN_MASK];
    }


    /**
     * Ambient Color Matrix
     */
    public static final ColorMatrix cm = new ColorMatrix(de.longri.watchface.ColorMatrix.AMBIENT);


    public static int mapValues(int srcMin, int srcMax, int targetMin, int targetMax, int value) {
        double v = ((double) (value - srcMin)) / ((double) (srcMax - srcMin));
        return (int) (v * (targetMax - targetMin) + targetMin);
    }


    public static Bitmap scaleBitmap(float scale, Bitmap bitmap) {
        if (scale == 1) return bitmap; // return original

        int scaledWidth = (int) (bitmap.getWidth() * scale);
        int scaledHeight = (int) (bitmap.getHeight() * scale);
        bitmap = bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
        return bitmap;
    }

}
/*
 * Copyright (C) 2015-2016 longri.de
 *
 * Licensed under the : GNU General Public License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.longri.watchface;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.text.format.Time;

/**
 * Created by Longri on 17.11.15.
 */
public class RES {

    private RES() {
    }


    private static Theme mTheme;
    public static final Paint mAntiAliasPaint = new Paint();
    public static final Paint mAntiAliasPaint_noGreyScale = new Paint();
    public static Time mTime = new Time();
    private static int mLastMinute = -1;
    private static int mLastTinyMinute = -1;

    public static boolean mMinuteChanged = false;

    static {
        mAntiAliasPaint.setFilterBitmap(true);
        mAntiAliasPaint_noGreyScale.setFilterBitmap(true);
    }

    public static void setTheme(Theme theme) {
        mTheme = theme;
    }

    public static int getTextColor() {
        return mTheme.TextColor;
    }

    public static Bitmap getAmbientBackGround() {
        return mTheme.BackGround;
    }

    public static Bitmap get12BackGround() {
        return mTheme.BackGround12;
    }

    public static Bitmap get24BackGround() {
        return mTheme.BackGround24;
    }

    public static Bitmap getTinyAmbientBackground() {
        return mTheme.tinyAmbientBackgroundBitmap;
    }

    public static Bitmap getTinyBackground() {
        return mTheme.tinyBackgroundBitmap;
    }

    public static Matrix getMatrixForPos(int position) {
        return mTheme.matrix[position];
    }

    public static Bitmap getTinyClock12Bitmap() {
        return mTheme.tinyClock12Background;
    }

    public static Bitmap getTinyClock24Bitmap() {
        return mTheme.tinyClock24Background;
    }

    public static Bitmap getTinyHandMinuteBitmap() {
        return mTheme.tinyHandMinuteBitmap;
    }

    public static Bitmap getWatchHandHourBitmap() {
        return mTheme.watchHandHourBitmap;
    }

    public static Bitmap getWatchHandMinuteBitmap() {
        return mTheme.watchHandMinuteBitmap;
    }

    public static Bitmap getWatchHandMinuteBitmapAmbient() {
        return mTheme.watchHandMinuteBitmapAmbient;
    }

    public static Bitmap getWatchHandHourBitmapAmbient() {
        return mTheme.watchHandHourBitmapAmbient;
    }

    public static Bitmap getTinyHandHourBitmap() {
        return mTheme.tinyHandHourBitmap;
    }

    public static Bitmap getTinyAmbientLogo() {
        return mTheme.tinyAmbientLogoBitmap;
    }

    public static Bitmap getTinyLogo() {
        return mTheme.tinyLogoBitmap;
    }

    public static Matrix getTinyHourHandMatrix() {
        return mTheme.tinyHourHandMatrix;
    }

    public static Matrix getTinyMinuteHandMatrix() {
        return mTheme.tinyMinuteHandMatrix;
    }

    public static void setHandsMatrix(Time time, boolean tiny, Matrix minuteMatrix, Matrix hourMatrix, float centerX, float centerY) {

        int lastMinute = tiny ? mLastTinyMinute : mLastMinute;
        if (lastMinute == -1 || lastMinute != time.minute || tiny) {
            if (tiny) {
                mLastTinyMinute = time.minute;
            } else {
                mLastMinute = time.minute;
                mMinuteChanged = true; // reset on end of onDraw!
            }
            minuteMatrix.reset();
            minuteMatrix.postRotate((time.minute / 30f * Utils.PI) * Utils.radiansToDegrees, centerX, centerY);

            hourMatrix.reset();
            hourMatrix.postRotate((((time.hour + (time.minute / 60f)) / 6f) * Utils.PI) * Utils.radiansToDegrees, centerX, centerY);
        }
    }
}

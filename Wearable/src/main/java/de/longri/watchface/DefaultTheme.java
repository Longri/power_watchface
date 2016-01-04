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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;

/**
 * Created by Longri on 17.11.15.
 */
public class DefaultTheme extends Theme {

    public static Resources resources;


    public DefaultTheme() {

    }


    Matrix bottomMatrix;
    Matrix topMatrix;
    Matrix leftMatrix;
    Matrix rightMatrix;
    Matrix tinyHourHandMatrix;
    Matrix tinyMinuteHandMatrix;

    @Override
    protected Bitmap getAmbientBackGround() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.bg)).getBitmap();
    }

    @Override
    protected Bitmap getTinyAmbientBackground() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.tiny_ambient_back)).getBitmap();
    }

    @Override
    protected Bitmap getTinyBackground() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.tiny_back)).getBitmap();
    }

    @Override
    protected Bitmap get24Background() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.back_24)).getBitmap();
    }

    @Override
    protected Bitmap get12Background() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.back_12)).getBitmap();
    }

    @Override
    protected int getTextColor() {
        return Color.WHITE;
    }

    @Override
    protected Matrix[] getMatrix() {
        if (bottomMatrix == null) calcMatrix();
        return new Matrix[]{topMatrix, rightMatrix, bottomMatrix, leftMatrix};
    }

    @Override
    protected Bitmap getTinyClock12Background() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.tiny_clock_12)).getBitmap();
    }

    @Override
    protected Bitmap getTinyClock24Background() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.tiny_clock_24)).getBitmap();
    }

    @Override
    protected Bitmap getTinyHandMinuteBitmap() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.tiny_hand_minute)).getBitmap();
    }

    @Override
    protected Bitmap getTinyHandHourBitmap() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.tiny_hand_hour)).getBitmap();
    }

    @Override
    protected Bitmap getWatchHandHourBitmap() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.hand_hour)).getBitmap();
    }

    @Override
    protected Bitmap getWatchHandMinuteBitmap() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.hand_minute)).getBitmap();
    }

    @Override
    protected Bitmap getWatchHandMinuteBitmapAmbient() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.hand_minute_ambient)).getBitmap();
    }

    @Override
    protected Bitmap getWatchHandHourBitmapAmbient() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.hand_hour_ambient)).getBitmap();
    }

    @Override
    protected Bitmap getAmbientLogoBitmap() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.ambient_logo)).getBitmap();
    }

    @Override
    protected Bitmap getLogoBitmap() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.logo)).getBitmap();
    }

    @Override
    protected Matrix getTinyHourHandMatrix() {
        if (tinyHourHandMatrix == null) tinyHourHandMatrix = new Matrix();
        return tinyHourHandMatrix;
    }

    @Override
    protected Matrix getTinyMinuteHandMatrix() {
        if (tinyMinuteHandMatrix == null) tinyMinuteHandMatrix = new Matrix();
        return tinyMinuteHandMatrix;
    }

    private void calcMatrix() {
        int width = bounds.width();
        int height = bounds.height();
        float tinyMargin = width / 17f;
        int tinyWidth = getTinyBackground().getWidth();
        float x = height / 2 - tinyWidth / 2;
        float y = x * 2 - tinyMargin;
        bottomMatrix = new Matrix();
        bottomMatrix.postTranslate(x, y);
        topMatrix = new Matrix();
        topMatrix.postTranslate(x, tinyMargin);
        leftMatrix = new Matrix();
        leftMatrix.postTranslate(tinyMargin, x);
        rightMatrix = new Matrix();
        rightMatrix.postTranslate(y, x);
    }
}

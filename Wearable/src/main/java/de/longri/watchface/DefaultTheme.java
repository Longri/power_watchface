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
import android.util.Log;


/**
 * Created by Longri on 17.11.15.
 */
public class DefaultTheme extends Theme {

    public static Resources resources;

    final Matrix bottomMatrix;
    final Matrix topMatrix;
    final Matrix leftMatrix;
    final Matrix rightMatrix;
    Matrix tinyHourHandMatrix;
    Matrix tinyMinuteHandMatrix;


    public DefaultTheme() {
        bottomMatrix = new Matrix();
        topMatrix = new Matrix();
        leftMatrix = new Matrix();
        rightMatrix = new Matrix();
        matrix = getMatrix();
    }


    @Override
    protected Bitmap getBackGround() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.background)).getBitmap();
    }

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
    protected Bitmap getScale() {
        return ((BitmapDrawable) resources.getDrawable(R.drawable.scale)).getBitmap();
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
        if (bottomMatrix.isIdentity()) calcMatrix();
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

    boolean mScaleIsDrawing = false;

    @Override
    protected void setScaleIsDrawing(boolean scaleIsDrawn) {
        if (mScaleIsDrawing != scaleIsDrawn) {
            mScaleIsDrawing = scaleIsDrawn;
            reCalcmatrix();
        }
    }

    private void reCalcmatrix() {
        synchronized (bottomMatrix) {
            bottomMatrix.reset();
        }
    }


    int tinyScale = 50;

    @Override
    public void setTinyScale(byte value) {
        if (tinyScale != value) {
            tinyScale = value;
            reCalcmatrix();
        }
    }

    TotalisatorOffset _totalisatorOffset;

    @Override
    public void setTotalisatorOffset(TotalisatorOffset totalisatorOffset) {
        if (totalisatorOffset == null) return;
        if (_totalisatorOffset == null || !_totalisatorOffset.equals(totalisatorOffset)) {
            _totalisatorOffset = totalisatorOffset;
            reCalcmatrix();
        }
    }

    int tinyMargin = 50;

    @Override
    public void setTinyMargin(byte value) {
        Log.d("W", "setTinyMargin " + value);

        if (tinyMargin != value) {
            tinyMargin = value;
            reCalcmatrix();
        }
    }

    private float getMultiplier(float value) {
        if (value >= 50) return value / 50;
        float v = Utils.mapValues(0, 50, 25, 50, value);
        return v / 50.0f;
    }


    private void calcMatrix() {
        synchronized (bottomMatrix) {
            int width = (int) (bounds.width());
            int height = (int) (bounds.height());

            float marginMultiplier = getMultiplier(tinyMargin);
            float scaleMultiplier = getMultiplier(Utils.mapValues(0f, 100f, 30f, 60f, tinyScale));
            Log.d("W", "Margin multi: " + marginMultiplier);

            float tinyMargin = (width / 16f) * marginMultiplier;

            //float scale = 1f;
            float invertScale = (1 / scaleMultiplier);

            if (mScaleIsDrawing) {
                tinyMargin += 15 * scaleFactor;
            }

            int tinyWidth = (int) (getTinyBackground().getWidth() * scaleFactor);
            float x = ((height * invertScale) / 2) - (tinyWidth / 2);
            // float y = (height * invertScale) - ((tinyMargin * invertScale) + (tinyWidth * invertScale));

            float y = x * 2 - ((tinyMargin) * invertScale);

            tinyMargin *= invertScale;

            bottomMatrix.reset();
            topMatrix.reset();
            leftMatrix.reset();
            rightMatrix.reset();

            bottomMatrix.postScale(scaleMultiplier, scaleMultiplier);
            topMatrix.postScale(scaleMultiplier, scaleMultiplier);
            leftMatrix.postScale(scaleMultiplier, scaleMultiplier);
            rightMatrix.postScale(scaleMultiplier, scaleMultiplier);

            bottomMatrix.preTranslate(x, y);
            topMatrix.preTranslate(x, tinyMargin);
            leftMatrix.preTranslate(tinyMargin, x);
            rightMatrix.preTranslate(y, x);

            // set totalisator offset
            if (_totalisatorOffset != null) {
                bottomMatrix.preTranslate(_totalisatorOffset.getBottom().get_x(), _totalisatorOffset.getBottom().get_y());
                topMatrix.preTranslate(_totalisatorOffset.getTop().get_x(), _totalisatorOffset.getTop().get_y());
                leftMatrix.preTranslate(_totalisatorOffset.getLeft().get_x(), _totalisatorOffset.getLeft().get_y());
                rightMatrix.preTranslate(_totalisatorOffset.getRight().get_x(), _totalisatorOffset.getRight().get_y());
            }
        }

    }
}

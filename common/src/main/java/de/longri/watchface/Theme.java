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
import android.graphics.Rect;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.Serializable;
import de.longri.serializable.StoreBase;

/**
 * Created by Longri on 17.11.15.
 */
public abstract class Theme implements Serializable {

    public static Rect bounds;
    public static float scaleFactor;

    //TODO create implementation of IPlugin like cachebox

    public final Bitmap BackGround = getBackGround();
    public final Bitmap AmbientBackGround = getAmbientBackGround();
    public final Bitmap tinyAmbientBackgroundBitmap = getTinyAmbientBackground();
    public final Bitmap tinyBackgroundBitmap = getTinyBackground();
    public final Bitmap Scale = getScale();
    public final Bitmap BackGround24 = get24Background();
    public final Bitmap BackGround12 = get12Background();
    public final int TextColor = getTextColor();
    public Matrix[] matrix = getMatrix();
    public final Bitmap tinyClock12Background = getTinyClock12Background();
    public final Bitmap tinyClock24Background = getTinyClock24Background();
    public final Bitmap tinyHandMinuteBitmap = getTinyHandMinuteBitmap();
    public final Bitmap tinyHandHourBitmap = getTinyHandHourBitmap();
    public final Matrix tinyHourHandMatrix = getTinyHourHandMatrix();
    public final Matrix tinyMinuteHandMatrix = getTinyMinuteHandMatrix();
    public final Bitmap watchHandHourBitmap = getWatchHandHourBitmap();
    public final Bitmap watchHandMinuteBitmap = getWatchHandMinuteBitmap();
    public final Bitmap tinyAmbientLogoBitmap = getAmbientLogoBitmap();
    public final Bitmap tinyLogoBitmap = getLogoBitmap();
    public final Bitmap watchHandHourBitmapAmbient = getWatchHandHourBitmapAmbient();
    public final Bitmap watchHandMinuteBitmapAmbient = getWatchHandMinuteBitmapAmbient();


    @Override
    public void serialize(StoreBase storeBase) throws NotImplementedException {

    }

    @Override
    public void deserialize(StoreBase storeBase) throws NotImplementedException {

    }

    protected abstract Bitmap getBackGround();

    protected abstract Bitmap getAmbientBackGround();

    protected abstract Bitmap getTinyAmbientBackground();

    protected abstract Bitmap getTinyBackground();

    protected abstract Bitmap getScale();

    protected abstract Bitmap get24Background();

    protected abstract Bitmap get12Background();

    protected abstract int getTextColor();

    protected abstract Matrix[] getMatrix();

    protected abstract Bitmap getTinyClock12Background();

    protected abstract Bitmap getTinyClock24Background();

    protected abstract Bitmap getTinyHandMinuteBitmap();

    protected abstract Bitmap getTinyHandHourBitmap();

    protected abstract Bitmap getWatchHandHourBitmap();

    protected abstract Bitmap getWatchHandMinuteBitmap();

    protected abstract Bitmap getWatchHandMinuteBitmapAmbient();

    protected abstract Bitmap getWatchHandHourBitmapAmbient();


    protected abstract Bitmap getAmbientLogoBitmap();

    protected abstract Bitmap getLogoBitmap();

    protected abstract Matrix getTinyHourHandMatrix();

    protected abstract Matrix getTinyMinuteHandMatrix();

    protected abstract void setScaleIsDrawing(boolean scaleIsDrawn);

    public abstract void setTinyMargin(byte value);

    public abstract void setTinyScale(byte value);

    public abstract void setTotalisatorOffset(TotalisatorOffset totalisatorOffset);
}


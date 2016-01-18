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

import android.content.Context;
import android.graphics.*;
import android.text.format.Time;

import java.util.TimeZone;

/**
 * Created by Longri on 18.11.2015.
 */
public class TinyClockDrawable extends TinyDrawable {

    private Paint datePaint;
    private WearTimeZone timeZone = WearTimeZone.getAvailableTimeZones()[25];
    private Time lastDrawTime = new Time();
    private boolean digital = false;
    private Time mTime = new Time();


    public TinyClockDrawable(Context context) {
        super(context);
    }

    public void setTimeZone(WearTimeZone timeZone) {
        this.timeZone = timeZone;
        this.setMustRedraw();
        this.Invalidate();
    }

    @Override
    public boolean hasFullDraw() {
        return false;
    }

    @Override
    public boolean draw(Canvas canvas, Matrix matrix) {

        drawTinyBackground(canvas, matrix);
        if (!digital) canvas.drawBitmap(RES.getTinyClock12Bitmap(), matrix, RES.mAntiAliasPaint);


        float centerX = RES.getTinyHandMinuteBitmap().getWidth() / 2f;
        float centerY = RES.getTinyHandMinuteBitmap().getHeight() / 2f;

        canvas.setMatrix(matrix);

        if (!digital) DrawAnalog(canvas, centerX, centerY);
        if (digital) DrawDigital(canvas, centerX, centerY);

        canvas.setMatrix(null);

        lastDrawTime.set(mTime.toMillis(false));

        return false; //TODO draw into Buffer
    }


    public void setDigital(boolean value) {
        if (value != digital) {
            digital = value;
            this.setMustRedraw();
        }
    }


    private void DrawAnalog(Canvas canvas, float centerX, float centerY) {
        setTimeZoneOffset();
        RES.setHandsMatrix(mTime, true, RES.getTinyMinuteHandMatrix(), RES.getTinyHourHandMatrix(), centerX, centerY);
        canvas.drawBitmap(RES.getTinyHandHourBitmap(), RES.getTinyHourHandMatrix(), RES.mAntiAliasPaint);
        canvas.drawBitmap(RES.getTinyHandMinuteBitmap(), RES.getTinyMinuteHandMatrix(), RES.mAntiAliasPaint);
    }

    private void DrawDigital(Canvas canvas, float centerX, float centerY) {

        if (datePaint == null) {

            Typeface digiTypeface = Typeface.createFromAsset(mContext.getAssets(), "digital-7.ttf");

            datePaint = new Paint();
            datePaint.setColor(RES.getTextColor());
            datePaint.setTextSize(45 * Theme.scaleFactor);
            datePaint.setAntiAlias(true);
            datePaint.setTypeface(digiTypeface);
        }
        setTimeZoneOffset();
        String timeString = String.format("%02d", mTime.hour) + ":" + String.format("%02d", mTime.minute);

        Rect bounds = new Rect();
        datePaint.getTextBounds(timeString, 0, timeString.length(), bounds);
        canvas.drawText(timeString, centerX - bounds.width() / 2, centerY + bounds.height() / 2, datePaint);
    }

    private void setTimeZoneOffset() {
        mTime.setToNow();
        long now = mTime.toMillis(false);
        long offset = this.timeZone.getRawOffset();
        long localOffset = TimeZone.getDefault().getRawOffset();

        long timeOff = (now - localOffset) + offset;
        mTime.set(timeOff);
    }

    public boolean mustRedraw() {
        setTimeZoneOffset();
        if (lastDrawTime.minute != mTime.minute || lastDrawTime.hour != mTime.hour) return true;
        return super.mustRedraw();
    }

    @Override
    public boolean drawFull(Canvas canvas, Matrix matrix) {
        return false; //TODO draw into Buffer
    }

    @Override
    public void onTapCommand(int tapType, int x, int y, long eventTime) {

    }

    @Override
    public WatchFaceView getViewType() {
        return WatchFaceView.SecondTime;
    }
}

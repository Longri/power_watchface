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
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.*;
import android.os.BatteryManager;
import android.text.format.DateFormat;

import java.util.Date;

/**
 * Created by Longri on 18.11.2015.
 */
public class TinyDateDrawable extends TinyDrawable {

    private static final int POWER_WATCH_ANGLE_BEGIN = 90;

    private int width;
    private int height;
    final private PowerDrawable mPowerWatchDrawable;
    final private PowerDrawable mPowerPhoneDrawable;

    private int phonePowerPercent = 0;

    private int mLastDay = -1;

    public TinyDateDrawable(Context context) {
        super(context);
        width = RES.getTinyBackground().getWidth();
        height = RES.getTinyBackground().getHeight();
        mPowerWatchDrawable = new PowerDrawable(255, 0, 0, POWER_WATCH_ANGLE_BEGIN, PowerDrawable.Direction.RIGHT);
        mPowerPhoneDrawable = new PowerDrawable(255, 0, 0, POWER_WATCH_ANGLE_BEGIN, PowerDrawable.Direction.LEFT);
    }

    public boolean mustRedraw() {
        if (mLastDay != RES.mTime.monthDay)
            return true;
        return super.mustRedraw();
    }

    public void setPhonePower(int value) {
        phonePowerPercent = value;
        this.setMustRedraw();
    }

    Bitmap bufferBitmap;

    @Override
    public boolean hasFullDraw() {
        return false;
    }

    @Override
    public boolean draw(Canvas canvas, Matrix matrix) {

        boolean fromBuffer = true;

        // check if day changed
        if (mLastDay != RES.mTime.monthDay || bufferBitmap == null || this.mustRedraw()) {
            mLastDay = RES.mTime.monthDay;

            //create BufferBitmap?
            if (bufferBitmap == null) {
                bufferBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            }

            final Canvas bufferCanvas = new Canvas(bufferBitmap);

            float scale = Theme.scaleFactor;

            final Paint datePaint = new Paint();
            datePaint.setColor(RES.getTextColor());
            datePaint.setTextSize(42 * scale);
            datePaint.setAntiAlias(true);

            final Paint dateNamePaint = new Paint();
            dateNamePaint.setColor(Color.WHITE);
            dateNamePaint.setTextSize(28 * scale);
            dateNamePaint.setAntiAlias(true);

            final String dateString = Integer.toString(RES.mTime.monthDay);
            final String dateNameString = (String) DateFormat.format("MMM", new Date());

            final float xPositionDate = width / 2 - datePaint.measureText(dateString) / 2;
            final float xPositionDateName = width / 2 - dateNamePaint.measureText(dateNameString) / 2;

            drawTinyBackground(bufferCanvas, EMPTY_MATRIX);
            bufferCanvas.drawText(dateString, xPositionDate, 55 * scale, datePaint);
            bufferCanvas.drawText(dateNameString, xPositionDateName, 85 * scale, dateNamePaint);

            // draw week day
            final Paint dayPaint = new Paint();
            dayPaint.setColor(RES.getTextColor());
            dayPaint.setTextSize(10 * scale);
            dayPaint.setAntiAlias(true);

            final Paint actDayPaint = new Paint();
            actDayPaint.setARGB(255, 255, 0, 0);
            actDayPaint.setTextSize(12 * scale);
            actDayPaint.setAntiAlias(true);

            String[] days = new String[]{"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};

            // act day index, shift to 'Mon' is first
            int actDayIndex = RES.mTime.weekDay - 1;
            if (actDayIndex < 0)
                actDayIndex = 6;

            // measure additional width!
            // "MON" equals margin
            float length = 0;
            float margin = -1;
            float[] deg = new float[7];

            int idx = 0;
            for (String day : days) {
                if (margin == -1) {
                    margin = dayPaint.measureText(day) / 4;
                }

                float dayLength;
                if (idx == actDayIndex) {
                    dayLength = actDayPaint.measureText(day);
                } else {
                    dayLength = dayPaint.measureText(day);
                }

                deg[idx] = (float) ((dayLength * 180) / (Math.PI * ((width - 28) / 2)));

                length += dayLength;
                if (idx++ < 6) {
                    length += margin;
                }
            }

            float marginDeg = (float) ((margin * 180) / (Math.PI * ((width - 30) / 2)));
            float lengthDeg = (float) ((length * 180) / (Math.PI * ((width - 30) / 2)));

            Path path = new Path();
            path.addArc(new RectF(15, 15, width - 15, width - 15), 0, 90);
            Matrix rotate = new Matrix();

            rotate.postRotate(-(lengthDeg / 2) - 90, width / 2, width / 2);

            idx = 0;
            for (String day : days) {

                bufferCanvas.setMatrix(rotate);
                bufferCanvas.drawTextOnPath(day, path, 0, 0, (idx == actDayIndex) ? actDayPaint : dayPaint);

                rotate.postRotate(deg[idx++] + marginDeg, width / 2, width / 2);
                //		if (idx == 3)
                //		    break;
            }

            //draw Power
            bufferCanvas.setMatrix(null);
            mPowerWatchDrawable.setPowerPercent(getBatteryPercent());
            mPowerWatchDrawable.draw(bufferCanvas, width + 2, height + 2);

            mPowerPhoneDrawable.setPowerPercent(phonePowerPercent);
            mPowerPhoneDrawable.draw(bufferCanvas, width + 2, height + 2);

            fromBuffer = false;
        }

        canvas.drawBitmap(bufferBitmap, matrix, RES.mAntiAliasPaint_noGreyScale);
        return fromBuffer;
    }

    private int getBatteryPercent() {
        IntentFilter iFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = mContext.registerReceiver(null, iFilter);
        return batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
    }

    @Override
    public boolean drawFull(Canvas canvas, Matrix matrix) {
        return false;
    }

    @Override
    public void onTapCommand(int tapType, int x, int y, long eventTime) {

    }

    @Override
    public WatchFaceView getViewType() {
        return WatchFaceView.Date;
    }
}

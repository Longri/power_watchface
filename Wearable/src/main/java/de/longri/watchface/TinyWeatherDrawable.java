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
import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import de.longri.watchface.loging.Log;
import de.longri.watchface.loging.LogType;
import de.longri.weather.Info;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Longri on 17.11.15.
 */
public class TinyWeatherDrawable extends TinyDrawable {

    private static final String WEATHER_NA = "weather_na_25_44";
    private static final String WEATHER_00 = "weather_00_3_4_17_35";
    private static final String WEATHER_WEATHER_1_2_12_40 = "weather_1_2_12_40";
    private static final String WEATHER_05 = "weather_5";
    private static final String WEATHER_06 = "weather_6";
    private static final String WEATHER_07 = "weather_7";
    private static final String WEATHER_08_10 = "weather_8_10";
    private static final String WEATHER_09_11 = "weather_9_11";
    private static final String WEATHER_19_22 = "weather_19_22";
    private static final String WEATHER_26 = "weather_26";
    private static final String WEATHER_27 = "weather_27";
    private static final String WEATHER_28 = "weather_28";
    private static final String WEATHER_29 = "weather_29";
    private static final String WEATHER_30 = "weather_30";
    private static final String WEATHER_31 = "weather_31";
    private static final String WEATHER_32 = "weather_32";
    private static final String WEATHER_33 = "weather_33";
    private static final String WEATHER_34 = "weather_34";
    private static final String WEATHER_36 = "weather_36";
    private static final String WEATHER_37 = "weather_37";
    private static final String WEATHER_41 = "weather_41";
    private static final String WEATHER_42 = "weather_42";
    private static final String WEATHER_43 = "weather_43";
    private static final String WEATHER_45 = "weather_45";
    private static final String WEATHER_46 = "weather_46";
    private static final String WEATHER_47 = "weather_47";
    private final Paint mTxtPaint;
    private final Paint mDatePaint;

    private Info mWeatherInfo;
    private Info mWeatherForecast1;
    private Info mWeatherForecast2;
    private Bitmap mWeatherIconBitmap[] = new Bitmap[3];
    private String mLastWeatherIconName[] = new String[]{"", "", ""};
    private int mLastWeatherIconId[] = new int[]{-1, -1, -1};
    private String mDegrease;

    public TinyWeatherDrawable(Context context) {
        super(context);
        mTxtPaint = new Paint();
        mTxtPaint.setColor(Color.WHITE);
        mTxtPaint.setTextSize(28);
        mTxtPaint.setAntiAlias(true);

        mDatePaint = new Paint();
        mDatePaint.setColor(Color.WHITE);
        mDatePaint.setTextSize(14);
        mDatePaint.setAntiAlias(true);


    }


    Bitmap bufferBitmap;

    public boolean mustRedraw() {
        if (bufferBitmap == null || mWeatherIconBitmap[0] == null) return true;
        return super.mustRedraw();
    }

    @Override
    public boolean hasFullDraw() {
        return true;
    }

    @Override
    public boolean draw(Canvas canvas, Matrix matrix) {

        boolean fromBuffer = true;

        if (this.mustRedraw() || bufferBitmap == null) {

            //create BufferBitmap?
            if (bufferBitmap == null) {
                bufferBitmap = Bitmap.createBitmap(
                        RES.getTinyBackground().getWidth(), RES.getTinyBackground().getHeight(),
                        Bitmap.Config.ARGB_8888);
            }

            Canvas bufferCanvas = new Canvas(bufferBitmap);
            boolean error = mWeatherInfo == null;

            if (error) mDegrease = "--";
            else
                mDegrease = mWeatherInfo.getTemp();


            drawTinyBackground(bufferCanvas, EMPTY_MATRIX);

            if (!error) {
                try {
                    setWeatherIcon(0, mWeatherInfo.getActualIconID());
                } catch (Exception e) {
                    e.printStackTrace();
                    mWeatherIconBitmap[0] = null;
                    mDegrease = "E " + Integer.toString(mWeatherInfo.getActualIconID());
                }
            }
            if (mWeatherIconBitmap[0] != null) {
                bufferCanvas.drawBitmap(mWeatherIconBitmap[0], EMPTY_MATRIX, RES.mAntiAliasPaint);
            } else {
                if (Log.isLoggable(LogType.WEATHER)) {
                    Log.d(Consts.TAG_WEAR, "Weather icon are null, ID=" + Integer.toString(mWeatherInfo != null ? mWeatherInfo.getActualIconID() : -1));
                }
            }

            float dateCircleWidth = res.getTinyBackground().getWidth();
            float PositionDegrese = dateCircleWidth / 2 - mTxtPaint.measureText(mDegrease) / 2;
            bufferCanvas.drawText(mDegrease, PositionDegrese, 90, mTxtPaint);

            fromBuffer = false;
        }


        canvas.drawBitmap(bufferBitmap, matrix, RES.mAntiAliasPaint_noGreyScale);
        return fromBuffer;
    }

    @Override
    public boolean drawFull(Canvas canvas, Matrix matrix) {

        if (nowMatrix == null) {
            nowMatrix = new Matrix();
            nowMatrix.setTranslate(2, 60);
            nowMatrix.postScale(1.5f, 1.5f);

            forecast1Matrix = new Matrix();
            forecast1Matrix.setTranslate(140, 50);


            forecast2Matrix = new Matrix();
            forecast2Matrix.setTranslate(140, 180);
        }

        if (mWeatherInfo != null) {
            setWeatherIcon(0, mWeatherInfo.getActualIconID());
            drawWeather(0, canvas, mWeatherInfo, nowMatrix, false);
        }

        if (mWeatherForecast1 != null) {
            setWeatherIcon(1, mWeatherForecast1.getActualIconID());
            drawWeather(1, canvas, mWeatherForecast1, forecast1Matrix, true);
        }

        if (mWeatherForecast2 != null) {
            setWeatherIcon(2, mWeatherForecast2.getActualIconID());
            drawWeather(2, canvas, mWeatherForecast2, forecast2Matrix, true);
        }
        return false; //TODO draw into Buffer
    }

    @Override
    public void onTapCommand(int tapType, int x, int y, long eventTime) {
        if (tapType != 2L) return;
        if (listener != null) {
            listener.resetFullDraw();
            listener.setAmbientMode(true);
        }

    }

    Matrix nowMatrix;
    Matrix forecast1Matrix;
    Matrix forecast2Matrix;


    private void drawWeather(int index, Canvas canvas, Info info, Matrix matrix, boolean withDate) {

        if (mWeatherIconBitmap[index] != null)
            canvas.drawBitmap(mWeatherIconBitmap[index], matrix, RES.mAntiAliasPaint);

        canvas.setMatrix(matrix);


        if (withDate) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            Date date = info.getDate();
            String dateString = dateFormatter.format(date);
            canvas.drawText(dateString, 90, 40, mDatePaint);
        }

        String degreeString = info.getTemp();
        float dateCircleWidth = res.getTinyBackground().getWidth();
        float degree = dateCircleWidth / 2 - mTxtPaint.measureText(degreeString) / 2;
        canvas.drawText(degreeString, degree, 90, mTxtPaint);
        canvas.setMatrix(null);
    }


    @Override
    public View getViewType() {
        return View.Weather;
    }

    private boolean[] inLoad = new boolean[]{false, false, false};

    private void setWeatherIcon(final int index, final int id) {

        if (inLoad[index]) return;

        if (mLastWeatherIconId[index] != id) {

            if (index == 0) {
                setMustRedraw();
            }

            if (!mLastWeatherIconName.equals(getNameOfWeatherIcon(id))) {

                inLoad[index] = true;
                //do that in a background task
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        mWeatherIconBitmap[index] = null;
                        mLastWeatherIconName[index] = getNameOfWeatherIcon(id);
                        Resources resources = mContext.getResources();
                        int resID = resources.getIdentifier(
                                mLastWeatherIconName[index],
                                "drawable", mContext.getPackageName());
                        Drawable weatherDrawable = resources.getDrawable(resID);
                        mWeatherIconBitmap[index] = ((BitmapDrawable) weatherDrawable).getBitmap();
                        inLoad[index] = false;
                        Invalidate();
                    }
                });
                thread.start();
            }
            mLastWeatherIconId[index] = id;
        }
    }


    private String getNameOfWeatherIcon(int id) {

        switch (id) {
            case 0:
            case 3:
            case 4:
            case 17:
            case 35:
                return WEATHER_00;
            case 25:
            case 44:
                return WEATHER_NA;
            case 1:
            case 2:
            case 12:
            case 40:
                return WEATHER_WEATHER_1_2_12_40;
            case 5:
                return WEATHER_05;
            case 6:
                return WEATHER_06;
            case 7:
                return WEATHER_07;
            case 8:
            case 10:
                return WEATHER_08_10;
            case 9:
            case 11:
                return WEATHER_09_11;
            case 19:
            case 22:
                return WEATHER_19_22;
            case 26:
                return WEATHER_26;
            case 27:
                return WEATHER_27;
            case 28:
                return WEATHER_28;
            case 29:
                return WEATHER_29;
            case 30:
                return WEATHER_30;
            case 31:
                return WEATHER_31;
            case 32:
                return WEATHER_32;
            case 33:
                return WEATHER_33;
            case 34:
                return WEATHER_34;
            case 36:
                return WEATHER_36;
            case 37:
                return WEATHER_37;
            case 41:
                return WEATHER_41;
            case 42:
                return WEATHER_42;
            case 43:
                return WEATHER_43;
            case 45:
                return WEATHER_45;
            case 46:
                return WEATHER_46;
            case 47:
                return WEATHER_47;
        }
        return WEATHER_NA;
    }

    public void setActWeather(Info info) {
        this.mWeatherInfo = info;
        setMustRedraw();
        if (Log.isLoggable(LogType.WEATHER)) {
            Log.d(Consts.TAG_WEAR, "WeatherDrawable set ActWeather:" + this.mWeatherInfo.toString());
        }
    }

    public void setWeatherForecast1(Info info) {
        this.mWeatherForecast1 = info;
        setMustRedraw();
        if (Log.isLoggable(LogType.WEATHER)) {
            Log.d(Consts.TAG_WEAR, "WeatherDrawable set Forecast1:" + this.mWeatherForecast1.toString());
        }
    }

    public void setWeatherForecast2(Info info) {
        this.mWeatherForecast2 = info;
        setMustRedraw();
        if (Log.isLoggable(LogType.WEATHER)) {
            Log.d(Consts.TAG_WEAR, "WeatherDrawable set Forecast2:" + this.mWeatherForecast2.toString());
        }
    }

    public WeatherInfoType getWeatherRequestType() {

        boolean loggable = Log.isLoggable(LogType.WEATHER);
        if (loggable) Log.d(Consts.TAG_WEAR, "CHECK WEATHER REQUEST TYPE");


        if (this.mWeatherInfo == null) {
            if (loggable) Log.d(Consts.TAG_WEAR, "return FORECAST (WeatherInfo are NULL)");
            return WeatherInfoType.FORECAST;
        }

        if (mWeatherForecast1 != null && mWeatherForecast2 != null) {

            int minutes1 = RES.mTime.hour * 60 + RES.mTime.minute + 90;
            int minuets2 = mWeatherForecast1.getDate().getHours() * 60 + mWeatherForecast1.getDate().getMinutes();

            if (minutes1 > minuets2) {
                if (loggable)
                    Log.d(Consts.TAG_WEAR, "return NOW (minutes1 > minuets2)" + "(" + minutes1 + ">" + minuets2 + ")");
                return WeatherInfoType.NOW;
            } else {
                if (loggable)
                    Log.d(Consts.TAG_WEAR, "return FORECAST (minutes1 <= minuets2)" + "(" + minutes1 + "<=" + minuets2 + ")");
                return WeatherInfoType.FORECAST;
            }
        } else {
            if (loggable) Log.d(Consts.TAG_WEAR, "return FORECAST (mWeatherForecast1 && mWeatherForecast2 are NULL)");
            return WeatherInfoType.FORECAST;
        }
    }

    public boolean weatherIsNull() {

        return mWeatherInfo == null || mWeatherForecast1 == null || mWeatherForecast2 == null;

    }

    public void setOutputToCelsius() {
        if (mWeatherInfo != null) mWeatherInfo.setOutputToCelsius();
        if (mWeatherForecast1 != null) mWeatherForecast1.setOutputToCelsius();
        if (mWeatherForecast2 != null) mWeatherForecast2.setOutputToCelsius();
    }

    public void setOutputToFahrenheit() {
        if (mWeatherInfo != null) mWeatherInfo.setOutputToFahrenheit();
        if (mWeatherForecast1 != null) mWeatherForecast1.setOutputToFahrenheit();
        if (mWeatherForecast2 != null) mWeatherForecast2.setOutputToFahrenheit();
    }
}

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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.*;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.wearable.watchface.CanvasWatchFaceService;
import android.support.wearable.watchface.WatchFaceService;
import android.support.wearable.watchface.WatchFaceStyle;
import android.text.format.DateUtils;
import android.view.SurfaceHolder;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.*;
import de.longri.serializable.BitStore;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.SerializableArrayList;
import de.longri.watchface.loging.Log;
import de.longri.watchface.loging.LogItem;
import de.longri.watchface.loging.LogType;
import de.longri.weather.Info;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * => PS H:\> cd C:\@Work\ide\android-sdk-windows\platform-tools
 * <p/>
 * for deinstall on watch:
 * => AndresMacBook:platform-tools Longri$ ./adb uninstall de.longri.watchface
 * <p/>
 * for list:
 * <p/>
 * =>  C:\@Work\ide\android-sdk-windows\platform-tools>  .\adb shell 'pm list packages -f'
 * <p/>
 * Weather watch face tutorial => https://github.com/swarmnyc/Android-Watch-Face-Template
 */
public class PowerWatchFaceService extends CanvasWatchFaceService {
    private static final boolean DEBUG_WEATHER = false;
    static String debugString;
    static String mVersionString = "-1";
    static String lastWeatherUpdateTime = "?";
    static String lastWeatherRequestTime = "?";

    static void initialDebugString() {
        debugString = mVersionString + "\n    " + lastWeatherUpdateTime + "\n    " + lastWeatherRequestTime;
    }

    private enum FullDraw {
        Top(0), Right(1), Bottom(2), Left(3), None(-1);

        private int numVal;

        FullDraw(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }

    private enum BackgroundType {
        B_24, B_12, NONE, B_Ambient
    }


    /**
     * Update rate in milliseconds for interactive mode. We update once a second to advance the
     * second hand.
     */
    private static final long INTERACTIVE_UPDATE_RATE_MS = TimeUnit.SECONDS.toMillis(1);

    private GoogleApiClient mClient;
    private static final int MSG_UPDATE_TIME = 0;


    @Override
    public Engine onCreateEngine() {
        return new Engine();
    }

    private class Engine extends CanvasWatchFaceService.Engine
            implements
            GoogleApiClient.ConnectionCallbacks,
            GoogleApiClient.OnConnectionFailedListener,
            DataApi.DataListener, NodeApi.NodeListener, TinyDrawable.InvalidateListener {


        private Paint mSecondPaint;
        private FullDraw fullDraw = FullDraw.None;

        private final Interval weatherUpdateInterval = new Interval(5 * 60 * 1000);//Weather update interval with try timer of 5min
        private final Interval weatherCheckInterval = new Interval(5 * 60 * 1000);//Weather check interval with try timer of 5min
        private final Interval phonePowerUpdateInterval = new Interval(1000);


        private boolean mMute;

        private boolean mLowBitAmbient;

        private final Matrix mMinuteHandMatrix = new Matrix();
        private final Matrix mHourHandMatrix = new Matrix();

        private boolean mSizeIsInitial = false;
        private int mWidth, mHeight;
        private float mCenterX, mCenterY;
        private float mSecLength;

        private TinyWeatherDrawable weatherDrawable;
        private TinyDateDrawable dateDrawable;
        private TinyClockDrawable tinyClockDrawable;
        private TinyLogoDrawable logoDrawable;

        private boolean mRegisteredTimeZoneReceiver = false;

        private Config mConfig;
        private boolean lastTimeUnit;

        private RectF touchTop;
        private RectF touchRight;
        private RectF touchBottom;
        private RectF touchLeft;
        private boolean invalid = true;

        private BackgroundType lastBackgroundType = BackgroundType.NONE;

        /**
         * Handler to update the time once a second in interactive mode.
         */
        final Handler mUpdateTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case MSG_UPDATE_TIME:
                        invalidate();
                        if (shouldTimerBeRunning()) {
                            long timeMs = System.currentTimeMillis();
                            long delayMs = INTERACTIVE_UPDATE_RATE_MS
                                    - (timeMs % INTERACTIVE_UPDATE_RATE_MS);
                            mUpdateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs);
                        }
                        break;
                }
            }
        };


        final BroadcastReceiver mTimeZoneReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                RES.mTime.clear(intent.getStringExtra("time-zone"));
                RES.mTime.setToNow();
            }
        };

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(PowerWatchFaceService.this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Wearable.API)
                .build();


        @Override
        public void onCreate(SurfaceHolder holder) {
            if (Log.isLoggable()) {
                Log.d(Consts.TAG_WEAR, "onCreate");
            }
            super.onCreate(holder);


            setWatchFaceStyle(new WatchFaceStyle.Builder(PowerWatchFaceService.this)
                    .setCardPeekMode(WatchFaceStyle.PEEK_MODE_SHORT)
                    .setBackgroundVisibility(WatchFaceStyle.BACKGROUND_VISIBILITY_INTERRUPTIVE)
                    .setShowSystemUiTime(false)
                    .setAcceptsTapEvents(true)
                    .build());

            PackageInfo pInfo;
            try {
                pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                mVersionString = "Rev: " + pInfo.versionName;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }

            mSecondPaint = new Paint();
            mSecondPaint.setARGB(255, 255, 0, 0);
            mSecondPaint.setStrokeWidth(2.f);
            mSecondPaint.setAntiAlias(true);
            mSecondPaint.setStrokeCap(Paint.Cap.ROUND);

            mClient = new GoogleApiClient.Builder(PowerWatchFaceService.this)
                    .addApi(Wearable.API).build();
            mClient.connect();

            weatherCheckInterval.setInterval(10 * 60 * 1000);

            getConfig();


            //initial Log
            //check Log
            if (!Log.isInitial()) new Log(PowerWatchFaceService.this, true);
        }


        @Override
        public void onTapCommand(int tapType, int x, int y, long eventTime) {
            if (Log.isLoggable(LogType.TouchInput)) {
                Log.d(Consts.TAG_WEAR, "onTapCommand: tapType = " + tapType + "   x:" + x + " y:" + y);
            }
            if (tapType != 2L) return;

            if (isInAmbientMode()) {
                setAmbientMode(false);
                return;
            }


            // calc touch zones
            if (touchTop == null) {
                int radius = RES.getTinyBackground().getWidth() / 2;
                RectF center = new RectF(mCenterX, mCenterY, radius, radius);

                touchTop = getTouchZone(center, RES.getMatrixForPos(0));
                touchRight = getTouchZone(center, RES.getMatrixForPos(1));
                touchBottom = getTouchZone(center, RES.getMatrixForPos(2));
                touchLeft = getTouchZone(center, RES.getMatrixForPos(3));
            }

            //check where taped
            if (fullDraw != FullDraw.None) {
                //send to tiny Drawable
                if (fullDraw.getNumVal() == mConfig.getPositionOf(View.Logo)) {
                    logoDrawable.onTapCommand(tapType, x, y, eventTime);
                } else if (fullDraw.getNumVal() == mConfig.getPositionOf(View.Date)) {
                    dateDrawable.onTapCommand(tapType, x, y, eventTime);
                } else if (fullDraw.getNumVal() == mConfig.getPositionOf(View.SecondTime)) {
                    tinyClockDrawable.onTapCommand(tapType, x, y, eventTime);
                } else if (fullDraw.getNumVal() == mConfig.getPositionOf(View.Weather)) {
                    weatherDrawable.onTapCommand(tapType, x, y, eventTime);
                }

                return;
            }


            if (inside(touchTop, x, y)) {
                if (Log.isLoggable(LogType.TouchInput)) {
                    Log.d(Consts.TAG_WEAR, "onTapCommand: Top");
                }
                if (hasFullDraw(FullDraw.Top)) fullDraw = FullDraw.Top;
            }
            if (inside(touchRight, x, y)) {
                if (Log.isLoggable(LogType.TouchInput)) {
                    Log.d(Consts.TAG_WEAR, "onTapCommand: Right");
                }
                if (hasFullDraw(FullDraw.Right)) fullDraw = FullDraw.Right;
            }
            if (inside(touchBottom, x, y)) {
                if (Log.isLoggable(LogType.TouchInput)) {
                    Log.d(Consts.TAG_WEAR, "onTapCommand: Bottom");
                }
                if (hasFullDraw(FullDraw.Bottom)) fullDraw = FullDraw.Bottom;
            }
            if (inside(touchLeft, x, y)) {
                if (Log.isLoggable(LogType.TouchInput)) {
                    Log.d(Consts.TAG_WEAR, "onTapCommand: Left");
                }
                if (hasFullDraw(FullDraw.Left)) fullDraw = FullDraw.Left;
            }
        }

        public boolean hasFullDraw(FullDraw fd) {
            boolean handleTab = false;
            for (View v : View.values()) {
                if (fd.getNumVal() == mConfig.getPositionOf(v)) {
                    if (v == View.Logo) handleTab = logoDrawable.hasFullDraw();
                    if (v == View.Date) handleTab = dateDrawable.hasFullDraw();
                    if (v == View.SecondTime) handleTab = tinyClockDrawable.hasFullDraw();
                    if (v == View.Weather) handleTab = weatherDrawable.hasFullDraw();
                    break;
                }
            }
            return handleTab;
        }


        public boolean inside(RectF rec, int x, int y) {
            return Math.hypot(rec.left - x, rec.top - y) <= (int) ((rec.right - rec.left) / 2);
        }

        public RectF getTouchZone(RectF center, Matrix matrix) {
            RectF ret = new RectF(center);
            matrix.mapRect(ret);
            return ret;
        }


        @Override
        public void onDestroy() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            mClient.disconnect();
            super.onDestroy();
        }

        @Override
        public void onPropertiesChanged(Bundle properties) {
            super.onPropertiesChanged(properties);
            mLowBitAmbient = properties.getBoolean(PROPERTY_LOW_BIT_AMBIENT, false);
            if (Log.isLoggable(LogType.DRAW)) {
                Log.d(Consts.TAG_WEAR, "onPropertiesChanged: low-bit ambient = " + mLowBitAmbient);
            }
        }

        @Override
        public void onTimeTick() {
            super.onTimeTick();
            invalidate();
        }

        @Override
        public void onAmbientModeChanged(boolean inAmbientMode) {
            super.onAmbientModeChanged(inAmbientMode);
            backgroundChanged = true;

            if (Log.isLoggable(LogType.DRAW)) {
                Log.d(Consts.TAG_WEAR, "onAmbientModeChanged: " + inAmbientMode);
            }
            if (mLowBitAmbient) {
                boolean antiAlias = !inAmbientMode;
                mSecondPaint.setAntiAlias(antiAlias);
                RES.mAntiAliasPaint.setColorFilter(new ColorMatrixColorFilter(Utils.cm));
            }

            boolean antiAlias = !inAmbientMode;
            mSecondPaint.setAntiAlias(antiAlias);
            if (inAmbientMode) {
                RES.mAntiAliasPaint.setColorFilter(new ColorMatrixColorFilter(Utils.cm));
                TinyDrawable.setAmbientMode(true);

                //revert possible full draw
                fullDraw = FullDraw.None;

            } else {
                RES.mAntiAliasPaint.setColorFilter(null);
                TinyDrawable.setAmbientMode(false);
            }
            invalidate();
            updateTimer();
        }

        @Override
        public void onInterruptionFilterChanged(int interruptionFilter) {
            super.onInterruptionFilterChanged(interruptionFilter);
            boolean inMuteMode = (interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE);
            if (mMute != inMuteMode) {
                mMute = inMuteMode;
                mSecondPaint.setAlpha(inMuteMode ? 80 : 255);
            }
        }


        boolean configLoaded = false;

        Bitmap bufferBitmap;
        Canvas bufferCanvas;

        @Override
        public void onDraw(Canvas canvas, Rect bounds) {

            if (mConfig == null) {
                mConfig = new Config();//default config
            }

            if (!configLoaded) {
                if (Log.isLoggable()) {
                    Log.d(Consts.TAG_WEAR, "Config not complete read, require");
                    getConfig();
                }
            }

            RES.mTime.setToNow();

            if (!mSizeIsInitial) initialSize(bounds);


            if (bufferBitmap == null) {
                bufferBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
                bufferCanvas = new Canvas(bufferBitmap);
            }


            drawBuffer();
            canvas.drawBitmap(bufferBitmap, 0, 0, RES.mAntiAliasPaint_noGreyScale);

            if (weatherCheckInterval.isElapsed()) {

                if (Log.isLoggable(LogType.WEATHER)) {
                    Log.d(Consts.TAG_WEAR, "weatherCheckInterval elapsed");
                }

                if (weatherUpdateInterval.isElapsed()) {

                    boolean force = weatherDrawable.weatherIsNull();
                    WeatherInfoType requestType = getWeatherRequestType();

                    requireWeatherInfo(requestType, force);
                    if (Log.isLoggable(LogType.WEATHER)) {

                        RES.mTime.setToNow();
                        int h = RES.mTime.hour;
                        int m = RES.mTime.minute;

                        lastWeatherRequestTime = Integer.toString(h) + ":" + Integer.toString(m) + (force ? "force " : " ") + requestType.toString();
                        debugString = null;
                        Log.d(Consts.TAG_WEAR, "weatherUpdateInterval elapsed");
                    }
                }
                weatherCheckInterval.restartInterval();
            }


            if (fullDraw == FullDraw.None) {
                RES.setHandsMatrix(RES.mTime, false, mMinuteHandMatrix, mHourHandMatrix, mCenterX, mCenterY);

                Bitmap handHourBmp = isInAmbientMode() ? RES.getWatchHandHourBitmapAmbient() : RES.getWatchHandHourBitmap();
                Bitmap handMinuteBmp = isInAmbientMode() ? RES.getWatchHandMinuteBitmapAmbient() : RES.getWatchHandMinuteBitmap();

                canvas.drawBitmap(handHourBmp, mHourHandMatrix, RES.mAntiAliasPaint_noGreyScale);
                canvas.drawBitmap(handMinuteBmp, mMinuteHandMatrix, RES.mAntiAliasPaint_noGreyScale);
                if (!isInAmbientMode()) {
                    float secRot = RES.mTime.second / 30f * Utils.PI;
                    float secX = Utils.sin(secRot) * mSecLength;
                    float secY = -Utils.cos(secRot) * mSecLength;
                    canvas.drawLine(mCenterX, mCenterY, mCenterX + secX, mCenterY + secY, mSecondPaint);
                }
            }

            /*
            Draw version number
             */
            if (!isInAmbientMode() && Log.isLoggable()) {
                if (debugPaint == null) initialDebug();
                if (debugString == null) initialDebugString();
                Utils.drawString(canvas, debugPaint, debugString, 40, 250);
            }


            //draw black with transparency to overdraw brightness
            canvas.drawARGB(100, 0, 0, 0); // TODO: 03.01.16 set brightness over config 0=100% to 200=0%


            // reset flag
            RES.mMinuteChanged = false;
        }

        Paint debugPaint;


        void initialDebug() {
            debugPaint = new Paint();
            debugPaint.setColor(Color.WHITE);
            debugPaint.setTextSize(10);
            debugPaint.setAntiAlias(true);
        }


        boolean backgroundChanged = true;
        FullDraw lastBufferFullDraw;


        private void drawBuffer() {

            boolean anyDrawChanges = false;

            boolean mustDrawLogo = logoDrawable == null ? true : logoDrawable.mustRedraw();
            boolean mustDrawWeather = weatherDrawable == null ? true : weatherDrawable.mustRedraw();
            boolean mustDrawDate = dateDrawable == null ? true : dateDrawable.mustRedraw();
            boolean mustDrawTinyClock = tinyClockDrawable == null ? true : tinyClockDrawable.mustRedraw();


            if (invalid) {
                // background is changed, must redraw all tiny'S
                mustDrawLogo = true;
                mustDrawWeather = true;
                mustDrawDate = true;
                mustDrawTinyClock = true;

                anyDrawChanges = true;
                invalid = false;
            }


            if (lastBufferFullDraw != fullDraw) {
                lastBufferFullDraw = fullDraw;

                if (fullDraw == FullDraw.None) {
                    //must draw all
                    backgroundChanged = true;
                    mustDrawLogo = true;
                    mustDrawWeather = true;
                    mustDrawDate = true;
                    mustDrawTinyClock = true;
                } else {
                    backgroundChanged = true;
                    mustDrawLogo = fullDraw.getNumVal() == mConfig.getPositionOf(View.Logo);
                    mustDrawWeather = fullDraw.getNumVal() == mConfig.getPositionOf(View.Weather);
                    mustDrawDate = fullDraw.getNumVal() == mConfig.getPositionOf(View.Date);
                    mustDrawTinyClock = fullDraw.getNumVal() == mConfig.getPositionOf(View.SecondTime);
                }
                if (Log.isLoggable(LogType.DRAW)) {
                    Log.d(Consts.TAG_WEAR, "Full draw is changed");
                }
            }

            BackgroundType newType;

            if (isInAmbientMode()) {
                newType = BackgroundType.B_Ambient;
            } else {

                boolean timeUnit = mConfig != null ? mConfig.getTimeUnit() : false;

                if (fullDraw == FullDraw.None) {
                    if (RES.mTime.hour > 12) {
                        //  24h Background or 12h Background
                        if (timeUnit) {
                            newType = BackgroundType.B_24;
                        } else {
                            newType = BackgroundType.B_12;
                        }
                    } else {
                        // 12h Background
                        newType = BackgroundType.B_12;
                    }
                } else {
                    newType = BackgroundType.B_Ambient;
                }
            }


            if (backgroundChanged || lastBackgroundType != newType) {

                lastBackgroundType = newType;

                Bitmap mBackgroundBitmap = null;

                switch (newType) {
                    case B_12:
                        mBackgroundBitmap = RES.get12BackGround();
                        break;
                    case B_24:
                        mBackgroundBitmap = RES.get24BackGround();
                        break;
                    case B_Ambient:
                        mBackgroundBitmap = RES.getAmbientBackGround();
                        break;
                }


                if (mBackgroundBitmap != null && !mBackgroundBitmap.isRecycled()) {

                    if (Log.isLoggable(LogType.DRAW)) {
                        Log.d(Consts.TAG_WEAR, "Draw new Background");
                    }
                    backgroundChanged = false;
                    bufferCanvas.drawBitmap(mBackgroundBitmap, 0, 0, RES.mAntiAliasPaint_noGreyScale);
                }

                // background is changed, must redraw all tiny'S
                mustDrawLogo = true;
                mustDrawWeather = true;
                mustDrawDate = true;
                mustDrawTinyClock = true;

                anyDrawChanges = true;
            }

            if (mustDrawLogo) {
                if (logoDrawable == null) {
                    logoDrawable = new TinyLogoDrawable(PowerWatchFaceService.this);
                }

                int pos = mConfig.getPositionOf(View.Logo);
                //check to have this a drawing index
                if (pos >= 0 && pos <= 3) {
                    boolean fromBuffer = false;

                    if (fullDraw == FullDraw.None) {
                        fromBuffer = logoDrawable.draw(bufferCanvas, RES.getMatrixForPos(pos));
                    } else if (fullDraw.getNumVal() == pos) {
                        fromBuffer = logoDrawable.drawFull(bufferCanvas, RES.getMatrixForPos(pos));
                    }
                    anyDrawChanges = true;
                    if (Log.isLoggable(LogType.DRAW)) {
                        Log.d(Consts.TAG_WEAR, "Draw new Logo" + (fromBuffer ? " from Buffer" : ""));
                    }
                }
                logoDrawable.resetMustRedraw();
            }


            if (mustDrawWeather) {
                if (weatherDrawable == null) {
                    weatherDrawable = new TinyWeatherDrawable(PowerWatchFaceService.this);
                    weatherDrawable.setInvalidateListener(this);
                }
                int pos = mConfig.getPositionOf(View.Weather);
                //check to have this a drawing index
                if (pos >= 0 && pos <= 3) {
                    boolean fromBuffer = false;
                    if (fullDraw == FullDraw.None) {
                        fromBuffer = weatherDrawable.draw(bufferCanvas, RES.getMatrixForPos(pos));
                    } else if (fullDraw.getNumVal() == pos) {
                        bufferCanvas.drawRGB(0, 0, 0);
                        fromBuffer = weatherDrawable.drawFull(bufferCanvas, RES.getMatrixForPos(pos));
                    }

                    anyDrawChanges = true;
                    if (Log.isLoggable(LogType.DRAW)) {
                        Log.d(Consts.TAG_WEAR, "Draw new Weather" + (fromBuffer ? " from Buffer" : ""));
                    }
                }
                weatherDrawable.resetMustRedraw();
            }


            if (mustDrawDate) {
                if (dateDrawable == null) {
                    dateDrawable = new TinyDateDrawable(PowerWatchFaceService.this);
                }
                int pos = mConfig.getPositionOf(View.Date);
                //check to have this a drawing index
                if (pos >= 0 && pos <= 3) {
                    boolean fromBuffer = false;
                    if (fullDraw == FullDraw.None) {
                        fromBuffer = dateDrawable.draw(bufferCanvas, RES.getMatrixForPos(pos));
                    } else if (fullDraw.getNumVal() == pos) {
                        fromBuffer = dateDrawable.drawFull(bufferCanvas, RES.getMatrixForPos(pos));
                    }
                    anyDrawChanges = true;
                    if (Log.isLoggable(LogType.DRAW)) {
                        Log.d(Consts.TAG_WEAR, "Draw new Date" + (fromBuffer ? " from Buffer" : ""));
                    }
                }
                dateDrawable.resetMustRedraw();
            }

            if (mustDrawTinyClock) {
                if (tinyClockDrawable == null) {
                    tinyClockDrawable = new TinyClockDrawable(PowerWatchFaceService.this);
                    tinyClockDrawable.setInvalidateListener(this);
                }

                int pos = mConfig.getPositionOf(View.SecondTime);
                //check to have this a drawing index
                if (pos >= 0 && pos <= 3) {
                    boolean fromBuffer = false;
                    if (fullDraw == FullDraw.None) {
                        fromBuffer = tinyClockDrawable.draw(bufferCanvas, RES.getMatrixForPos(pos));
                    } else if (fullDraw.getNumVal() == pos) {
                        fromBuffer = tinyClockDrawable.drawFull(bufferCanvas, RES.getMatrixForPos(pos));
                    }
                    anyDrawChanges = true;
                    if (Log.isLoggable(LogType.DRAW)) {
                        Log.d(Consts.TAG_WEAR, "Draw new second clock" + (fromBuffer ? " from Buffer" : ""));
                    }
                }
                tinyClockDrawable.resetMustRedraw();
            }


            // finally, if any drawing changed, run GC
            if (anyDrawChanges) {
                if (Log.isLoggable(LogType.DRAW)) {
                    Log.d(Consts.TAG_WEAR, "Any drawing is changed, run GC");
                }
                System.gc();
            }
        }


        @Override
        public void invalidate() {
            super.invalidate();
            invalid = true;
        }

        private void initialSize(Rect bounds) {
//    load defaultResources

            DefaultTheme.resources = PowerWatchFaceService.this.getResources();
            DefaultTheme.bounds = bounds;
            RES.setTheme(new DefaultTheme());

            mWidth = bounds.width();
            mHeight = bounds.height();
            mCenterX = mWidth / 2f;
            mCenterY = mHeight / 2f;
            mSecLength = mCenterX - 20;
            mSizeIsInitial = true;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            if (Log.isLoggable(LogType.DRAW)) {
                Log.d(Consts.TAG_WEAR, "onVisibilityChanged: " + visible);
            }

            if (visible) {
                registerReceiver();

                // Update time zone in case it changed while we weren't visible.
                RES.mTime.clear(TimeZone.getDefault().getID());
                RES.mTime.setToNow();
            } else {
                unregisterReceiver();
            }

            updateTimer();
        }

        private void registerReceiver() {
            if (mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = true;
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIMEZONE_CHANGED);
            PowerWatchFaceService.this.registerReceiver(mTimeZoneReceiver, filter);
        }

        private void unregisterReceiver() {
            if (!mRegisteredTimeZoneReceiver) {
                return;
            }
            mRegisteredTimeZoneReceiver = false;
            PowerWatchFaceService.this.unregisterReceiver(mTimeZoneReceiver);
        }

        /**
         * Starts the {@link #mUpdateTimeHandler} timer if it should be running and isn't currently
         * or stops it if it shouldn't be running but currently is.
         */
        private void updateTimer() {
            mUpdateTimeHandler.removeMessages(MSG_UPDATE_TIME);
            if (shouldTimerBeRunning()) {
                mUpdateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME);
            }
        }

        /**
         * Returns whether the {@link #mUpdateTimeHandler} timer should be running. The timer should
         * only run when we're visible and in interactive mode.
         */
        private boolean shouldTimerBeRunning() {
            return isVisible() && !isInAmbientMode();
        }

        @Override  // GoogleApiClient.ConnectionCallbacks
        public void onConnected(Bundle connectionHint) {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_WEAR, "onConnected: " + connectionHint);
            }

            getConfig();

            Wearable.NodeApi.addListener(mGoogleApiClient, this);
            Wearable.DataApi.addListener(mGoogleApiClient, this);
            requireWeatherInfo(getWeatherRequestType(), false);

        }

        @Override  // GoogleApiClient.ConnectionCallbacks
        public void onConnectionSuspended(int cause) {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_WEAR, "onConnectionSuspended: " + cause);
            }
        }

        @Override  // GoogleApiClient.OnConnectionFailedListener
        public void onConnectionFailed(ConnectionResult result) {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_WEAR, "onConnectionFailed: " + result);
            }
        }

        @Override
        public void onDataChanged(DataEventBuffer dataEvents) {
            for (int i = 0; i < dataEvents.getCount(); i++) {
                DataEvent event = dataEvents.get(i);
                DataMap dataMap = DataMap.fromByteArray(event.getDataItem().getData());
                if (Log.isLoggable(LogType.COMMUNICATION)) {
                    Log.d(Consts.TAG_WEAR, "onDataChanged: " + dataMap);
                }
                fetchData(dataMap);
            }
        }

        //#####################################################
        // Weather Service connection

        @Override
        public void onPeerConnected(Node node) {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_WEAR, "PeerConnected: " + node);
            }
            requireWeatherInfo(getWeatherRequestType(), false);
        }

        private WeatherInfoType getWeatherRequestType() {
            if (weatherDrawable == null) return WeatherInfoType.FORECAST;
            return weatherDrawable.getWeatherRequestType();
        }

        @Override
        public void onPeerDisconnected(Node node) {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_WEAR, "PeerDisconnected: " + node);
            }
        }

        protected void getConfig() {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_WEAR, "SendRequireConfigMessage:");
            }

            if (!mGoogleApiClient.isConnected()) {
                //try to connect
                mGoogleApiClient.connect();
                if (!mGoogleApiClient.isConnected()) {
                    if (Log.isLoggable(LogType.COMMUNICATION)) {
                        Log.d(Consts.TAG_WEAR, "GoogleApiClientNotConnected");
                    }
                    return;
                }
            }


            //add Listener
            Wearable.MessageApi.addListener(mClient, MESSAGE_LISTENER);

            //send massage to receive answer on listener
            Wearable.MessageApi.sendMessage(mGoogleApiClient, "", Consts.PATH_CONFIG, null)
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            if (Log.isLoggable(LogType.COMMUNICATION)) {
                                Log.d(Consts.TAG_WEAR, "Finish Config Status: " + sendMessageResult.getStatus());
                            }
                        }
                    });
        }

        protected void fetchData(DataMap data) {

            //deserialize weather Info
            if (data.containsKey(Consts.KEY_WEATHER_INFO)) {

                byte[] info = data.getByteArray(Consts.KEY_WEATHER_INFO);
                if (info != null) {

                    BitStore reader = new BitStore(info);
                    SerializableArrayList<Info> infoList = new SerializableArrayList<Info>(Info.class);
                    try {
                        infoList.deserialize(reader);
                    } catch (NotImplementedException e) {
                        e.printStackTrace();
                    }

                    Info mWeatherInfo = infoList.get(0);
                    Info mWeatherForecast1 = null;
                    Info mWeatherForecast2 = null;
                    Info mWeatherForecast3 = null;
                    try {
                        if (infoList.size() >= 2) mWeatherForecast1 = infoList.get(1);
                        if (infoList.size() >= 3) mWeatherForecast2 = infoList.get(2);
                        if (infoList.size() >= 4) mWeatherForecast3 = infoList.get(3);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (mWeatherInfo != null) {

                        if (Log.isLoggable(LogType.COMMUNICATION, LogType.WEATHER)) {
                            final StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("\nReceive weather update:\n");
                            stringBuilder.append("NOW: " + (mWeatherInfo == null ? "NULL" : mWeatherInfo.toString()) + "\n");
                            stringBuilder.append("FC1: " + (mWeatherForecast1 == null ? "NULL" : mWeatherForecast1.toString()) + "\n");
                            stringBuilder.append("FC2: " + (mWeatherForecast2 == null ? "NULL" : mWeatherForecast2.toString()) + "\n");
                            stringBuilder.append("FC3: " + (mWeatherForecast3 == null ? "NULL" : mWeatherForecast3.toString()) + "\n");
                            Log.d(Consts.TAG_WEAR, stringBuilder.toString());
                        }


                        if (weatherDrawable == null) {
                            weatherDrawable = new TinyWeatherDrawable(PowerWatchFaceService.this);
                            weatherDrawable.setInvalidateListener(this);
                        }

                        if (Log.isLoggable(LogType.WEATHER)) {
                            Log.d(Consts.TAG_WEAR, "Set NOW Weather:" + mWeatherInfo.toString());
                        }
                        try {
                            weatherDrawable.setActWeather(mWeatherInfo);
                        } catch (Exception e) {
                            if (Log.isLoggable(LogType.WEATHER)) {
                                Log.e(Consts.TAG_WEAR, "Set Act Weather:");
                            }
                        }
                        //Decide which forecast is relevant
                        if (mWeatherForecast1 != null && mWeatherForecast2 != null && mWeatherForecast3 != null) {
                            if ((DateUtils.HOUR_IN_MILLIS * 1.5) + mWeatherInfo.getDate().getTime()
                                    < mWeatherForecast1.getDate().getTime()) {
                                if (Log.isLoggable(LogType.WEATHER)) {
                                    Log.d(Consts.TAG_WEAR, "Set Forcast 1 & 2");
                                }
                                weatherDrawable.setWeatherForecast1(mWeatherForecast1);
                                weatherDrawable.setWeatherForecast2(mWeatherForecast2);
                            } else {
                                if (Log.isLoggable(LogType.WEATHER)) {
                                    Log.d(Consts.TAG_WEAR, "Set Forcast 2 & 3");
                                }
                                weatherDrawable.setWeatherForecast1(mWeatherForecast2);
                                weatherDrawable.setWeatherForecast2(mWeatherForecast3);
                            }
                        } else {
                            if (Log.isLoggable(LogType.WEATHER)) {
                                Log.d(Consts.TAG_WEAR, "any forecast info are NULL, can't decide");
                            }
                        }
                    } else {
                        if (Log.isLoggable(LogType.WEATHER)) {
                            Log.d(Consts.TAG_WEAR, "WeatherInfo are NULL");
                        }
                    }


                    RES.mTime.setToNow();
                    int h = RES.mTime.hour;
                    int m = RES.mTime.minute;

                    boolean isFromPrefs = false;
                    try {
                        isFromPrefs = reader.readBool();
                    } catch (NotImplementedException e) {
                        e.printStackTrace();
                    }

                    lastWeatherUpdateTime = Integer.toString(h) + ":" + Integer.toString(m) + (isFromPrefs ? "P" : "HTTP");
                    debugString = null;


                    setWeatherInfoToUnity();
                }
            }


            //deserialize config
            if (data.containsKey(Consts.KEY_CONFIG)) {

                byte[] info = data.getByteArray(Consts.KEY_CONFIG);

                if (info != null) {

                    BitStore store = new BitStore(info);

                    if (mConfig == null) mConfig = new Config();
                    try {
                        backgroundChanged = mConfig.deserializeVisualIsChanged(store);
                        configLoaded = true;
                    } catch (NotImplementedException e) {
                        e.printStackTrace();
                    }
                    if (Log.isLoggable(LogType.COMMUNICATION, LogType.WEATHER)) {
                        Log.d(Consts.TAG_WEAR, "Config changed: \n " + mConfig.toString());
                    }
                    weatherUpdateInterval.setInterval(mConfig.getInterval());
                    if (Log.isLoggable(LogType.WEATHER)) {
                        Log.d(Consts.TAG_WEAR, "set update interval to:" + weatherUpdateInterval.getInterval());
                    }

                    tinyClockDrawable.setDigital(mConfig.getDigital());
                    tinyClockDrawable.setTimeZone(mConfig.getSecondTimeZone());
                    setWeatherInfoToUnity();


                    Interval.setDebugDivisor(mConfig.getDebugIntervalDevisor());

                }
            }

            if (data.containsKey(Consts.KEY_POWER_INFO)) {
                byte[] info = data.getByteArray(Consts.KEY_POWER_INFO);
                BitStore store = new BitStore(info);
                try {
                    int power = store.readInt();
                    dateDrawable.setPhonePower(power);
                    if (Log.isLoggable(LogType.COMMUNICATION, LogType.POWER)) {
                        Log.d(Consts.TAG_WEAR, "Recive PhonePower:" + power + "%");
                    }
                } catch (NotImplementedException e) {
                    e.printStackTrace();
                }

            }


            invalidate();
        }

        private void setWeatherInfoToUnity() {
            if (mConfig == null || weatherDrawable == null || weatherDrawable.weatherIsNull()) return;

            if (mConfig.getUseCelsius()) {
                weatherDrawable.setOutputToCelsius();
            } else {
                weatherDrawable.setOutputToFahrenheit();
            }
        }

        protected void requireWeatherInfo(WeatherInfoType type, final boolean force) {


            if (DEBUG_WEATHER && weatherDrawable.weatherIsNull()) {
                Info actWeather = new Info();
                Info forecast2 = new Info();
                Info forecast1 = new Info();

                actWeather.setIconID((byte) 10);
                forecast1.setIconID((byte) 30);
                forecast2.setIconID((byte) 50);

                actWeather.setTemp((byte) 10);
                forecast1.setTemp((byte) 30);
                forecast2.setTemp((byte) 50);

                SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
                try {
                    actWeather.setDate(DATE_FORMATTER.parse("2015-11-20 12:00:00"));
                    forecast1.setDate(DATE_FORMATTER.parse("2015-11-20 15:00:00"));
                    forecast2.setDate(DATE_FORMATTER.parse("2015-11-20 18:00:00"));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                weatherDrawable.setActWeather(actWeather);
                weatherDrawable.setWeatherForecast1(forecast1);
                weatherDrawable.setWeatherForecast2(forecast2);
            }

            if (DEBUG_WEATHER) return;

            // The weather Info is still up to date.
            if (force || weatherUpdateInterval.isElapsed() || type == WeatherInfoType.FORECAST) {

                if (!mGoogleApiClient.isConnected()) {
                    //try to connect
                    mGoogleApiClient.connect();
                    if (!mGoogleApiClient.isConnected()) {
                        if (Log.isLoggable(LogType.COMMUNICATION, LogType.WEATHER)) {
                            Log.d(Consts.TAG_WEAR, "GoogleApiClientNotConnected");
                        }
                        return;
                    }
                }

                String TypeString = type == WeatherInfoType.NOW ? Consts.PATH_WEATHER_REQUIRE : Consts.PATH_WEATHER_REQUIRE_FORECAST;
                if (Log.isLoggable(LogType.COMMUNICATION, LogType.WEATHER)) {
                    Log.d(Consts.TAG_WEAR, "SendWeatherRequireMessage: " + TypeString);
                }


                //add Listener
                Wearable.MessageApi.addListener(mClient, MESSAGE_LISTENER);

                //send massage to receive answer on listener
                Wearable.MessageApi.sendMessage(mGoogleApiClient, "", TypeString, null)
                        .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                            @Override
                            public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                if (Log.isLoggable(LogType.COMMUNICATION, LogType.WEATHER)) {
                                    Log.d(Consts.TAG_WEAR, "SendRequireMessage Result:" + sendMessageResult.getStatus());
                                    Log.d(Consts.TAG_WEAR, "SendRequireMessage weatherUpdateInterval:" + weatherUpdateInterval.toString());
                                    Log.d(Consts.TAG_WEAR, "SendRequireMessage Force:" + force);
                                }
                            }
                        });
            } else {
                if (Log.isLoggable(LogType.COMMUNICATION, LogType.WEATHER)) {
                    Log.d(Consts.TAG_WEAR, "Weather request was rejected by Interval :" + weatherUpdateInterval.toString());
                }
            }
        }

        private final MessageApi.MessageListener MESSAGE_LISTENER = new MessageApi.MessageListener() {
            @Override
            public void onMessageReceived(MessageEvent messageEvent) {

                final String path = messageEvent.getPath();

                if (path.equals(Consts.PATH_WEATHER_INFO) ||
                        path.equals(Consts.PATH_CONFIG)) {

                    if (path.equals(Consts.PATH_WEATHER_INFO)) {
                        if (Log.isLoggable(LogType.WEATHER)) {
                            Log.d(Consts.TAG_WEAR, "reset weather update interval");
                        }
                        weatherUpdateInterval.restartInterval();
                    }

                    DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());
                    if (Log.isLoggable(LogType.COMMUNICATION)) {
                        Log.d(Consts.TAG_WEAR, "onDataChanged: " + dataMap);
                    }
                    fetchData(dataMap);
                } else {

                    if (path.equals(Consts.KEY_LOGS)) {
                        if (Log.isLoggable(LogType.COMMUNICATION)) {
                            Log.d(Consts.TAG_WEAR, "Recive Message get logs");
                        }
                        // send logs to phone
                        if (!mGoogleApiClient.isConnected()) {
                            //try to connect
                            mGoogleApiClient.connect();
                            if (!mGoogleApiClient.isConnected()) {
                                if (Log.isLoggable(LogType.COMMUNICATION)) {
                                    Log.d(Consts.TAG_WEAR, "GoogleApiClientNotConnected");
                                }
                                return;
                            }
                        }

                        try {
                            //add Listener
                            Wearable.MessageApi.addListener(mClient, MESSAGE_LISTENER);

                            //send massage to receive answer on listener
                            Wearable.MessageApi.sendMessage(mGoogleApiClient, "", Consts.KEY_LOGS, de.longri.watchface.loging.Log.getLogsForSend())
                                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                                        @Override
                                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                            if (sendMessageResult.getStatus().isSuccess()) {

                                                //delete sendet logs
                                                Log.logs = new SerializableArrayList<>(LogItem.class);
                                            }
                                            // Log.d(Consts.TAG_WEAR, "SendRequireMessage Result:" + sendMessageResult.getStatus());
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }


                    if (Log.isLoggable(LogType.COMMUNICATION)) {
                        Log.d(Consts.TAG_WEAR, "Wrong Message Path =" + path);
                    }
                }
            }
        };

        public void resetFullDraw() {
            fullDraw = FullDraw.None;
        }

        public void setAmbientMode(boolean value) {
            Bundle bundle = new Bundle();
            bundle.putBoolean(EXTRA_AMBIENT_MODE, value);
            this.onCommand(COMMAND_BACKGROUND_ACTION, 0, 0, 0, bundle, false);
        }
    }
}

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
import android.content.SharedPreferences;
import android.text.format.DateUtils;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.Serializable;
import de.longri.serializable.StoreBase;
import de.longri.watchface.loging.Log;
import de.longri.watchface.loging.LogType;


/**
 * A special class for store all configs!
 * <p/>
 * This Class has  method for serialise the compleate class to a ByteArray and
 * a constructor with a ByteArray for create a instance.
 * <p/>
 * The minimum ByteArray can use for transfer from Phone to Wear with minimum of traffic!
 * <p/>
 * Created by Longri on 29.10.15.
 */
public class Config implements Serializable {

    public static SharedPreferences mAndroidSetting;
    public static SharedPreferences.Editor mAndroidSettingEditor;

    //Masks
    private final byte MASK_TIME_UNIT = (byte) (1 << 6);
    private final byte MASK_USE_CELSIUS = (byte) (1 << 7);
    private final byte MASK_THEME = 7;
    private final byte MASK_UPDATE_INTERVAL = 56;

    // next four member are stored at one byte
    private int theme;             // bit 0-2 possible values 0-7
    private int updateInterval;    // bit 3-5 possible values 0-5 {1h, 2h, 4h, 8h, 12h, 24h}
    private boolean timeUnit;       // bit 6   true = 24h / false = 12h
    private boolean useCelsius;     // bit 7   true = Celsius / false = Fahrenheit

    private boolean LogWEATHER = false;
    private boolean LogCOMMUNICATION = false;
    private boolean LogPOWER = false;
    private boolean LogDRAW = false;
    private boolean LogTouchInput = false;
    private boolean LogHTTP = false;
    private int debugIntervalDevisor = 1;

    private boolean showDigitalClock = false;
    private WearTimeZone secondTimeZone = WearTimeZone.getAvailableTimeZones()[25];
    private boolean showScale = false;
    private boolean showScaleValue = true;
    private boolean showScaleAmbient = false;
    private boolean showScaleValueAmbient = false;

    private View[] views = new View[]{View.Logo, View.Date, View.SecondTime, View.Weather};
    private boolean debug;
    private byte brightness = 100; //100-0

    @Override
    public void serialize(StoreBase storeBase) throws NotImplementedException {
        byte first = 0;

        //Store first three bits for Theme
        first |= MASK_THEME & theme;

        //Store three bits from UpdateInterval to bit 3-5
        first |= MASK_UPDATE_INTERVAL & (updateInterval << 3);

        //Store bit 6 for timeUnit
        first = setMaskValue(first, MASK_TIME_UNIT, timeUnit);

        //Store bit 7 for useCelsius
        first = setMaskValue(first, MASK_USE_CELSIUS, useCelsius);
        storeBase.write(first);

        storeBase.write(views[0].ordinal());
        storeBase.write(views[1].ordinal());
        storeBase.write(views[2].ordinal());
        storeBase.write(views[3].ordinal());

        storeBase.write(debug);
        storeBase.write(LogWEATHER);
        storeBase.write(LogCOMMUNICATION);
        storeBase.write(LogPOWER);
        storeBase.write(LogDRAW);
        storeBase.write(LogTouchInput);
        storeBase.write(LogHTTP);
        storeBase.write(showDigitalClock);
        this.secondTimeZone.serialize(storeBase);
        storeBase.write(debugIntervalDevisor);
        storeBase.write(brightness);
        storeBase.write(showScale);
        storeBase.write(showScaleValue);
        storeBase.write(showScaleAmbient);
        storeBase.write(showScaleValueAmbient);
    }

    @Override
    public void deserialize(StoreBase storeBase) throws NotImplementedException {
        deserializeVisualIsChanged(storeBase);
    }


    public boolean deserializeVisualIsChanged(StoreBase storeBase) throws NotImplementedException {
        boolean isChanged = false;


        byte first = storeBase.readByte();
        theme = (byte) (MASK_THEME & first);
        updateInterval = (byte) ((MASK_UPDATE_INTERVAL & first) >>> 3);

        boolean val = getMaskValue(first, MASK_TIME_UNIT);
        if (timeUnit != val) isChanged = true;
        timeUnit = val;

        val = getMaskValue(first, MASK_USE_CELSIUS);
        if (useCelsius != val) isChanged = true;
        useCelsius = val;

        View[] viewValues = View.values();


        View[] storeViews = new View[]{viewValues[storeBase.readInt()], viewValues[storeBase.readInt()],
                viewValues[storeBase.readInt()], viewValues[storeBase.readInt()]};

        int index = 0;
        for (View v : storeViews) {
            if (views[index++] != v) isChanged = true;
        }
        views = storeViews;

        debug = storeBase.readBool();
        LogWEATHER = storeBase.readBool();
        LogCOMMUNICATION = storeBase.readBool();
        LogPOWER = storeBase.readBool();
        LogDRAW = storeBase.readBool();
        LogTouchInput = storeBase.readBool();
        LogHTTP = storeBase.readBool();

        // set to LOG
        if (debug) Log.setLoggable(Log.LogTo.Both);
        else Log.setLoggable(Log.LogTo.NONE);

        if (LogWEATHER) Log.addLogType(LogType.WEATHER);
        else Log.removeLogType(LogType.WEATHER);

        if (LogCOMMUNICATION) Log.addLogType(LogType.COMMUNICATION);
        else Log.removeLogType(LogType.COMMUNICATION);

        if (LogPOWER) Log.addLogType(LogType.POWER);
        else Log.removeLogType(LogType.POWER);

        if (LogDRAW) Log.addLogType(LogType.DRAW);
        else Log.removeLogType(LogType.DRAW);

        if (LogTouchInput) Log.addLogType(LogType.TouchInput);
        else Log.removeLogType(LogType.TouchInput);

        if (LogHTTP) Log.addLogType(LogType.HTTP);
        else Log.removeLogType(LogType.HTTP);

        showDigitalClock = storeBase.readBool();
        this.secondTimeZone = new WearTimeZone(storeBase);
        debugIntervalDevisor = storeBase.readInt();

        byte br = storeBase.readByte();
        if (brightness != br) isChanged = true;
        brightness = br;


        boolean show = storeBase.readBool();
        if (showScale != show) isChanged = true;
        showScale = show;

        show = storeBase.readBool();
        if (showScaleValue != show) isChanged = true;
        showScaleValue = show;

        show = storeBase.readBool();
        if (showScaleAmbient != show) isChanged = true;
        showScaleAmbient = show;

        show = storeBase.readBool();
        if (showScaleValueAmbient != show) isChanged = true;
        showScaleValueAmbient = show;

        return isChanged;
    }


    public Config() {
    }

    private byte setMaskValue(byte store, byte mask, boolean value) {
        if (getMaskValue(store, mask) == value) return store;

        if (value) {
            store |= mask;
        } else {
            store &= ~mask;
        }
        return store;
    }

    private boolean getMaskValue(byte store, byte mask) {
        return (store & mask) == mask;
    }

    public int getPositionOf(View view) {
        int index = 0;
        for (View v : views) {
            if (v == view) return index;
            index++;
        }
        return -1;
    }

    public void loadFromPreferences(Context context) {
        chkPreferences(context);
        this.useCelsius = mAndroidSetting.getBoolean(Consts.KEY_CONFIG_TEMPERATURE_SCALE, true);
        this.timeUnit = mAndroidSetting.getBoolean(Consts.KEY_CONFIG_TIME_UNIT, false);
        this.showDigitalClock = mAndroidSetting.getBoolean(Consts.KEY_CONFIG_DIGI, false);

        int timZoneIndex = mAndroidSetting.getInt(Consts.KEY_SECOND_TIME_ZONE, 25);

        this.secondTimeZone = WearTimeZone.getAvailableTimeZones()[timZoneIndex];
        this.debugIntervalDevisor = mAndroidSetting.getInt(Consts.KEY_CONFIG_DEBUG_INTERVAL_DEVISOR, 1);


        View[] viewValues = View.values();
        this.views[0] = viewValues[mAndroidSetting.getInt(Consts.KEY_CONFIG_VIEW_TOP, 1)];
        this.views[1] = viewValues[mAndroidSetting.getInt(Consts.KEY_CONFIG_VIEW_RIGHT, 2)];
        this.views[2] = viewValues[mAndroidSetting.getInt(Consts.KEY_CONFIG_VIEW_BOTTOM, 3)];
        this.views[3] = viewValues[mAndroidSetting.getInt(Consts.KEY_CONFIG_VIEW_LEFT, 0)];
        //debug
        debug = mAndroidSetting.getBoolean(Consts.KEY_DEBUG, false);
        if (debug) Log.setLoggable(Log.LogTo.Both);
        else Log.setLoggable(Log.LogTo.NONE);

        LogWEATHER = mAndroidSetting.getBoolean(Consts.KEY_LOG_WEATHER, false);
        if (LogWEATHER) Log.addLogType(LogType.WEATHER);
        else Log.removeLogType(LogType.WEATHER);

        LogCOMMUNICATION = mAndroidSetting.getBoolean(Consts.KEY_LOG_COMMUNICATION, false);
        if (LogCOMMUNICATION) Log.addLogType(LogType.COMMUNICATION);
        else Log.removeLogType(LogType.COMMUNICATION);

        LogPOWER = mAndroidSetting.getBoolean(Consts.KEY_LOG_POWER, false);
        if (LogPOWER) Log.addLogType(LogType.POWER);
        else Log.removeLogType(LogType.POWER);

        LogDRAW = mAndroidSetting.getBoolean(Consts.KEY_LOG_DRAW, false);
        if (LogDRAW) Log.addLogType(LogType.DRAW);
        else Log.removeLogType(LogType.DRAW);

        LogTouchInput = mAndroidSetting.getBoolean(Consts.KEY_LOG_TouchInput, false);
        if (LogTouchInput) Log.addLogType(LogType.TouchInput);
        else Log.removeLogType(LogType.TouchInput);

        LogHTTP = mAndroidSetting.getBoolean(Consts.KEY_LOG_HTTP, false);
        if (LogHTTP) Log.addLogType(LogType.HTTP);
        else Log.removeLogType(LogType.HTTP);

        brightness = (byte) mAndroidSetting.getInt(Consts.KEY_BRIGHTNESS, 100);

        showScale = mAndroidSetting.getBoolean(Consts.KEY_CONFIG_SCALE, false);
        showScaleValue = mAndroidSetting.getBoolean(Consts.KEY_CONFIG_SCALE_VALUE, true);
        showScaleAmbient = mAndroidSetting.getBoolean(Consts.KEY_CONFIG_SCALE_AMBIENT, false);
        showScaleValueAmbient = mAndroidSetting.getBoolean(Consts.KEY_CONFIG_SCALE_VALUE_AMBIENT, false);
    }

    public static void chkPreferences(Context context) {
        if (mAndroidSetting == null) {
            mAndroidSetting = context.getSharedPreferences(Consts.PREFS_NAME, 0);
            mAndroidSettingEditor = mAndroidSetting.edit();
        }
    }


    public void setViewPositionTop(Context context, View view) {
        if (this.views[0] == view) return;
        this.views[0] = view;
        if (this.views[1] == view) this.views[1] = View.None;
        if (this.views[2] == view) this.views[2] = View.None;
        if (this.views[3] == view) this.views[3] = View.None;
        saveViewPositions(context);
    }

    public void setViewPositionRight(Context context, View view) {
        if (this.views[0] == view) this.views[0] = View.None;
        if (this.views[1] == view) return;
        this.views[1] = view;
        if (this.views[2] == view) this.views[2] = View.None;
        if (this.views[3] == view) this.views[3] = View.None;
        saveViewPositions(context);
    }

    public void setViewPositionBottom(Context context, View view) {
        if (this.views[0] == view) this.views[0] = View.None;
        if (this.views[1] == view) this.views[1] = View.None;
        if (this.views[2] == view) return;
        this.views[2] = view;
        if (this.views[3] == view) this.views[3] = View.None;
        saveViewPositions(context);
    }

    public void setViewPositionLeft(Context context, View view) {
        if (this.views[0] == view) this.views[0] = View.None;
        if (this.views[1] == view) this.views[1] = View.None;
        if (this.views[2] == view) this.views[2] = View.None;
        if (this.views[3] == view) return;
        this.views[3] = view;
        saveViewPositions(context);
    }

    private void saveViewPositions(Context context) {
        chkPreferences(context);
        mAndroidSettingEditor.putInt(Consts.KEY_CONFIG_VIEW_TOP, this.views[0].ordinal());
        mAndroidSettingEditor.putInt(Consts.KEY_CONFIG_VIEW_RIGHT, this.views[1].ordinal());
        mAndroidSettingEditor.putInt(Consts.KEY_CONFIG_VIEW_BOTTOM, this.views[2].ordinal());
        mAndroidSettingEditor.putInt(Consts.KEY_CONFIG_VIEW_LEFT, this.views[3].ordinal());
        mAndroidSettingEditor.commit();
    }

    public void setTheme(Context context, int theme) {
        this.theme = (byte) theme;
        chkPreferences(context);
        mAndroidSettingEditor.putInt(Consts.KEY_CONFIG_THEME, theme);
        mAndroidSettingEditor.commit();
    }

    public void setTimeUnit(Context context, boolean timeUnit) {
        this.timeUnit = timeUnit;
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_CONFIG_TIME_UNIT, timeUnit);
        mAndroidSettingEditor.commit();
    }

    public void setInterval(Context context, int interval) {
        this.updateInterval = interval;
        chkPreferences(context);
        mAndroidSettingEditor.putInt(Consts.KEY_WEATHER_UPDATE_TIME, interval);
        mAndroidSettingEditor.commit();
    }


    public void setDebugIntervalDevisor(Context context, int interval) {
        this.debugIntervalDevisor = interval;
        chkPreferences(context);
        mAndroidSettingEditor.putInt(Consts.KEY_CONFIG_DEBUG_INTERVAL_DEVISOR, interval);
        mAndroidSettingEditor.commit();
    }

    public void setUseCelsius(Context context, boolean celsius) {
        this.useCelsius = celsius;
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_CONFIG_TEMPERATURE_SCALE, celsius);
        mAndroidSettingEditor.commit();
    }


    public void setShowScale(Context context, boolean value) {
        this.showScale = value;
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_CONFIG_SCALE, value);
        mAndroidSettingEditor.commit();
    }

    public void setShowScaleValue(Context context, boolean value) {
        this.showScaleValue = value;
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_CONFIG_SCALE_VALUE, value);
        mAndroidSettingEditor.commit();
    }

    public void setShowScaleAmbient(Context context, boolean value) {
        this.showScaleAmbient = value;
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_CONFIG_SCALE_AMBIENT, value);
        mAndroidSettingEditor.commit();
    }

    public void setShowScaleValueAmbient(Context context, boolean value) {
        this.showScaleValueAmbient = value;
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_CONFIG_SCALE_VALUE_AMBIENT, value);
        mAndroidSettingEditor.commit();
    }

    public boolean getShowScale() {
        return this.showScale;
    }

    public boolean getShowScaleValue() {
        return this.showScaleValue;
    }

    public boolean getShowScaleAmbient() {
        return this.showScaleAmbient;
    }

    public boolean getShowScaleValueAmbient() {
        return this.showScaleValueAmbient;
    }

    public int getIntervalSpinnerPos() {
        return this.updateInterval;
    }

    public long getInterval() {

        switch (this.updateInterval) {
            case 1:
                return 2 * DateUtils.HOUR_IN_MILLIS;
            case 2:
                return 4 * DateUtils.HOUR_IN_MILLIS;
            case 3:
                return 8 * DateUtils.HOUR_IN_MILLIS;
            case 4:
                return 12 * DateUtils.HOUR_IN_MILLIS;
            case 5:
                return 24 * DateUtils.HOUR_IN_MILLIS;
        }

        return DateUtils.HOUR_IN_MILLIS;
    }

    public int getTheme() {
        return this.theme;
    }

    public int getDebugIntervalDevisor() {
        return this.debugIntervalDevisor;
    }

    public boolean getTimeUnit() {
        return timeUnit;
    }

    public boolean getUseCelsius() {
        return useCelsius;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TimeUnit =" + timeUnit);
        sb.append("\n");

        sb.append("UseCelsius =" + useCelsius);
        sb.append("\n");

        sb.append("UpdateInterval =" + getInterval());
        sb.append("\n");

        sb.append("TopView =" + this.views[0]);
        sb.append("\n");

        sb.append("RightView =" + this.views[1]);
        sb.append("\n");

        sb.append("BottomView =" + this.views[2]);
        sb.append("\n");

        sb.append("LeftView =" + this.views[3]);
        sb.append("\n");

        return sb.toString();
    }


    public boolean getDebug() {
        return debug;
    }

    public void setDebug(Context context, boolean debug) {
        this.debug = debug;
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_DEBUG, debug);
        mAndroidSettingEditor.commit();

        debug = mAndroidSetting.getBoolean(Consts.KEY_DEBUG, false);
        if (debug) Log.setLoggable(Log.LogTo.Both);
        else Log.setLoggable(Log.LogTo.NONE);
    }


    //Log Types


    public boolean isLogWEATHER() {
        return LogWEATHER;
    }

    public void setLogWEATHER(Context context, boolean value) {
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_LOG_WEATHER, value);
        mAndroidSettingEditor.commit();
        LogWEATHER = value;
    }

    public boolean isLogCOMMUNICATION() {
        return LogCOMMUNICATION;
    }

    public void setLogCOMMUNICATION(Context context, boolean value) {
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_LOG_COMMUNICATION, value);
        mAndroidSettingEditor.commit();
        LogCOMMUNICATION = value;
    }

    public boolean isLogPOWER() {
        return LogPOWER;
    }

    public void setLogPOWER(Context context, boolean value) {
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_LOG_POWER, value);
        mAndroidSettingEditor.commit();
        LogPOWER = value;
    }

    public boolean isLogDRAW() {
        return LogDRAW;
    }

    public void setLogDRAW(Context context, boolean value) {
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_LOG_DRAW, value);
        mAndroidSettingEditor.commit();
        LogDRAW = value;
    }

    public boolean isLogTouchInput() {
        return LogTouchInput;
    }

    public void setLogTouchInput(Context context, boolean value) {
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_LOG_TouchInput, value);
        mAndroidSettingEditor.commit();
        LogTouchInput = value;
    }

    public boolean isLogHTTP() {
        return LogHTTP;
    }

    public void setLogHTTP(Context context, boolean value) {
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_LOG_HTTP, value);
        mAndroidSettingEditor.commit();
        LogHTTP = value;
    }

    public boolean getDigital() {
        return showDigitalClock;
    }

    public void setDigital(Context context, boolean value) {
        chkPreferences(context);
        mAndroidSettingEditor.putBoolean(Consts.KEY_CONFIG_DIGI, value);
        mAndroidSettingEditor.commit();
        showDigitalClock = value;
    }

    public void setSecondTimeZone(Context context, WearTimeZone timeZone) {
        chkPreferences(context);
        mAndroidSettingEditor.putInt(Consts.KEY_SECOND_TIME_ZONE, timeZone.getIndex());
        mAndroidSettingEditor.commit();
        secondTimeZone = timeZone;
    }

    public WearTimeZone getSecondTimeZone() {
        return secondTimeZone;
    }

    public byte getBrightness() {
        return brightness;
    }

    public void setBrightness(Context context, int value) {
        chkPreferences(context);
        mAndroidSettingEditor.putInt(Consts.KEY_BRIGHTNESS, value);
        mAndroidSettingEditor.commit();
        brightness = (byte) value;
    }
}

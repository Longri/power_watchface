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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Bundle;
import android.util.Base64;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.*;
import de.longri.watchface.loging.Log;
import de.longri.watchface.loging.LogItem;
import de.longri.watchface.loging.LogType;
import de.longri.serializable.BitStore;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.SerializableArrayList;
import de.longri.weather.Info;
import de.longri.weather.openweather.OpenWeatherApi;
import roboguice.RoboGuice;

import java.util.ArrayList;
import java.util.Date;


public class WeatherService extends WearableListenerService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private Location mLocation;
    private String mPeerId;


    @Override
    public void onCreate() {
        super.onCreate();


        //check Log
        if (!Log.isInitial()) new Log(this, false);

        Log.d(Consts.TAG_PHONE, "Created");

        if (null == mGoogleApiClient) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_PHONE, "GoogleApiClient created");
            }
        }

        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_PHONE, "Connecting to GoogleApiClient..");
            }
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (Log.isLoggable(LogType.COMMUNICATION)) {
            Log.d(Consts.TAG_PHONE, "onStartCommand()");
        }


        if (intent != null) {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_PHONE, "Intent Action: " + intent.getAction());
            }
            if (WeatherWatchFaceConfigActivity.class.getSimpleName().equals(intent.getAction())) {
                mPeerId = intent.getStringExtra("PeerId");
                startWeatherTask(WeatherInfoType.FORECAST);
            }
        } else {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_PHONE, "onStartCommand() with Intent are null");
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);
        mPeerId = messageEvent.getSourceNodeId();
        if (Log.isLoggable(LogType.COMMUNICATION))
            Log.d(Consts.TAG_PHONE, "MessageReceived: " + messageEvent.getPath());

        if (messageEvent.getPath().equals(Consts.PATH_WEATHER_REQUIRE)) {
            if (Log.isLoggable(LogType.COMMUNICATION)) Log.d(Consts.TAG_PHONE, "Start weather Task Now");
            startWeatherTask(WeatherInfoType.NOW);
        } else if (messageEvent.getPath().equals(Consts.PATH_WEATHER_REQUIRE_FORECAST)) {
            if (Log.isLoggable(LogType.COMMUNICATION)) Log.d(Consts.TAG_PHONE, "Start weather Task Forecast");
            startWeatherTask(WeatherInfoType.FORECAST);
        } else if (messageEvent.getPath().equals(Consts.PATH_CONFIG)) {
            if (Log.isLoggable(LogType.COMMUNICATION)) Log.d(Consts.TAG_PHONE, "Start Config Task");
            startConfigTask();
        } else if (messageEvent.getPath().equals(Consts.PATH_PHONE_POWER)) {
            if (Log.isLoggable(LogType.COMMUNICATION)) Log.d(Consts.TAG_PHONE, "Start Power Task");
            startPhonePowerTask();
        } else if (messageEvent.getPath().equals(Consts.KEY_LOGS)) {

            BitStore reader = new BitStore(messageEvent.getData());

            try {
                ArrayList<LogItem> list = reader.readList(LogItem.class);

                for (LogItem item : list) {
                    Log.Item(item);
                }


            } catch (NotImplementedException e) {
                e.printStackTrace();
            }

        } else {
            if (Log.isLoggable(LogType.COMMUNICATION)) {
                Log.d(Consts.TAG_PHONE, "Wrong Path =" + messageEvent.getPath());
            }
        }
    }

    private void startConfigTask() {
        ConfigTask configTask = new ConfigTask();
        configTask.execute();
    }

    private void startPhonePowerTask() {
        PhonePowerTask phonePowerTask = new PhonePowerTask();
        phonePowerTask.execute();
    }

    private void startWeatherTask(final WeatherInfoType type) {
        if (Log.isLoggable(LogType.WEATHER)) {
            Log.d(Consts.TAG_PHONE, "Start Weather AsyncTask");
        }
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Wearable.API).build();

        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (mLocation == null) {
            mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            if (Log.isLoggable(LogType.WEATHER)) {
                                Log.d(Consts.TAG_PHONE, "onLocationChanged: " + location);
                            }
                            mLocationManager.removeUpdates(this);
                            mLocation = location;
                            WeatherTask task = new WeatherTask(type);
                            task.execute();
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }
            );
        } else {
            WeatherTask weatherTask = new WeatherTask(type);
            weatherTask.execute();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (Log.isLoggable(LogType.COMMUNICATION)) {
            Log.d(Consts.TAG_PHONE, "onConnected called");
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (Log.isLoggable(LogType.COMMUNICATION)) {
            Log.d(Consts.TAG_PHONE, "onConnectionSuspended called");
        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (Log.isLoggable(LogType.COMMUNICATION)) {
            Log.d(Consts.TAG_PHONE, "onConnectionFailed called");
        }
    }

    private class WeatherTask extends AsyncTask {

        private final WeatherInfoType type;

        public WeatherTask(WeatherInfoType type) {
            this.type = type;
        }

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                if (Log.isLoggable(LogType.WEATHER)) {
                    Log.d(Consts.TAG_PHONE, "WeatherTask Running");
                }
                Context context = WeatherService.this.getApplicationContext();
                RoboGuice.getInjector(context).injectMembers(this);
                SerializableArrayList<Info> info = new SerializableArrayList<>(Info.class);
                boolean fromPref = false;

                //load last weather info from preferences
                Config.chkPreferences(context);
                byte[] infoBytes = Base64.decode(Config.mAndroidSetting.getString(Consts.KEY_CONFIG_LAST_WEATHER_INFO, ""), Base64.DEFAULT);
                if (infoBytes.length > 0) {
                    BitStore reader = new BitStore(infoBytes);
                    try {
                        info.deserialize(reader);
                        if (info.size() > 0 && info.size() != 1) {
                            Info actWeatherInfo = info.get(0);

                            //check if weather older then 30 min
                            Date now = new Date(new Date().getTime() - 1800000);
                            if (actWeatherInfo.getDate().after(now)) {
                                fromPref = true;
                                if (Log.isLoggable(LogType.WEATHER)) {
                                    Log.d(Consts.TAG_PHONE, "read weather from preferences");
                                    Log.d(Consts.TAG_PHONE, "now - 30 min:" + now);
                                    Log.d(Consts.TAG_PHONE, "WetherInfoDate:" + actWeatherInfo.getDate());
                                }
                            } else {
                                if (Log.isLoggable(LogType.WEATHER)) {
                                    Log.e(Consts.TAG_PHONE, "read weather from HTTP");
                                    Log.e(Consts.TAG_PHONE, "now - 30 min:" + now);
                                    Log.e(Consts.TAG_PHONE, "WetherInfoDate:" + actWeatherInfo.getDate());
                                }
                            }
                        }
                    } catch (Exception e) {
                        if (Log.isLoggable(LogType.WEATHER)) {
                            Log.e(Consts.TAG_PHONE, "Read weather info from preferences");
                        }
                    }
                }

                if (!fromPref) {

                    //TODO change to set as config property


                    String key = getResources().getString(R.string.openweatherApiKey);


                    OpenWeatherApi api = new OpenWeatherApi(key);
                    if (type == WeatherInfoType.NOW)
                        info = api.getCurrentWeatherInfo(mLocation.getLatitude(), mLocation.getLongitude());
                    if (type == WeatherInfoType.FORECAST)
                        info = api.getForecastWeatherInfo(3, mLocation.getLatitude(), mLocation.getLongitude());
                }
                BitStore writer = new BitStore();
                info.serialize(writer);


                //safe weather info to preferences
                if (!fromPref) {
                    String saveString = Base64.encodeToString(writer.getArray(), Base64.DEFAULT);
                    Config.mAndroidSettingEditor.putString(Consts.KEY_CONFIG_LAST_WEATHER_INFO, saveString);
                    Config.mAndroidSettingEditor.commit();
                }

                writer.write(fromPref);


                //send to wear
                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }

                DataMap data = new DataMap();
                data.putByteArray(Consts.KEY_WEATHER_INFO, writer.getArray());

                // additional PhonePower info
                data.putByteArray(Consts.KEY_POWER_INFO, getPhonePowerData());

                if (Log.isLoggable(LogType.COMMUNICATION, LogType.WEATHER)) {

                    final StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Send weather Info:" + (fromPref ? "from preferences" : "from openWeather.com") + "\n");
                    stringBuilder.append("NOW: " + (info.get(0) == null ? "NULL" : info.get(0).toString()) + "\n");
                    stringBuilder.append("FC1: " + (info.get(1) == null ? "NULL" : info.get(1).toString()) + "\n");
                    stringBuilder.append("FC2: " + (info.get(2) == null ? "NULL" : info.get(2).toString()) + "\n");
                    stringBuilder.append("FC3: " + (info.get(3) == null ? "NULL" : info.get(3).toString()) + "\n");
                    Log.d(Consts.TAG_PHONE, stringBuilder.toString());
                }
                Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, Consts.PATH_WEATHER_INFO, data.toByteArray())
                        .setResultCallback(
                                new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                        if (Log.isLoggable(LogType.COMMUNICATION)) {
                                            Log.d(Consts.TAG_PHONE, "SendUpdateMessage: " + sendMessageResult.getStatus());
                                        }
                                    }
                                }
                        );
            } catch (Exception e) {
                if (Log.isLoggable(LogType.COMMUNICATION)) {
                    Log.d(Consts.TAG_PHONE, "WeatherTask Fail: " + e);
                }
            }
            return null;
        }
    }

    private class ConfigTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                if (Log.isLoggable(LogType.COMMUNICATION)) {
                    Log.d(Consts.TAG_PHONE, "ConfigTask Running");
                }
                RoboGuice.getInjector(WeatherService.this.getApplicationContext()).injectMembers(this);

                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
                DataMap data = new DataMap();

                Config config = new Config();
                config.loadFromPreferences(WeatherService.this);

                BitStore store = new BitStore();
                config.serialize(store);
                data.putByteArray(Consts.KEY_CONFIG, store.getArray());

                // additional PhonePower info
                data.putByteArray(Consts.KEY_POWER_INFO, getPhonePowerData());

                Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, Consts.PATH_CONFIG, data.toByteArray())
                        .setResultCallback(
                                new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                        if (Log.isLoggable(LogType.COMMUNICATION)) {
                                            Log.d(Consts.TAG_PHONE, "SendConfigUpdateMessage: " + sendMessageResult.getStatus());
                                        }
                                    }
                                }
                        );
            } catch (Exception e) {
                if (Log.isLoggable(LogType.COMMUNICATION)) {
                    Log.d(Consts.TAG_PHONE, "ConfigTask Fail: " + e);
                }
            }
            return null;
        }
    }


    private class PhonePowerTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {
            try {
                if (Log.isLoggable(LogType.COMMUNICATION, LogType.POWER)) {
                    Log.d(Consts.TAG_PHONE, "PowerTask Running");
                }
                RoboGuice.getInjector(WeatherService.this.getApplicationContext()).injectMembers(this);

                if (!mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
                DataMap data = new DataMap();
                data.putByteArray(Consts.KEY_POWER_INFO, getPhonePowerData());

                Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, Consts.PATH_CONFIG, data.toByteArray())
                        .setResultCallback(
                                new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                        if (Log.isLoggable(LogType.COMMUNICATION)) {
                                            Log.d(Consts.TAG_PHONE, "SendPhonePowerUpdateMessage: " + sendMessageResult.getStatus());
                                        }
                                    }
                                }
                        );
            } catch (Exception e) {
                if (Log.isLoggable(LogType.COMMUNICATION)) {
                    Log.d(Consts.TAG_PHONE, "PhonePowerTask Fail: " + e);
                }
            }
            return null;
        }
    }


    private byte[] getPhonePowerData() throws NotImplementedException {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = this.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        BitStore store = new BitStore();
        store.write(level);
        return store.getArray();
    }


}


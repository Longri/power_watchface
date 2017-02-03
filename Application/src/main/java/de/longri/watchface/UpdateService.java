package de.longri.watchface;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.support.wearable.companion.WatchFaceCompanion;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static de.longri.watchface.Consts.PREFS_NAME;

public class UpdateService extends Service {

    public static final int notify = 300000;  //interval between two services(Here Service run every 5 Minute)
    private Handler mHandler = new Handler();   //run on another Thread to avoid crash
    private Timer mTimer = null;    //timer handling

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        if (mTimer != null) // Cancel if already existed
            mTimer.cancel();
        else
            mTimer = new Timer();   //recreate new
        mTimer.scheduleAtFixedRate(new TimeDisplay(), 0, notify);   //Schedule task
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimer.cancel();    //For Cancel Timer
        Toast.makeText(this, "Service is Destroyed", Toast.LENGTH_SHORT).show();
    }

    //class TimeDisplay for handling task
    class TimeDisplay extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();

                    Calendar calendar = Calendar.getInstance();
                    int hour = calendar.get(Calendar.HOUR_OF_DAY);
                    int minute = calendar.get(Calendar.MINUTE);
                    int day = calendar.get(Calendar.DAY_OF_YEAR);

                    int time = hour * 60 + minute;
                    int updateTime = 1440; // 24h

                    int interval = settings.getInt(Consts.KEY_WEATHER_UPDATE_TIME, -1);
                    switch (interval) {
                        case 1:
                            updateTime = 1 * 60;
                        case 2:
                            updateTime = 2 * 60;
                        case 3:
                            updateTime = 4 * 60;
                        case 4:
                            updateTime = 8 * 60;
                        case 5:
                            updateTime = 12 * 60;
                        case 6:
                            updateTime = 24 * 60;
                    }


                    boolean weatherUpdate = settings.getInt(Consts.LAST_WEATHER_UPDATE_DAY, -1) != day ||
                            settings.getInt(Consts.LAST_WEATHER_UPDATE_DAY, -1) + updateTime < time;

                    String peerId = settings.getString(WatchFaceCompanion.EXTRA_PEER_ID, "");

                    if (weatherUpdate) {
                        Intent intent = new Intent(UpdateService.this, WeatherService.class);
                        intent.setAction(UpdateService.class.getSimpleName());
                        intent.putExtra("PeerId", peerId);
                        intent.putExtra("Force", 1);
                        startService(intent);
                        Toast.makeText(UpdateService.this, "Send weather Update!", Toast.LENGTH_SHORT).show();


                        editor.putInt(Consts.LAST_WEATHER_UPDATE_TIME, time);
                        editor.putInt(Consts.LAST_WEATHER_UPDATE_DAY, day);
                        editor.commit();

                    }
                }
            });
        }
    }
}
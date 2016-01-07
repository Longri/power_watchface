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


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import de.longri.watchface.loging.Log;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.Timer;
import java.util.TimerTask;

@ContentView(R.layout.about_version_page)
public class AboutActivity extends RoboActivity {

    @InjectView(R.id.LogView)
    private TextView mLogTextView;

    @InjectView(R.id.debugSwitch)
    private Switch mDebugSwitch;

    @InjectView(R.id.debugLayout)
    private LinearLayout mDebugLayout;

    @InjectView(R.id.debugWEATHER)
    private CheckBox mChkWeather;

    @InjectView(R.id.debugCOMMUNICATION)
    private CheckBox mChkComm;

    @InjectView(R.id.debugPOWER)
    private CheckBox mChkPower;

    @InjectView(R.id.debugDRAW)
    private CheckBox mChkDraw;

    @InjectView(R.id.debugTouchInput)
    private CheckBox mChkTouch;

    @InjectView(R.id.debugHTTP)
    private CheckBox mChkHTTP;

    @InjectView(R.id.debugIntervallDividor)
    private CheckBox mChkInterval;

    @InjectView(R.id.btnClear)
    private Button mBtnClearLogs;

    @InjectView(R.id.btnRefresh)
    private Button mBtnRefreshLogs;

    @InjectView(R.id.donate)
    private Button mBtnDonate;

    private boolean alreadyInitialize = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mBtnClearLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.clear();
                Log.writeLogs(mLogTextView);
            }
        });

        mBtnDonate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String url = getResources().getString(R.string.donateUrl);

                try {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url.trim()));
                    AboutActivity.this.startActivityForResult(browserIntent, 1);
                } catch (Exception exc) {
                    android.util.Log.d(Consts.TAG_PHONE, "cant open ext url");
                }
            }
        });

        mBtnRefreshLogs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send massage to wear for get logs
                WeatherWatchFaceConfigActivity.THAT.sendGetLogsMessage();


                // wait 5 sec for recive and refresh TextView
                final Timer timer = new Timer();
                final TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        AboutActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Log.writeLogs(mLogTextView);
                            }
                        });
                    }
                };
                timer.schedule(timerTask, 5000);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!alreadyInitialize) {
            setPreferencesToUI();
        }
    }

    private void setPreferencesToUI() {

        WeatherWatchFaceConfigActivity.THAT.mConfig.loadFromPreferences(this);
        Log.writeLogs(mLogTextView);


        switchDebug(WeatherWatchFaceConfigActivity.THAT.mConfig.getDebug());
        mDebugSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchDebug(isChecked);
            }
        });


        mChkWeather.setChecked(WeatherWatchFaceConfigActivity.THAT.mConfig.isLogWEATHER());
        mChkWeather.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherWatchFaceConfigActivity.THAT.mConfig.setLogWEATHER(AboutActivity.this, isChecked);
                WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
            }
        });

        mChkComm.setChecked(WeatherWatchFaceConfigActivity.THAT.mConfig.isLogCOMMUNICATION());
        mChkComm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherWatchFaceConfigActivity.THAT.mConfig.setLogCOMMUNICATION(AboutActivity.this, isChecked);
                WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
            }
        });

        mChkPower.setChecked(WeatherWatchFaceConfigActivity.THAT.mConfig.isLogPOWER());
        mChkPower.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherWatchFaceConfigActivity.THAT.mConfig.setLogPOWER(AboutActivity.this, isChecked);
                WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
            }
        });

        mChkDraw.setChecked(WeatherWatchFaceConfigActivity.THAT.mConfig.isLogDRAW());
        mChkDraw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherWatchFaceConfigActivity.THAT.mConfig.setLogDRAW(AboutActivity.this, isChecked);
                WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
            }
        });

        mChkTouch.setChecked(WeatherWatchFaceConfigActivity.THAT.mConfig.isLogTouchInput());
        mChkTouch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherWatchFaceConfigActivity.THAT.mConfig.setLogTouchInput(AboutActivity.this, isChecked);
                WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
            }
        });

        mChkHTTP.setChecked(WeatherWatchFaceConfigActivity.THAT.mConfig.isLogHTTP());
        mChkHTTP.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherWatchFaceConfigActivity.THAT.mConfig.setLogHTTP(AboutActivity.this, isChecked);
                WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
            }
        });

        mChkInterval.setChecked(WeatherWatchFaceConfigActivity.THAT.mConfig.getDebugIntervalDevisor() == 10);
        mChkInterval.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                WeatherWatchFaceConfigActivity.THAT.mConfig.setDebugIntervalDevisor(AboutActivity.this, isChecked ? 10 : 1);
                WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
            }
        });

        alreadyInitialize = true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void switchDebug(boolean value) {
        mDebugSwitch.setChecked(value);
        WeatherWatchFaceConfigActivity.THAT.mConfig.setDebug(this, value);
        if (value) mDebugLayout.setVisibility(View.VISIBLE);
        else mDebugLayout.setVisibility(View.GONE);
        WeatherWatchFaceConfigActivity.THAT.sendConfigUpdateMessage();
    }

}

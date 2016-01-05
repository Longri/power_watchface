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
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.wearable.companion.WatchFaceCompanion;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Wearable;
import de.longri.serializable.BitStore;
import de.longri.serializable.NotImplementedException;
import de.longri.watchface.loging.Log;
import roboguice.activity.RoboActivity;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;

import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_weather_watch_face_config)
public class WeatherWatchFaceConfigActivity extends RoboActivity {
// ------------------------------ FIELDS ------------------------------


    private List<Integer> themes = new ArrayList<>();
    private List<View> themeButtons = new ArrayList<>();
    Config mConfig;

    public static WeatherWatchFaceConfigActivity THAT;


    private void setPreferencesToUI() {
        if (mConfig.getUseCelsius()) {
            mScaleRadioGroup.check(R.id.celsiusRadioButton);
        } else {
            mScaleRadioGroup.check(R.id.fahrenheitRadioButton);
        }

        mTimeUnitSwitch.setChecked(mConfig.getTimeUnit());
        mTimeDigitalSwitch.setChecked(mConfig.getDigital());
        mIntervalSpinner.setSelection(mConfig.getIntervalSpinnerPos(), false);

        //onColorViewClick.onClick(themeButtons.get(mConfig.getTheme()));
        alreadyInitialize = true;
        mContainer.setVisibility(View.VISIBLE);
        mTimeUnitSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConfig.setTimeUnit(WeatherWatchFaceConfigActivity.this, isChecked);
                sendConfigUpdateMessage();
            }
        });

        mTimeDigitalSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConfig.setDigital(WeatherWatchFaceConfigActivity.this, isChecked);
                sendConfigUpdateMessage();
            }
        });

        mScaleRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mConfig.setUseCelsius(WeatherWatchFaceConfigActivity.this, checkedId != R.id.fahrenheitRadioButton);
                sendConfigUpdateMessage();
            }
        });

        mIntervalSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mConfig.setInterval(WeatherWatchFaceConfigActivity.this, (byte) position);
                sendConfigUpdateMessage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        //Time Zone Spinner
        ArrayAdapter<CharSequence> adapter =
                new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final WearTimeZone[] TZ = WearTimeZone.getAvailableTimeZones();
        final ArrayList<String> TZ1 = new ArrayList<String>();
        for (int i = 0; i < TZ.length; i++) {
            if (!(TZ1.contains(TZ[i].getID()))) {
                TZ1.add(TZ[i].getID());
            }
        }
        for (int i = 0; i < TZ1.size(); i++) {
            adapter.add(TZ1.get(i));
        }
        final Spinner TZone = (Spinner) findViewById(R.id.TimeZoneEntry);
        TZone.setAdapter(adapter);
        for (int i = 0; i < TZ1.size(); i++) {
            if (TZ[i].equals(mConfig.getSecondTimeZone())) {
                TZone.setSelection(i);
                break;
            }
        }


        TZone.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                mConfig.setSecondTimeZone(WeatherWatchFaceConfigActivity.this, TZ[position]);
                sendConfigUpdateMessage();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }


    private GoogleApiClient mGoogleApiClient;

    @InjectView(R.id.container)
    private ViewGroup mContainer;

    @InjectView(R.id.InfoTextView)
    private TextView mInfoLabel;

    @InjectView(R.id.scaleRadioGroup)
    private RadioGroup mScaleRadioGroup;

    @InjectView(R.id.intervalSpinner)
    private Spinner mIntervalSpinner;
    private String mPeerId;

    @InjectView(R.id.switch_time_unit)
    private Switch mTimeUnitSwitch;

    @InjectView(R.id.switch_digital)
    private Switch mTimeDigitalSwitch;

    @InjectView(R.id.VersionTextView)
    private TextView mVersionLabel;


    @InjectView(R.id.btn_refresh_button)
    private View mManualUpdateButton;

    @InjectView(R.id.infoLayout)
    private LinearLayout mInfoLine;

    private boolean alreadyInitialize;

// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Initialize Logs
        if (!Log.isInitial()) new Log(WeatherWatchFaceConfigActivity.this, false);

        THAT = this;

        mPeerId = getIntent().getStringExtra(WatchFaceCompanion.EXTRA_PEER_ID);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();


        mManualUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WeatherWatchFaceConfigActivity.this, WeatherService.class);
                intent.setAction(WeatherWatchFaceConfigActivity.class.getSimpleName());
                intent.putExtra("PeerId", mPeerId);
                intent.putExtra("Force", 1);
                startService(intent);
                Toast.makeText(WeatherWatchFaceConfigActivity.this, "Refresh Succeeded!", Toast.LENGTH_SHORT).show();
            }
        });

        int themeSize = this.getResources().getInteger(R.integer.theme_size);
        for (int i = 1; i <= themeSize; i++) {
            int id = this.getResources().getIdentifier("theme_" + i, "color", WeatherWatchFaceConfigActivity.class.getPackage().getName());
            themes.add(this.getResources().getColor(id));
        }

        //load config from preferences
        mConfig = new Config();


        try {
            mVersionLabel.setText("Version: " + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        mInfoLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(WeatherWatchFaceConfigActivity.this, AboutActivity.class);
                alreadyInitialize = false;
                startActivityForResult(i, 1);
            }
        });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if (!alreadyInitialize) {
            mConfig.loadFromPreferences(this);
            setPreferencesToUI();
            mConfig.setDebugIntervalDevisor(this, 1);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }


    void sendConfigUpdateMessage() {
        DataMap data = new DataMap();
        BitStore store = new BitStore();
        try {
            mConfig.serialize(store);
            data.putByteArray(Consts.KEY_CONFIG, store.getArray());
        } catch (NotImplementedException e) {
            return;
        }

        if (mPeerId != null && alreadyInitialize) {
            Log.d(Consts.TAG_PHONE, "Sending Config: \n" + mConfig.toString());
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, Consts.PATH_CONFIG, data.toByteArray())
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.d(Consts.TAG_PHONE, "Send Config Result: " + sendMessageResult.getStatus());
                        }
                    });
        }
    }

    public void sendGetLogsMessage() {

        if (mPeerId != null && alreadyInitialize) {
            Log.d(Consts.TAG_PHONE, "Sending Config: \n" + mConfig.toString());
            Wearable.MessageApi.sendMessage(mGoogleApiClient, mPeerId, Consts.KEY_LOGS, null)
                    .setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                            Log.d(Consts.TAG_PHONE, "Send Config Result: " + sendMessageResult.getStatus());
                        }
                    });
        }
    }
}

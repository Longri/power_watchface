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

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
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

@ContentView(R.layout.activity_weather_watch_face_config)
public class WeatherWatchFaceConfigActivity extends RoboActivity {
// ------------------------------ FIELDS ------------------------------


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

    @InjectView(R.id.imageViewTop)
    private ImageView imageViewTop;

    @InjectView(R.id.imageViewLeft)
    private ImageView imageViewLeft;

    @InjectView(R.id.imageViewBottom)
    private ImageView imageViewBottom;

    @InjectView(R.id.imageViewRight)
    private ImageView imageViewRight;

    @InjectView(R.id.brightnessSeekBar)
    private SeekBar brightnessSeekBar;

    @InjectView(R.id.brightnessTextView)
    private TextView brightnessTextView;

    @InjectView(R.id.layoutForecastUpdateIntervall)
    private View mLayoutForecastUpdateIntervall;

    @InjectView(R.id.selectIconButton)
    private View mSelectIconButton;

    @InjectView(R.id.layoutOpenWeatherApi)
    private View mLayoutOpenWeatherApi;

    private boolean alreadyInitialize;
    Config mConfig;

    public static WeatherWatchFaceConfigActivity THAT;


    private void setPreferencesToUI() {

        //disable settings views yet implemented on release
        if (Consts.RELEASE) {
            mSelectIconButton.setVisibility(View.GONE);
            mLayoutForecastUpdateIntervall.setVisibility(View.GONE);
            mLayoutOpenWeatherApi.setVisibility(View.GONE);
        }

        if (mConfig.getUseCelsius()) {
            mScaleRadioGroup.check(R.id.celsiusRadioButton);
        } else {
            mScaleRadioGroup.check(R.id.fahrenheitRadioButton);
        }

        mTimeUnitSwitch.setChecked(mConfig.getTimeUnit());
        mTimeDigitalSwitch.setChecked(mConfig.getDigital());
        mIntervalSpinner.setSelection(mConfig.getIntervalSpinnerPos(), false);

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


        //delete (override) tiny images to none
        Resources resources = this.getResources();
        setToImage(0, resources.getDrawable(R.drawable.tiny_back));
        setToImage(1, resources.getDrawable(R.drawable.tiny_back));
        setToImage(2, resources.getDrawable(R.drawable.tiny_back));
        setToImage(3, resources.getDrawable(R.drawable.tiny_back));

        //set images to tiny positions
        setToImage(mConfig.getPositionOf(de.longri.watchface.View.Date), resources.getDrawable(R.drawable.tiny_date));
        setToImage(mConfig.getPositionOf(de.longri.watchface.View.SecondTime), resources.getDrawable(R.drawable.tiny_clock));
        setToImage(mConfig.getPositionOf(de.longri.watchface.View.Weather), resources.getDrawable(R.drawable.tiny_weather));

        imageViewTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                showTinyPositionSelectDialog(getPositionIndex(0), 0);
            }
        });

        imageViewRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                showTinyPositionSelectDialog(getPositionIndex(1), 1);
            }
        });

        imageViewBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                showTinyPositionSelectDialog(getPositionIndex(2), 2);
            }
        });

        imageViewLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                showTinyPositionSelectDialog(getPositionIndex(3), 3);
            }
        });


        // set brightness values
        int brightness = mConfig.getBrightness();
        brightnessSeekBar.setMax(100);
        brightnessSeekBar.setProgress(brightness);

        brightnessTextView.setText(R.string.brightness);
        brightnessTextView.append("  " + brightness + "%");

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int value;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                brightnessTextView.setText(R.string.brightness);
                brightnessTextView.append("  " + value + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // set to config
                mConfig.setBrightness(WeatherWatchFaceConfigActivity.this, value);
                brightnessTextView.setText(R.string.brightness);
                brightnessTextView.append("  " + value + "%");
                sendConfigUpdateMessage();
            }
        });
    }


    private int getPositionIndex(int index) {
        if (index == mConfig.getPositionOf(de.longri.watchface.View.Date)) return 2;
        if (index == mConfig.getPositionOf(de.longri.watchface.View.SecondTime)) return 3;
        if (index == mConfig.getPositionOf(de.longri.watchface.View.Weather)) return 4;
        if (index == mConfig.getPositionOf(de.longri.watchface.View.Logo)) return 1;
        return 0;
    }

    private void showTinyPositionSelectDialog(int selected, final int editPosition) {
        alreadyInitialize = false;

        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.layout_select_tiny_dialog);

        dialog.setTitle(getString(R.string.select_view));

        dialog.show();

        // set the custom dialog components

        RadioButton rb0 = (RadioButton) dialog.findViewById(R.id.radioButton0);
        RadioButton rb1 = (RadioButton) dialog.findViewById(R.id.radioButton1);
        RadioButton rb2 = (RadioButton) dialog.findViewById(R.id.radioButton2);
        RadioButton rb3 = (RadioButton) dialog.findViewById(R.id.radioButton3);
        RadioButton rb4 = (RadioButton) dialog.findViewById(R.id.radioButton4);
        switch (selected) {
            case 0:
                rb0.setChecked(true);
                break;
            case 1:
                rb1.setChecked(true);
                break;
            case 2:
                rb2.setChecked(true);
                break;
            case 3:
                rb3.setChecked(true);
                break;
            case 4:
                rb4.setChecked(true);
                break;
        }

        //Button listener
        Button btnCancel = (Button) dialog.findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog without any changes
                dialog.dismiss();
            }
        });

        Button btnApply = (Button) dialog.findViewById(R.id.btn_apply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //close dialog and save changes
                RadioGroup group = (RadioGroup) dialog.findViewById(R.id.radioView);

                int selectionId = group.getCheckedRadioButtonId();
                de.longri.watchface.View view = de.longri.watchface.View.None;

                switch (selectionId) {
                    case R.id.radioButton0:
                        view = de.longri.watchface.View.None;
                        break;
                    case R.id.radioButton1:
                        view = de.longri.watchface.View.Logo;
                        break;
                    case R.id.radioButton2:
                        view = de.longri.watchface.View.Date;
                        break;
                    case R.id.radioButton3:
                        view = de.longri.watchface.View.SecondTime;
                        break;
                    case R.id.radioButton4:
                        view = de.longri.watchface.View.Weather;
                        break;
                }


                switch (editPosition) {
                    case 0:
                        mConfig.setViewPositionTop(WeatherWatchFaceConfigActivity.this, view);
                        break;
                    case 1:
                        mConfig.setViewPositionRight(WeatherWatchFaceConfigActivity.this, view);
                        break;
                    case 2:
                        mConfig.setViewPositionBottom(WeatherWatchFaceConfigActivity.this, view);
                        break;
                    case 3:
                        mConfig.setViewPositionLeft(WeatherWatchFaceConfigActivity.this, view);
                        break;
                }

                // reload ConfigActivity
                alreadyInitialize = false;
                WeatherWatchFaceConfigActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.dismiss();
                    }
                });
            }
        });
    }

    private void setToImage(int pos, Drawable drawable) {
        switch (pos) {
            case 0:
                imageViewTop.setImageDrawable((drawable));
                break;
            case 1:
                imageViewRight.setImageDrawable((drawable));
                break;
            case 2:
                imageViewBottom.setImageDrawable((drawable));
                break;
            case 3:
                imageViewLeft.setImageDrawable((drawable));
                break;
        }
    }


// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //Initialize Logs
        new Log(WeatherWatchFaceConfigActivity.this, false);

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

        if (!hasFocus) return;

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

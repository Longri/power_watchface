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

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.wearable.companion.WatchFaceCompanion;
import android.view.Menu;
import android.view.MenuItem;
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

    private TotalisatorView totalisatorViewTop;

    private TotalisatorView totalisatorViewLeft;

    private TotalisatorView totalisatorViewBottom;

    private TotalisatorView totalisatorViewRight;

    @InjectView(R.id.brightnessSeekBar)
    private SeekBar brightnessSeekBar;

    @InjectView(R.id.brightnessTextView)
    private TextView brightnessTextView;

    @InjectView(R.id.layoutForecastUpdateIntervall)
    private View mLayoutForecastUpdateIntervall;

    @InjectView(R.id.layoutOpenWeatherApi)
    private View mLayoutOpenWeatherApi;

    @InjectView(R.id.switchScaleNormal)
    private Switch mSwitchScaleNormal;

    @InjectView(R.id.switchScaleAmbient)
    private Switch mSwitchScaleAmbient;

    @InjectView(R.id.switchScaleValueNormal)
    private Switch mSwitchScaleValueNormal;

    @InjectView(R.id.switchScaleValueAmbient)
    private Switch mSwitchScaleValueAmbient;

    @InjectView(R.id.seekBarTotalizatorMargin)
    private SeekBar seekBarTotalizatorMargin;

    @InjectView(R.id.tvTotalizatorMargin)
    private TextView tvTotalizatorMargin;

    @InjectView(R.id.seekBarTotalizatorZoom)
    private SeekBar seekBarTotalizatorZoom;

    @InjectView(R.id.tvTotalizatorZoom)
    private TextView tvTotalizatorZoom;


    private boolean alreadyInitialize;
    Config mConfig;

    public static WeatherWatchFaceConfigActivity THAT;


    private void setPreferencesToUI() {

        //disable settings views yet implemented on release
        if (Consts.RELEASE) {
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
        setToImage(mConfig.getPositionOf(WatchFaceView.Date), resources.getDrawable(R.drawable.tiny_date));
        setToImage(mConfig.getPositionOf(WatchFaceView.SecondTime), resources.getDrawable(R.drawable.tiny_clock));
        setToImage(mConfig.getPositionOf(WatchFaceView.Weather), resources.getDrawable(R.drawable.tiny_weather));
        setToImage(mConfig.getPositionOf(WatchFaceView.Logo), resources.getDrawable(R.drawable.logo));


        final TotalisatorOffset to = mConfig.getTotalisatorOffset();

        to.setChangedListener(new TotalisatorOffset.IChanged() {
            @Override
            public void isChanged(TotalisatorOffsetPos pos) {
                TotalisatorValueChanged(pos, to);
            }
        });

        totalisatorViewTop.setValue(to.getTop(), false);
        totalisatorViewTop.setOnButtonClickListener(to.Top_x_plus, to.Top_x_minus, to.Top_y_plus, to.Top_y_minus);
        totalisatorViewTop.setOnImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                showTinyPositionSelectDialog(getPositionIndex(0), 0);
            }
        });
        totalisatorViewTop.setOnImageLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                to.getTop().Reset();
                                TotalisatorValueChanged(TotalisatorOffsetPos.Top, to);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherWatchFaceConfigActivity.this);
                builder.setMessage(WeatherWatchFaceConfigActivity.this.getString(R.string.reset_totalisator_offsett))
                        .setPositiveButton(WeatherWatchFaceConfigActivity.this.getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(WeatherWatchFaceConfigActivity.this.getString(R.string.no), dialogClickListener).show();
                return true;
            }
        });

        totalisatorViewRight.setValue(to.getRight(), false);
        totalisatorViewRight.setOnButtonClickListener(to.Right_x_plus, to.Right_x_minus, to.Right_y_plus, to.Right_y_minus);
        totalisatorViewRight.setOnImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                showTinyPositionSelectDialog(getPositionIndex(1), 1);
            }
        });
        totalisatorViewRight.setOnImageLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                to.getRight().Reset();
                                TotalisatorValueChanged(TotalisatorOffsetPos.Right, to);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherWatchFaceConfigActivity.this);
                builder.setMessage(WeatherWatchFaceConfigActivity.this.getString(R.string.reset_totalisator_offsett))
                        .setPositiveButton(WeatherWatchFaceConfigActivity.this.getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(WeatherWatchFaceConfigActivity.this.getString(R.string.no), dialogClickListener).show();
                return true;
            }
        });

        totalisatorViewBottom.setValue(to.getBottom(), false);
        totalisatorViewBottom.setOnButtonClickListener(to.Bottom_x_plus, to.Bottom_x_minus, to.Bottom_y_plus, to.Bottom_y_minus);
        totalisatorViewBottom.setOnImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                showTinyPositionSelectDialog(getPositionIndex(2), 2);
            }
        });
        totalisatorViewBottom.setOnImageLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                to.getBottom().Reset();
                                TotalisatorValueChanged(TotalisatorOffsetPos.Bottom, to);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherWatchFaceConfigActivity.this);
                builder.setMessage(WeatherWatchFaceConfigActivity.this.getString(R.string.reset_totalisator_offsett))
                        .setPositiveButton(WeatherWatchFaceConfigActivity.this.getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(WeatherWatchFaceConfigActivity.this.getString(R.string.no), dialogClickListener).show();
                return true;
            }
        });

        totalisatorViewLeft.setValue(to.getLeft(), false);
        totalisatorViewLeft.setOnButtonClickListener(to.Left_x_plus, to.Left_x_minus, to.Left_y_plus, to.Left_y_minus);
        totalisatorViewLeft.setOnImageClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //show selection dialog
                if (v instanceof ImageView) showTinyPositionSelectDialog(getPositionIndex(3), 3);
            }
        });
        totalisatorViewLeft.setOnImageLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                to.getLeft().Reset();
                                TotalisatorValueChanged(TotalisatorOffsetPos.Left, to);
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(WeatherWatchFaceConfigActivity.this);
                builder.setMessage(WeatherWatchFaceConfigActivity.this.getString(R.string.reset_totalisator_offsett))
                        .setPositiveButton(WeatherWatchFaceConfigActivity.this.getString(R.string.yes), dialogClickListener)
                        .setNegativeButton(WeatherWatchFaceConfigActivity.this.getString(R.string.no), dialogClickListener).show();
                return true;
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


        // set totalizator zoom values
        int tvTotalizatorZoomValue = mConfig.getTotalisatorZoom();
        seekBarTotalizatorZoom.setMax(100);
        seekBarTotalizatorZoom.setProgress(tvTotalizatorZoomValue);

        tvTotalizatorZoom.setText(R.string.totalizator_zoom);
        tvTotalizatorZoom.append("  " + (tvTotalizatorZoomValue - 50) + "%");

        seekBarTotalizatorZoom.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int value;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                tvTotalizatorZoom.setText(R.string.totalizator_zoom);
                tvTotalizatorZoom.append("  " + (value - 50) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // set to config
                mConfig.setTotalisatorZoom(WeatherWatchFaceConfigActivity.this, value);
                tvTotalizatorZoom.setText(R.string.totalizator_zoom);
                tvTotalizatorZoom.append("  " + (value - 50) + "%");
                sendConfigUpdateMessage();
            }
        });


        // set totalizator zoom values
        int tvTotalizatorMarginValue = mConfig.getTotalisatorMargin();
        seekBarTotalizatorMargin.setMax(100);
        seekBarTotalizatorMargin.setProgress(tvTotalizatorMarginValue);

        tvTotalizatorMargin.setText(R.string.totalizator_margin);
        tvTotalizatorMargin.append("  " + (tvTotalizatorMarginValue - 50) + "%");

        seekBarTotalizatorMargin.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            int value;

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                value = progress;
                tvTotalizatorMargin.setText(R.string.totalizator_margin);
                tvTotalizatorMargin.append("  " + (value - 50) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // set to config
                mConfig.setTotalisatorMargin(WeatherWatchFaceConfigActivity.this, value);
                tvTotalizatorMargin.setText(R.string.totalizator_margin);
                tvTotalizatorMargin.append("  " + (value - 50) + "%");
                sendConfigUpdateMessage();
            }
        });


        // set Scale drawing settings
        mSwitchScaleNormal.setChecked(mConfig.getShowScale());
        mSwitchScaleNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConfig.setShowScale(WeatherWatchFaceConfigActivity.this, isChecked);
                sendConfigUpdateMessage();
            }
        });

        mSwitchScaleAmbient.setChecked(mConfig.getShowScaleAmbient());
        mSwitchScaleAmbient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConfig.setShowScaleAmbient(WeatherWatchFaceConfigActivity.this, isChecked);
                sendConfigUpdateMessage();
            }
        });

        mSwitchScaleValueNormal.setChecked(mConfig.getShowScaleValue());
        mSwitchScaleValueNormal.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConfig.setShowScaleValue(WeatherWatchFaceConfigActivity.this, isChecked);
                sendConfigUpdateMessage();
            }
        });

        mSwitchScaleValueAmbient.setChecked(mConfig.getShowScaleValueAmbient());
        mSwitchScaleValueAmbient.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mConfig.setShowScaleValueAmbient(WeatherWatchFaceConfigActivity.this, isChecked);
                sendConfigUpdateMessage();
            }
        });


    }

    private void TotalisatorValueChanged(TotalisatorOffsetPos pos, TotalisatorOffset to) {
        mConfig.setTotalisatorOffset(WeatherWatchFaceConfigActivity.this, to);

        switch (pos) {
            case Top:
                totalisatorViewTop.setValue(to.getTop(), true);
                break;
            case Right:
                totalisatorViewRight.setValue(to.getRight(), true);
                break;
            case Bottom:
                totalisatorViewBottom.setValue(to.getBottom(), true);
                break;
            case Left:
                totalisatorViewLeft.setValue(to.getLeft(), true);
        }


        sendConfigUpdateMessage();
    }


    private int getPositionIndex(int index) {
        if (index == mConfig.getPositionOf(WatchFaceView.Date)) return 2;
        if (index == mConfig.getPositionOf(WatchFaceView.SecondTime)) return 3;
        if (index == mConfig.getPositionOf(WatchFaceView.Weather)) return 4;
        if (index == mConfig.getPositionOf(WatchFaceView.Logo)) return 1;
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
                WatchFaceView watchFaceView = WatchFaceView.None;

                switch (selectionId) {
                    case R.id.radioButton0:
                        watchFaceView = WatchFaceView.None;
                        break;
                    case R.id.radioButton1:
                        watchFaceView = WatchFaceView.Logo;
                        break;
                    case R.id.radioButton2:
                        watchFaceView = WatchFaceView.Date;
                        break;
                    case R.id.radioButton3:
                        watchFaceView = WatchFaceView.SecondTime;
                        break;
                    case R.id.radioButton4:
                        watchFaceView = WatchFaceView.Weather;
                        break;
                }


                switch (editPosition) {
                    case 0:
                        mConfig.setViewPositionTop(WeatherWatchFaceConfigActivity.this, watchFaceView);
                        break;
                    case 1:
                        mConfig.setViewPositionRight(WeatherWatchFaceConfigActivity.this, watchFaceView);
                        break;
                    case 2:
                        mConfig.setViewPositionBottom(WeatherWatchFaceConfigActivity.this, watchFaceView);
                        break;
                    case 3:
                        mConfig.setViewPositionLeft(WeatherWatchFaceConfigActivity.this, watchFaceView);
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
                totalisatorViewTop.setImageDrawable((drawable));
                break;
            case 1:
                totalisatorViewRight.setImageDrawable((drawable));
                break;
            case 2:
                totalisatorViewBottom.setImageDrawable((drawable));
                break;
            case 3:
                totalisatorViewLeft.setImageDrawable((drawable));
                break;
        }
    }


// -------------------------- OTHER METHODS --------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.getActionBar().setDisplayHomeAsUpEnabled(true);

        //Initialize Logs
        new Log(WeatherWatchFaceConfigActivity.this, false);
        THAT = this;


        totalisatorViewTop = (TotalisatorView) findViewById(R.id.totalisatorViewTop);
        totalisatorViewLeft = (TotalisatorView) findViewById(R.id.totalisatorViewLeft);
        totalisatorViewBottom = (TotalisatorView) findViewById(R.id.totalisatorViewBottom);
        totalisatorViewRight = (TotalisatorView) findViewById(R.id.totalisatorViewRight);


        totalisatorViewTop.setActivity(WeatherWatchFaceConfigActivity.this);
        totalisatorViewRight.setActivity(WeatherWatchFaceConfigActivity.this);
        totalisatorViewBottom.setActivity(WeatherWatchFaceConfigActivity.this);
        totalisatorViewLeft.setActivity(WeatherWatchFaceConfigActivity.this);


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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

package com.example.koresuniku.kisosvet;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private final static String PSU_OFF = "PSU OFF";
    private final static String PSU_ON = "PSU ON";
    private final static String LED_ALL_ON = "LED ALL ON";
    private final static String LED_ALL_OFF = "LED ALL OFF";
    public static final String GET_TEMP = "gettemp";
    public static final String GET_ADC_DATA_V = "getadcdata v";


    ImageView led1;
    ImageView led2;
    ImageView led3;
    ImageView led4;
    SeekBar brightnessSeekbar;
    SeekBar temperatureSeekbar;
    Button psu;
    Button ledAll;
    static TextView temperatureTextView;
    static TextView voltageTextView;
    TextView colorTemperatureTextView;

    static MainActivity mActivity;

    boolean led1Toogle;
    boolean led2Toogle;
    boolean led3Toogle;
    boolean led4Toogle;

    int oldProgress = 100;

    static boolean continueReceivingTempAndVoltageData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(LOG_TAG, "onCreate()");
        setContentView(R.layout.redesign);
        mActivity = this;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        led1 = (ImageView) findViewById(R.id.led1_nw);
        led2 = (ImageView) findViewById(R.id.led2_ww);
        led3 = (ImageView) findViewById(R.id.led3_ww);
        led4 = (ImageView) findViewById(R.id.led4_nw);
        brightnessSeekbar = (SeekBar) findViewById(R.id.brightness_seekbar);
        temperatureSeekbar = (SeekBar) findViewById(R.id.color_temperature_seekbar);
        psu = (Button) findViewById(R.id.psu_button);
        ledAll = (Button) findViewById(R.id.led_all_button);
        temperatureTextView = (TextView) findViewById(R.id.temperature_textview);
        voltageTextView = (TextView) findViewById(R.id.voltage_textview);
        colorTemperatureTextView = (TextView) findViewById(R.id.color_temperature_textview);

        colorTemperatureTextView.setText(
                getApplicationContext().getResources().getString(R.string.color_temperature_text));

        led1Toogle = false;
        led2Toogle = false;
        led3Toogle = false;
        led4Toogle = false;

        led1.setContentDescription("1");
        led2.setContentDescription("2");
        led3.setContentDescription("3");
        led4.setContentDescription("4");

        led1.setOnClickListener(ledOnClickListener);
        led2.setOnClickListener(ledOnClickListener);
        led3.setOnClickListener(ledOnClickListener);
        led4.setOnClickListener(ledOnClickListener);

        brightnessSeekbar.setProgress(0);
        brightnessSeekbar.setKeyProgressIncrement(1);
        brightnessSeekbar.setMax(200);
        brightnessSeekbar.setOnSeekBarChangeListener(seekBar1ChangeListener);

        temperatureSeekbar.setProgress(100);
        temperatureSeekbar.setKeyProgressIncrement(1);
        temperatureSeekbar.setMax(200);
        temperatureSeekbar.setOnSeekBarChangeListener(temperatureChangeListener);

        psu.setText(PSU_OFF);
        psu.setOnClickListener(psuOnClickListener);

        ledAll.setText(LED_ALL_OFF);
        ledAll.setOnClickListener(ledAllClickListener);

        temperatureTextView.setText(Constants.TEXT_TEMPERATURE);
        voltageTextView.setText(Constants.TEXT_VOLTAGE);

        enableCheckingUpdates();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.item_exit: {
                finish();
                System.exit(0);
            }
        }
        return true;
    }

    private static void enableCheckingUpdates() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                while (true) {
                    if (!continueReceivingTempAndVoltageData) continue;
                    ArrayList<String> receivedTemperatureData = UDPClient.clienNeedToReceiveData(GET_TEMP);
                    ArrayList<String> receivedVoltageData = UDPClient.clienNeedToReceiveData(GET_ADC_DATA_V);
                    //Log.i(LOG_TAG, "data received");
                    if (receivedTemperatureData == null) continue;
                    if (receivedVoltageData == null) continue;

                    final String temperatureData = Parcer.parseString(receivedTemperatureData, Constants.CODE_TEMPERATURE);
                    final String voltageData = Parcer.parseString(receivedVoltageData, Constants.CODE_VOLTAGE);

                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            temperatureTextView = (TextView) mActivity.findViewById(R.id.temperature_textview);
                            voltageTextView = (TextView) mActivity.findViewById(R.id.voltage_textview);
                            temperatureTextView.setText(Constants.TEXT_TEMPERATURE + temperatureData);
                            voltageTextView.setText(Constants.TEXT_VOLTAGE + voltageData);
                        }
                    });
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.execute();
    }

    private View.OnClickListener ledAllClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            continueReceivingTempAndVoltageData = false;

            if (ledAll.getText().equals(LED_ALL_OFF)) {
                ledAll.setText(LED_ALL_ON);
                UDPClient.client(LED_ALL_ON);
            } else {
                UDPClient.client(LED_ALL_OFF);
                ledAll.setText(LED_ALL_OFF);
            }
            continueReceivingTempAndVoltageData = true;
        }
    };

    private SeekBar.OnSeekBarChangeListener temperatureChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            StringBuilder sb1 = new StringBuilder("LED 1 NC ");
            StringBuilder sb2 = new StringBuilder("LED 2 NC ");
            StringBuilder sb3 = new StringBuilder("LED 3 NC ");
            StringBuilder sb4 = new StringBuilder("LED 4 NC ");

            int pwmValueHot = 45000 + 100 * progress;
            int pwmValueCold = 65000 - 100 * progress;

            Log.i(LOG_TAG, "pwmValueHot " + pwmValueHot);
            Log.i(LOG_TAG, "pwmValueCold " + pwmValueCold);

//            sb1.append(pwmValueHot);
//            sb2.append(pwmValueCold);
//            sb3.append(pwmValueCold);
//            sb4.append(pwmValueHot);

            if (progress < oldProgress) {
                sb1.append("-2");
                sb2.append("+2");
                sb3.append("+2");
                sb4.append("-2");
            } else {
                sb1.append("+2");
                sb2.append("-2");
                sb3.append("-2");
                sb4.append("+2");
            }

            UDPClient.client(sb1.toString());
            UDPClient.client(sb2.toString());
            UDPClient.client(sb3.toString());
            UDPClient.client(sb4.toString());

            oldProgress = progress;
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            continueReceivingTempAndVoltageData = false;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            continueReceivingTempAndVoltageData = true;
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBar1ChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        //private int OldProgress = progress;
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

           // if (oldProgress <= progress) {
                StringBuilder sb = new StringBuilder("LED ALL NC ");
                int pwmValue = 1000 + (64000 * progress) / 200;//120 * progress;
                sb.append(pwmValue);
            Log.i(LOG_TAG, "progress " + sb.toString());

            UDPClient.client(sb.toString());
                oldProgress = progress;

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            continueReceivingTempAndVoltageData = false;
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            continueReceivingTempAndVoltageData = true;
        }
    };

    View.OnClickListener psuOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            continueReceivingTempAndVoltageData = false;
            if (psu.getText().equals(PSU_OFF)) {
                psu.setText(PSU_ON);
                UDPClient.client(PSU_ON);
            } else {
                UDPClient.client(PSU_OFF);
                psu.setText(PSU_OFF);
            }
            continueReceivingTempAndVoltageData = true;
        }
    };

    View.OnClickListener ledOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            continueReceivingTempAndVoltageData = false;

            int id = Integer.parseInt(String.valueOf(v.getContentDescription()));
            Log.i(LOG_TAG, "id " + id);
            switch (id) {
                case 1: {
                    try {
                        if (!led1Toogle) {
                            Log.i(LOG_TAG, "onClick() false");
                            UDPClient.client("LED 1 1");
                            led1Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            UDPClient.client("LED 1 0");
                            led1Toogle = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 2: {
                    try {
                        if (!led2Toogle) {
                            Log.i(LOG_TAG, "onClick() false");
                            UDPClient.client("LED 2 1");
                            led2Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            UDPClient.client("LED 2 0");
                            led2Toogle = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 3: {
                    try {
                        if (!led3Toogle) {
                            Log.i(LOG_TAG, "onClick() false");
                            UDPClient.client("LED 3 1");
                            led3Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            UDPClient.client("LED 3 0");
                            led3Toogle = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
                case 4: {
                    try {
                        if (!led4Toogle) {
                            Log.i(LOG_TAG, "onClick() false");
                            UDPClient.client("LED 4 1");
                            led4Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            UDPClient.client("LED 4 0");
                            led4Toogle = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
            continueReceivingTempAndVoltageData = true;
        }
    };

    private void wait500msAndContinueReceivingData() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                continueReceivingTempAndVoltageData = true;
                return null;
            }
        }.execute();
    }
}

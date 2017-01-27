package com.example.koresuniku.kisosvet;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private final static String LOG_TAG = MainActivity.class.getSimpleName();

    private final static String PSU_OFF = "PSU OFF";
    private final static String PSU_ON = "PSU ON";
    private final static String LED_ALL_ON = "LED ALL ON";
    private final static String LED_ALL_OFF = "LED ALL OFF";

    ImageView led1;
    ImageView led2;
    ImageView led3;
    ImageView led4;
    SeekBar seekBar1;
    Button psu;
    Button ledAll;

    boolean led1Toogle;
    boolean led2Toogle;
    boolean led3Toogle;
    boolean led4Toogle;

    int oldProgress = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        led1 = (ImageView) findViewById(R.id.led1_nw);
        led2 = (ImageView) findViewById(R.id.led2_ww);
        led3 = (ImageView) findViewById(R.id.led3_ww);
        led4 = (ImageView) findViewById(R.id.led4_nw);
        seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
        psu = (Button) findViewById(R.id.psu);
        ledAll = (Button) findViewById(R.id.led_all);

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

        seekBar1.setProgress(0);
        seekBar1.setKeyProgressIncrement(1);
        seekBar1.setMax(200);
        seekBar1.setOnSeekBarChangeListener(seekBar1ChangeListener);

        psu.setText(PSU_OFF);
        psu.setOnClickListener(psuOnClickListener);

        ledAll.setText(LED_ALL_OFF);
        ledAll.setOnClickListener(ledAllClickListener);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    private View.OnClickListener ledAllClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (ledAll.getText().equals(LED_ALL_OFF)) {
                ledAll.setText(LED_ALL_ON);
                new UDPTask().execute(LED_ALL_ON);
            } else {
                new UDPTask().execute(LED_ALL_OFF);
                ledAll.setText(LED_ALL_OFF);
            }
        }
    };

    private SeekBar.OnSeekBarChangeListener seekBar1ChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        //private int OldProgress = progress;
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

           // if (oldProgress <= progress) {
                StringBuilder sb = new StringBuilder("LED ALL ON ");
                int pwmValue = 45000 + 100 * progress;
                sb.append(pwmValue);
                new UDPTask().execute(sb.toString());
                oldProgress = progress;
                Log.i(LOG_TAG, "progress " + progress);

//            }
//            if (oldProgress >= progress) {
//                Log.i(LOG_TAG, "progress " + progress);
//
//                StringBuilder sb = new StringBuilder("LED ALL ON -1");
//                //int pwmValue = 45000 + 100 * progress;
//                //sb.append(pwmValue);
//                new UDPTask().execute(sb.toString());
//                oldProgress = progress;
//            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    View.OnClickListener psuOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (psu.getText().equals(PSU_OFF)) {
                psu.setText(PSU_ON);
                new UDPTask().execute(PSU_ON);
            } else {
                new UDPTask().execute(PSU_OFF);
                psu.setText(PSU_OFF);
            }
        }
    };

    View.OnClickListener ledOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            int id = Integer.parseInt(String.valueOf(v.getContentDescription()));
            Log.i(LOG_TAG, "id " + id);
            switch (id) {
                case 1: {
                    try {
                        if (!led1Toogle) {
                            Log.i(LOG_TAG, "onClick() false");
                            new UDPTask().execute("LED 1 1");
                            led1Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            new UDPTask().execute("LED 1 0");
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
                            new UDPTask().execute("LED 2 1");
                            led2Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            new UDPTask().execute("LED 2 0");
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
                            new UDPTask().execute("LED 3 1");
                            led3Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            new UDPTask().execute("LED 3 0");
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
                            new UDPTask().execute("LED 4 1");
                            led4Toogle = true;
                        } else {
                            Log.i(LOG_TAG, "onClick() true");
                            new UDPTask().execute("LED 4 0");
                            led4Toogle = false;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }

        }
    };
}

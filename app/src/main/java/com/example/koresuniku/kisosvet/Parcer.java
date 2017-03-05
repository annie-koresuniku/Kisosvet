package com.example.koresuniku.kisosvet;


import android.util.Log;

import java.util.ArrayList;

public class Parcer {
    private static final String LOG_TAG = Parcer.class.getSimpleName();


    public static String parseString(ArrayList<String> input, int whatToReturn) {
        if (input == null) return null;

        switch (whatToReturn) {
            case Constants.CODE_TEMPERATURE: {
                String needToParse = null;
                for (String s : input) {
                    if (s.contains(Constants.TEMPERATURE_STRING_BEGINNING)) {
                        needToParse = s;
                        break;
                    }
                }
                if (needToParse == null) return null;
                String withoutBeginning = needToParse.replace(Constants.TEMPERATURE_STRING_BEGINNING, "");
                String rawTemperature = withoutBeginning.substring(0, 3);
                //Log.i(LOG_TAG, "gotten temperature " + rawTemperature);
                StringBuilder sb = new StringBuilder(rawTemperature.substring(0, 2));
                sb.append(",");
                sb.append(rawTemperature.substring(2, 3));
                sb.append("° C");
                return sb.toString();
            }
            case Constants.CODE_VOLTAGE: {
                String needToParse = null;
                for (String s : input) {
                    if (s.contains(Constants.VOLTAGE_STRING_BEGINNING)) {
                        needToParse = s;
                        break;
                    }
                }
                if (needToParse == null) return null;
                String withoutBeginning = needToParse.replace(Constants.VOLTAGE_STRING_BEGINNING, "");
                String rawVoltage = withoutBeginning.substring(0, 4);
                //Log.i(LOG_TAG, "gotten temperature " + rawTemperature);
                StringBuilder sb = new StringBuilder(rawVoltage.substring(0, 2));
                sb.append(",");
                sb.append(rawVoltage.substring(2, 4));
                sb.append(" V");
                return sb.toString();
            }
            default: return null;
        }
    }
}

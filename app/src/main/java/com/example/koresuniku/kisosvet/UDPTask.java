package com.example.koresuniku.kisosvet;


import android.os.AsyncTask;

public class UDPTask extends AsyncTask<String, Void, Void> {
    @Override
    protected Void doInBackground(String... str) {
        UDPClient.client(str[0]);
        return null;
    }
}

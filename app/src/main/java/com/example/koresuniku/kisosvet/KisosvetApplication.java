package com.example.koresuniku.kisosvet;

import android.app.Application;

public class KisosvetApplication extends Application {

    public static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }
}

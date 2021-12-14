package com.moveo.notes;

import android.app.Application;
import android.content.SharedPreferences;
import android.location.LocationListener;

import io.paperdb.Paper;

public class NotesApp extends Application {
    Info info;
    GPSScanner gps;


    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        info = new Info(this);
        gps = new GPSScanner(this);

//        info.sp.edit().clear().commit();
    }
}

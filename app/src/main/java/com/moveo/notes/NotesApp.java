package com.moveo.notes;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationListener;

import androidx.core.content.ContextCompat;

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == 1) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                gps.locationManager.requestSingleUpdate(gps.criteria, gps.locationListener, gps.looper);
            }
        }
    }
}

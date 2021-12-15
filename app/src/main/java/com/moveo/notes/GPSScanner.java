package com.moveo.notes;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;


import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GPSScanner {
    LocationManager locationManager;
    LocationListener locationListener;
    NotesApp app;
    final Looper looper = null;
    public Criteria criteria;
    Double latitude = 0.0, longitude = 0.0;


    GPSScanner(NotesApp app) {
        this.app = app;
        initGPS();
    }

    public void initGPS() {
        locationManager = (LocationManager)
                app.getSystemService(Context.LOCATION_SERVICE);
        locationListener = location -> {
            Geocoder geocoder = new Geocoder(app, Locale.getDefault());
            try {
                List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

                if (listAddresses != null && listAddresses.size() > 0) {
                    latitude = listAddresses.get(0).getLatitude();
                    longitude = listAddresses.get(0).getLongitude();
                    app.info.getLocationUpdate().setValue(app.info.getLocationUpdate().getValue());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        };

        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        criteria.setHorizontalAccuracy(Criteria.ACCURACY_HIGH);
        criteria.setVerticalAccuracy(Criteria.ACCURACY_HIGH);


    }
    public void search(FragmentActivity activity) {
        if (ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            if (ContextCompat.checkSelfPermission(app, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestSingleUpdate(criteria, locationListener, looper);
            }
        } else {
            locationManager.requestSingleUpdate(criteria, locationListener, looper);
        }
    }


    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}


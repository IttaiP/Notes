package com.moveo.notes;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;


public class MainScreen extends ActivityAncestor implements OnMapReadyCallback {
    boolean MAP = true;
    boolean LIST = false;
    boolean lastPressed = LIST;
    boolean onEmpty = false;
    Button logOut, newNote;
    ListFrag listFrag;
    SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);

        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            FirebaseAuth.getInstance().signInAnonymously();
        }
        animateText();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        logOut = findViewById(R.id.logout);
        newNote = findViewById(R.id.new_note);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        app.gps.search(this);

        listFrag = new ListFrag();

        // map fragment
        GoogleMapOptions options = new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                .compassEnabled(false)
                .rotateGesturesEnabled(false)
                .tiltGesturesEnabled(false)
                .camera(new CameraPosition.Builder()
                        .target(new LatLng(app.gps.getLatitude(), app.gps.getLongitude()))
                        .zoom(2)
                        .tilt(40)
                        .build());
        mapFragment = SupportMapFragment.newInstance(options);
        mapFragment.getMapAsync(this);


        if (!app.info.noteList.isEmpty()) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFrag()).commit();
        } else {
            onEmpty = true;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoNotesFragment()).commit();
        }
        centerTitle();

        final Observer<Integer> lengthObserver = newUpdatedListLength -> {
            if (newUpdatedListLength > 0) {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, listFrag).commit();
            } else {
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoNotesFragment()).commit();
            }
        };

        app.info.getUpdatedList().observe(this, lengthObserver);
        final Observer<Boolean> locationObserver = newUpdatedLocation -> {
            options.mapType(GoogleMap.MAP_TYPE_SATELLITE)
                    .compassEnabled(false)
                    .rotateGesturesEnabled(false)
                    .tiltGesturesEnabled(false)
                    .camera(new CameraPosition.Builder()
                            .target(new LatLng(app.gps.getLatitude(), app.gps.getLongitude()))
                            .zoom(9)
                            .tilt(40)
                            .build());
            mapFragment = SupportMapFragment.newInstance(options);
            mapFragment.getMapAsync(this);
        };
        app.info.getLocationUpdate().observe(this, locationObserver);


        Intent logoutIntent = new Intent(this, Login.class);
        logOut.setOnClickListener(view -> {
            app.info.LogOut();
            app.info = new Info(getApplicationContext());
            startActivity(logoutIntent);
            finish();
        });

        Intent newNoteIntent = new Intent(this, NewNote.class);
        newNote.setOnClickListener(view -> {
            startActivity(newNoteIntent);
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.setOnMarkerClickListener(marker -> {
            Note note = (Note) marker.getTag();
            Intent intent = new Intent(this, NewNote.class);
            intent.putExtra("id", note.id);
            intent.putExtra("index", app.info.noteList.indexOf(note));
            startActivity(intent);
            return false;
        });
        for (Note note : app.info.noteList) {
            Marker newMarker = googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(note.getLatitude(), note.getLongitude()))
                    .title(note.title));
            newMarker.setTag(note);
        }

    }


    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if (app.info.noteList.isEmpty()) return true;
            switch (item.getItemId()) {
                case R.id.map:
                    if (lastPressed == LIST) {
                        app.gps.search(MainScreen.this);
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left,
                                        R.anim.slide_out_right,
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left)
                                .replace(R.id.fragment_container, mapFragment)
                                .commit();
                        lastPressed = MAP;
                    }
                    break;
                case R.id.list:
                    if (lastPressed == MAP) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left,
                                        R.anim.slide_in_left,
                                        R.anim.slide_out_right)
                                .replace(R.id.fragment_container, listFrag)
                                .commit();
                        lastPressed = LIST;
                    }
                    break;
            }
            return true;
        }
    };

    public void animateText() {
        TextView tv1 = findViewById(R.id.textView1);
        TextView tv2 = findViewById(R.id.textView2);
        tv1.setAlpha(0);
        tv2.setAlpha(0);
        tv1.animate().alpha(1).setDuration(2000).setStartDelay(1000).start();
        tv2.animate().alpha(1).setDuration(2000).setStartDelay(3000).start();
    }


}
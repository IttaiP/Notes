package com.moveo.notes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moveo.notes.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

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
                .tiltGesturesEnabled(false);
        mapFragment = SupportMapFragment.newInstance(options);
        mapFragment.getMapAsync(this);



        Log.e("EMPTY", String.valueOf(app.info.noteList.isEmpty()));
        if(!app.info.noteList.isEmpty()){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFrag()).commit();
        }
        else{
            onEmpty = true;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoNotesFragment()).commit();
        }
        centerTitle();

        final Observer<Integer> lengthObserver = newUpdatedListLength -> {
            if(newUpdatedListLength>0){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,listFrag ).commit();
            }
            else{
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NoNotesFragment()).commit();
            }

        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        app.info.getUpdatedList().observe(this, lengthObserver);



        Intent logoutIntent = new Intent(this, Login.class);
        logOut.setOnClickListener(view -> {
            app.info.LogOut();
            startActivity(logoutIntent);
            finish();
        });

        Intent newNoteIntent = new Intent(this, NewNote.class);
        newNote.setOnClickListener(view -> {
            startActivity(newNoteIntent);
            finish();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        for (Note note:app.info.noteList) {
            Log.e("place", String.valueOf(note.latitude)+"___"+String.valueOf(note.latitude));
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(note.latitude, note.longitude))
                    .title(note.title));
        }

    }




    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            Fragment selectedFragment = null;
            if(app.info.noteList.isEmpty()) return true;
            switch (item.getItemId()) {
                case R.id.map:
                    if(lastPressed == LIST) {
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
                    if(lastPressed == MAP) {
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
            // It will help to replace the
            // one fragment to other.
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.fragment_container, selectedFragment)
//                    .commit();
            return true;
        }


    };


}
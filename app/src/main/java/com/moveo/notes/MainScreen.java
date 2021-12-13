package com.moveo.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moveo.notes.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

public class MainScreen extends ActivityAncestor {
    boolean MAP = true;
    boolean LIST = false;
    boolean lastPressed = MAP;
    Button logOut, newNote;







    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        logOut = findViewById(R.id.logout);
        newNote = findViewById(R.id.new_note);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MapFrag()).commit();

        centerTitle();



        Intent logoutIntent = new Intent(this, Login.class);
        logOut.setOnClickListener(view -> {
            app.info.LogOut();
            startActivity(logoutIntent);
            finish();
        });

        Intent newNoteIntent = new Intent(this, NewNote.class);
        newNote.setOnClickListener(view -> {
            app.info.LogOut();
            startActivity(newNoteIntent);
            finish();
        });
    }




    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            // By using switch we can easily get
            // the selected fragment
            // by using there id.
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.map:
                    if(lastPressed == LIST) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .setCustomAnimations(R.anim.slide_in_left,
                                        R.anim.slide_out_right,
                                        R.anim.slide_in_right,
                                        R.anim.slide_out_left)
                                .replace(R.id.fragment_container, new MapFrag())
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
                                .replace(R.id.fragment_container, new ListFrag())
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
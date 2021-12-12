package com.moveo.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.firebase.firestore.FirebaseFirestore;

public class Info {
    SharedPreferences sp;
    public String userEmail;
    public String myID = null;
    public User currentUser;
    FirebaseFirestore db;


    public Info(Context context) {
        db = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public void StoreEmail(String email){
        // todo: add email to paper.
    }

    public void StorePassword(String password){
        // todo: add password to sp with email as key.
    }

    public void RemmemberLogIn(String email){
        sp.edit().putString("logged_in", email).apply();
    }

    public void LogOut(){
        sp.edit().remove("logged_in").apply();
    }

    public String getLoggedIn(){
        return sp.getString("logged_in", "");
    }


}

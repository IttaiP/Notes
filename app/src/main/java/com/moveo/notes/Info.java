package com.moveo.notes;

import android.content.SharedPreferences;

import com.google.firebase.firestore.FirebaseFirestore;

public class Info {
    SharedPreferences sp;
    public String userEmail;
    public String myID = null;
    public User currentUser;
    FirebaseFirestore db;


    public Info() {
         db = FirebaseFirestore.getInstance();
    }

    public void StoreEmail(String email){
        // todo: add email to paper.
    }

    public void StorePassword(String password){
        // todo: add password to sp with email as key.
    }


}

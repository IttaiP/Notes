package com.moveo.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Info {
    SharedPreferences sp;
    public String userEmail;
    public String myID = null;
    public User currentUser;
    FirebaseFirestore db;
    List<Note> noteList;
    Context appContext;


    public Info(Context context) {
        appContext = context;
        db = FirebaseFirestore.getInstance();
        sp = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public void setUser(User user, String id){
        this.currentUser = user;
        this.currentUser.SetID(id);
        loadNotesFromFirestore();
    }

    public void loadNotesFromFirestore(){
       noteList = new ArrayList<>();
        db.collection("users")
                .document(this.currentUser.id)
                .collection("notes")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {

                        for (DocumentSnapshot document : task.getResult().getDocuments()) {
                            Log.e("DOCUMENTTT", document.getId() + " => " + document.getData());
                            Note newNote = new Note(document.getId(), document.getString("title"), document.getString("body"), document.getTimestamp("date"), document.getGeoPoint("location"));
                            noteList.add(newNote);
                            Log.d("ID IS ", document.getId());
                            Log.d("TITLE IS ", document.getString("title"));

                        }
//                        Gson gson = new Gson();
//                        String ratingsAsJson = gson.toJson(info.ratings);
//                        Paper.book(info.getUserEmail()).write("ratings", info.ratings);
//                        info.sp.edit().putString("ratings", ratingsAsJson).apply();
//                        String iRatingsAsJson = gson.toJson(info.indicesInRatings);
//                        Paper.book(info.getUserEmail()).write("iRatings", info.indicesInRatings);
//                        info.sp.edit().putString("iRatings", iRatingsAsJson).apply();
                    } else {
                        Log.e("ERRRORRR", "Error getting documents: ", task.getException());
                    }
                });
    }

    public void StoreEmail(String email){
        // todo: add email to paper.
    }

    public void StorePassword(String password){
        // todo: add password to sp with email as key.
    }

    public void RemmemberLogIn(String email){
        sp.edit().putString("logged_in", email).apply();
        sp.edit().putString("password", currentUser.password).apply();
    }

    public void LogOut(){
        sp.edit().remove("logged_in").apply();
    }

    public String getLoggedIn(){
        return sp.getString("logged_in", "");
    }

    public void addNoteToDB(Note newNote){
        String id = db.collection("collection_name").document().getId();
        newNote.setId(id);
        db.collection("users").document(this.currentUser.id).collection("notes").document(id)
                .set(newNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("WRITE SUCCES", "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("WRITE FAILURE", "Error writing document", e);

                    }
                });
    }


}

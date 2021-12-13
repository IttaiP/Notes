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
//        ArrayList<String> arrayList = new ArrayList<>();
//        db.collection("users").whereEqualTo("User email", user.getEmail())
//                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful() && !task.getResult().isEmpty()) {
//                    DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
//                    String documentId = documentSnapshot.getId();
//                    db.collection("notite").document(documentId).get()
//                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    if(documentSnapshot.exists()) {
//                                        String notita = documentSnapshot.getString("Detalii");
//                                        arrayList.add(notita);
//                                        ArrayAdapter arrayAdapter = new ArrayAdapter<>(appContext, android.R.layout.simple_list_item_1,
//                                                arrayList);
//                                        listView.setAdapter(arrayAdapter);
//                                    } else {
//                                        Toast.makeText(getContext(), "Notes does not exist",
//                                                Toast.LENGTH_LONG).show();
//                                    }
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(appContext, "Failure retrieving",
//                                    Toast.LENGTH_LONG).show();
//                            Log.d("Retrieve", e.toString());
//                        }
//                    });
//
//                } else {
//                    Toast.makeText(appContext, "Does not exist"
//                            , Toast.LENGTH_LONG).show();
//                }
//            }
//        });
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


}

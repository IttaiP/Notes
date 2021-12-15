package com.moveo.notes;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

import io.paperdb.Paper;

public class Info {
    SharedPreferences sp;
    public User currentUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    public FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();

    List<Note> noteList;
    Context appContext;
    private MutableLiveData<Integer> listLength;
    private MutableLiveData<Boolean> locationUpdate;



    public Info(Context context) {
        appContext = context;
        sp = PreferenceManager.getDefaultSharedPreferences(appContext);
    }

    public void setUser(User user, String id){
        this.currentUser = user;
        this.currentUser.SetID(id);
        loadNotesFromFirestore();
        saveUserToPaper();
    }

    public MutableLiveData<Integer> getUpdatedList() {
        if (listLength == null) {
            listLength = new MutableLiveData<Integer>();
        }
        return listLength;
    }

    public MutableLiveData<Boolean> getLocationUpdate() {
        if (locationUpdate == null) {
            locationUpdate = new MutableLiveData<Boolean>();
        }
        return locationUpdate;
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
                            Note newNote = new Note(document.getId(), document.getString("title"), document.getString("body"), document.getTimestamp("date"), document.getGeoPoint("location"));
                            noteList.add(newNote);
                            getUpdatedList().setValue(noteList.size());

                        }
                        saveNoteListToPaper();

//                        Gson gson = new Gson();
//                        String ratingsAsJson = gson.toJson(info.ratings);
//                        Paper.book(info.getUserEmail()).write("ratings", info.ratings);
//                        info.sp.edit().putString("ratings", ratingsAsJson).apply();
//                        String iRatingsAsJson = gson.toJson(info.indicesInRatings);
//                        Paper.book(info.getUserEmail()).write("iRatings", info.indicesInRatings);
//                        info.sp.edit().putString("iRatings", iRatingsAsJson).apply();
                    } else {
                        Log.e("ERRROR", "Error getting documents: ", task.getException());
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
        this.noteList.clear();
//        this.getUpdatedList().setValue(0);//
    }

    public String getLoggedIn(){
        return sp.getString("logged_in", "");
    }

    public void addNoteToDB(Note newNote){
        String id = db.collection("users").document(this.currentUser.id).collection("notes").document().getId();
        newNote.setId(id);
        db.collection("users").document(this.currentUser.id).collection("notes").document(id)
                .set(newNote)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("WRITE SUCCESS", "DocumentSnapshot successfully written!");
                        getUpdatedList().setValue(noteList.size());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("WRITE FAILURE", "Error writing document", e);

                    }
                });
    }

    public void updateNoteInDB(Note noteToUpdate){
        String id = noteToUpdate.id;
        db.collection("users").document(this.currentUser.id)
                .collection("notes").document(id)
                .set(noteToUpdate)
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

    public void deleteNoteFromDB(Note noteToDelete){
        String id = noteToDelete.id;
        db.collection("users").document(this.currentUser.id)
        .collection("notes").document(id)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(appContext, "Note was deleted", Toast.LENGTH_SHORT).show();
                        getUpdatedList().setValue(noteList.size());

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(appContext, "Error deleting note", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    public void loadUserFromPaper(){
        this.currentUser = Paper.book().read("current user", null);
        noteList = Paper.book().read("note list", new ArrayList<>());

        this.getUpdatedList().setValue(noteList.size());
    }

    public void saveUserToPaper(){
        Paper.book().write("current user", this.currentUser);

    }

    public void saveNoteListToPaper(){
        Paper.book().write("note list", noteList);
    }


}

package com.moveo.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActionBar;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.sql.Time;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;
import java.util.UUID;

public class NewNote extends ActivityAncestor {
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;

    NotesApp app;

    Button save, delete, image;
    EditText title, body;
    TextView date;
    Calendar currentDate = Calendar.getInstance();
    CheckBox updateLocation;
    Note newNote = null;

    final Timestamp[] currentTimestamp = {new Timestamp(new Date())};

    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        app = (NotesApp) getApplication();
        app.gps.search(this);

        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        date = findViewById(R.id.date);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
        image = findViewById(R.id.image);
        updateLocation = findViewById(R.id.update_location);

        save.setEnabled(false);
        final boolean[] titleSet = {false};
        final boolean[] bodySet = {false};

        Intent intent = getIntent();
        String id;
        if(intent.getExtras()!= null){
            id = intent.getExtras().getString("id","___");
            updateLocation.setVisibility(View.VISIBLE);
        }
        else{
            id = "___";
            updateLocation.setVisibility(View.GONE);
        }

        if(!id.equals("___")){
//            readNoteFromFirestore(id);
            titleSet[0] = true;
            bodySet[0] = true;
            save.setEnabled(true);
            index = intent.getExtras().getInt("index");
            newNote = app.info.noteList.get(index);
            readNoteFromStorage();
        }




        Log.e("Month", String.valueOf(currentDate.get(Calendar.MONTH)));
        Log.e("FULL DATE", String.valueOf(currentDate.get(Calendar.DATE)));



        date.setText("Date: "+currentDate.get(Calendar.DAY_OF_MONTH)+"."+(currentDate.get(Calendar.MONTH)+1)+"           to change, press here.");

        centerTitle();
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
                builder.setTitleText("Choose Date");
                MaterialDatePicker<Long> picker = builder.build();
                picker.show(getSupportFragmentManager(), picker.toString());
                picker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
                    @Override
                    public void onPositiveButtonClick(Long selection) {
//                        Log.e("picker", String.valueOf(picker.getSelection()));
//                        Date newDate = new Date(picker.getSelection());
                        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                        calendar.setTimeInMillis(selection);
//                        currentTimestamp[0] = new Timestamp(newDate);
                        date.setText("Date: "+calendar.get(Calendar.DAY_OF_MONTH)+"."+(calendar.get(Calendar.MONTH)+1)+"         to change, press here.");

                    }
                });

            }
        });

        save.setOnClickListener(view -> {
            if(id.equals("___")) {
                Note tempNote = new Note(id, title.getText().toString(), body.getText().toString(),
                        currentTimestamp[0], new GeoPoint(app.gps.getLatitude(), app.gps.getLongitude()));// todo: add location
                newNote = new Note(tempNote);
                app.info.addNoteToDB(newNote);
                app.info.noteList.add(newNote);
                app.info.saveUserToPaper();
                app.info.saveNoteListToPaper();
                startActivity(new Intent(NewNote.this, MainScreen.class));
                finish();
            }
            else{
                // todo: update with new location
                newNote.title = title.getText().toString();
                newNote.body = body.getText().toString();
                newNote.date = currentTimestamp[0];

                if(updateLocation.isChecked()){
                    newNote.location = new GeoPoint(app.gps.getLatitude(), app.gps.getLongitude());
                    newNote.setLatitude(app.gps.getLatitude());
                    newNote.setLongitude(app.gps.getLongitude());
                }
                else{

                }
                app.info.noteList.remove(index);
                app.info.noteList.add(index, newNote);



                app.info.updateNoteInDB(app.info.noteList.get(index));
                app.info.saveUserToPaper();
                app.info.saveNoteListToPaper();
                startActivity(new Intent(NewNote.this, MainScreen.class));
                finish();

            }
        });

        delete.setOnClickListener(view -> {
            if(!id.equals("___")) {
                Note tempNote = app.info.noteList.get(index);
                app.info.noteList.remove(index);
                app.info.deleteNoteFromDB(tempNote);
                app.info.saveNoteListToPaper();
                app.info.saveNoteListToPaper();
            }

            startActivity(new Intent(this, MainScreen.class));
            finish();
        });


        title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                titleSet[0] = true;
                save.setEnabled(bodySet[0] && !title.getText().toString().equals(""));
            }
        });
        body.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                bodySet[0] = true;
                save.setEnabled(titleSet[0] && !body.getText().toString().equals(""));
            }
        });

        image.setOnClickListener(view -> {
            // check runtime permission
            if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                // permission not granted, request it.
                String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                // show popup
                requestPermissions(permissions, PERMISSION_CODE);
            } else {
                // permission already granted
                pickImageFromGallery();
            }
        });

    }

    private void readNoteFromStorage(){
       ;
        title.setText(newNote.title);
        body.setText(newNote.body);
        currentTimestamp[0] = newNote.date;
        Date oldDate = currentTimestamp[0].toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(oldDate);
        date.setText("Date: "+calendar.get(Calendar.DAY_OF_MONTH)+"."+(currentDate.get(Calendar.MONTH)+1)+"           to change, press here.");

    }

    private void readNoteFromFirestore(String id){
        app.info.db.collection("users")
                .document(app.info.currentUser.id)
                .collection("notes")
                .document(id)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        title.setText(document.getString("title"));
                        body.setText(document.getString("body"));
                        currentTimestamp[0] = document.getTimestamp("date");
                        Date oldDate = currentTimestamp[0].toDate();
                        Calendar calendar = Calendar.getInstance();
                        calendar.setTime(oldDate);
                        date.setText("Date: "+calendar.get(Calendar.DAY_OF_MONTH)+"."+(currentDate.get(Calendar.MONTH)+1)+"           to change, press here.");
//                        image =
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



    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted
                    pickImageFromGallery();
                }
                else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri imageUri = data.getData();
            StorageReference storageReference = app.info.firebaseStorage.getReference();
            String ts = String.valueOf(System.currentTimeMillis() / 1000);
            StorageReference photoRef = storageReference.child(title.getText().toString()+UUID.randomUUID().toString());
            photoRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.e("SUCCESS UPLOADING", "photo");
                    photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Log.e("The uri is ", uri.toString());
                            newNote.setImage(uri);
                        }
                    });
                }
            });

        }
    }


}
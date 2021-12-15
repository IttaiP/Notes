package com.moveo.notes;

import androidx.annotation.NonNull;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
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
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.storage.StorageReference;
import java.util.Calendar;
import java.util.Date;
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
        centerTitle();

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
        if (intent.getExtras() != null) {
            id = intent.getExtras().getString("id", "___");
            updateLocation.setVisibility(View.VISIBLE);
        } else {
            id = "___";
            updateLocation.setVisibility(View.GONE);
            newNote = new Note();
        }

        if (!id.equals("___")) {
            titleSet[0] = true;
            bodySet[0] = true;
            save.setEnabled(true);
            index = intent.getExtras().getInt("index");
            newNote = app.info.noteList.get(index);
            readNoteFromStorage();
        }

        date.setText("Date: " + currentDate.get(Calendar.DAY_OF_MONTH) + "." + (currentDate.get(Calendar.MONTH) + 1) + "           to change, press here.");

        date.setOnClickListener(view -> {
            MaterialDatePicker.Builder<Long> builder = MaterialDatePicker.Builder.datePicker();
            builder.setTitleText("Choose Date");
            MaterialDatePicker<Long> picker = builder.build();
            picker.show(getSupportFragmentManager(), picker.toString());
            picker.addOnPositiveButtonClickListener(selection -> {
                Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(selection);
                date.setText("Date: " + calendar.get(Calendar.DAY_OF_MONTH) + "." + (calendar.get(Calendar.MONTH) + 1) + "         to change, press here.");
            });
        });

        save.setOnClickListener(view -> {
            if (id.equals("___")) {
                Note tempNote = new Note(id, title.getText().toString(), body.getText().toString(),
                        currentTimestamp[0], new GeoPoint(app.gps.getLatitude(), app.gps.getLongitude()));// todo: add location
                tempNote.image = newNote.getImage();
                newNote = new Note(tempNote);
                app.info.addNoteToDB(newNote);
                app.info.noteList.add(newNote);
                app.info.saveUserToPaper();
                app.info.saveNoteListToPaper();
                startActivity(new Intent(NewNote.this, MainScreen.class));
                finish();
            } else {
                newNote.title = title.getText().toString();
                newNote.body = body.getText().toString();
                newNote.date = currentTimestamp[0];
                if (updateLocation.isChecked()) {
                    newNote.location = new GeoPoint(app.gps.getLatitude(), app.gps.getLongitude());
                    newNote.setLatitude(app.gps.getLatitude());
                    newNote.setLongitude(app.gps.getLongitude());
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
            if (!id.equals("___")) {
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

    private void readNoteFromStorage() {
        title.setText(newNote.title);
        body.setText(newNote.body);
        currentTimestamp[0] = newNote.date;
        Date oldDate = currentTimestamp[0].toDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(oldDate);
        date.setText("Date: " + calendar.get(Calendar.DAY_OF_MONTH) + "." + (currentDate.get(Calendar.MONTH) + 1) + "           to change, press here.");
    }


    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        save.setEnabled(false);
        save.setText("uploading image..");
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            Uri imageUri = data.getData();
            StorageReference storageReference = app.info.firebaseStorage.getReference();
            String ts = String.valueOf(System.currentTimeMillis() / 1000);
            StorageReference photoRef = storageReference.child(title.getText().toString() + UUID.randomUUID().toString());
            photoRef.putFile(imageUri).addOnSuccessListener(taskSnapshot -> {
                Log.e("SUCCESS UPLOADING", "photo");
                photoRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Log.e("The uri is ", uri.toString());
                        save.setEnabled(true);
                        save.setText("save");
                        newNote.setImage(uri.toString());
                        if (newNote.id != null && !newNote.id.equals("___")) {
                            app.info.updateNoteInDB(newNote);
                            app.info.saveUserToPaper();
                            app.info.saveNoteListToPaper();
                        }
                    }
                });
            });
        }
    }
}
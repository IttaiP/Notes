package com.moveo.notes;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.sql.Time;
import java.util.Date;

public class NewNote extends ActivityAncestor {
    Button save,delete;
    EditText title, body;
    TextView date;
    Date currentDate = new Date();
    final Timestamp[] currentTimestamp = {new Timestamp(currentDate)};

    int index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_note);

        Intent intent = getIntent();
        String id;
        if(intent.getExtras()!= null){
            id = intent.getExtras().getString("id","___");
        }
        else{
            id = "___";
        }
        if(!id.equals("___")){
            readNoteFromFirestore(id);
            index = intent.getExtras().getInt("index");
        }

        title = findViewById(R.id.title);
        body = findViewById(R.id.body);
        date = findViewById(R.id.date);
        save = findViewById(R.id.save);
        delete = findViewById(R.id.delete);
        save.setEnabled(false);
        final boolean[] titleSet = {false};
        final boolean[] bodySet = {false};


        date.setText("Date: "+currentDate.getDate()+"."+currentDate.getMonth()+"           to change, press here.");

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
                        Log.e("picker", String.valueOf(picker.getSelection()));
                        Date newDate = new Date(picker.getSelection());
                        currentTimestamp[0] = new Timestamp(newDate);
                        date.setText("Date: "+newDate.getDate()+"."+newDate.getMonth()+"         to change, press here.");

                    }
                });

            }
        });

        save.setOnClickListener(view -> {
            if(id.equals("___")) {
                Note newNote = new Note(id, title.getText().toString(), body.getText().toString(),
                        currentTimestamp[0], null);// todo: add location
                app.info.addNoteToDB(newNote);
                app.info.noteList.add(newNote);
                startActivity(new Intent(NewNote.this, MainScreen.class));
                finish();
            }
            else{
                // todo: update with new location
                app.info.noteList.get(index).title = title.getText().toString();
                app.info.noteList.get(index).body = body.getText().toString();
                app.info.noteList.get(index).date = currentTimestamp[0];
                app.info.updateNoteInDB(app.info.noteList.get(index));
                startActivity(new Intent(NewNote.this, MainScreen.class));
                finish();

            }
        });

        delete.setOnClickListener(view -> {
            if(!id.equals("___")) {
                Note tempNote = app.info.noteList.get(index);
                app.info.noteList.remove(index);
                app.info.deleteNoteFromDB(tempNote);
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
                        date.setText("Date: "+oldDate.getDate()+"."+oldDate.getMonth()+"           to change, press here.");

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
}
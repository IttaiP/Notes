package com.moveo.notes;

import android.app.Application;

import io.paperdb.Paper;

public class NotesApp extends Application {
    Info info;
    @Override
    public void onCreate() {
        super.onCreate();
        Paper.init(this);
        info = new Info(this);
    }
}

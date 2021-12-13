package com.moveo.notes;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Note {
    public String title;
    public String body;
    public int image;
    public double latitude;
    public double longitude;
    Date date;

    public Note(String id, String title, String body, Timestamp date, GeoPoint location){

    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }


}

package com.moveo.notes;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

import java.util.Date;

public class Note {
    public String id;
    public String title;
    public String body;
    public int image;
    public double latitude;
    public double longitude;
    public Timestamp date;

    public Note(String id, String title, String body, Timestamp date, GeoPoint location){
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
        if(location!= null){
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }


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

    public void setId(String id){
        this.id = id;
    }


}

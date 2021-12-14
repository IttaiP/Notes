package com.moveo.notes;

import android.media.Image;
import android.net.Uri;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.GeoPoint;

public class Note {
    public String id;
    public String title;
    public String body;
    public Uri image;
    private double latitude;
    private double longitude;
    public Timestamp date;
    public GeoPoint location;

    public Note(String id, String title, String body, Timestamp date, GeoPoint location){
        this.id = id;
        this.title = title;
        this.body = body;
        this.date = date;
        this.location = location;
        if(location!= null){
            this.latitude = location.getLatitude();
            this.longitude = location.getLongitude();
        }
    }

    public Note(Uri image){
        this.image = image;
    }

    public Note(Note note){
        this.id = note.id;
        this.title = note.title;
        this.body = note.body;
        this.date = note.date;
        this.location = note.location;
        if(location!= null){
            this.latitude = note.location.getLatitude();
            this.longitude = note.location.getLongitude();
        }
        this.image = note.image;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
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

    public double getLatitude(){
        return this.latitude;
    }

    public double getLongitude(){
        return this.longitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}

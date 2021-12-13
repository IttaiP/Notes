package com.moveo.notes;

public class Note {
    public String title;
    public String body;
    public int image;
//    Date date;
//
//    public Note(String title,String body,Date date){
//
//    }

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

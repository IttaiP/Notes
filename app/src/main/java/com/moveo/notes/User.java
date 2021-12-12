package com.moveo.notes;

public class User {
    public String email;
    public String password;
    public String id;

    public User(String email, String password){
        this.email = email;
        this.password = password;
    }

    public void SetID(String id){
        this.id = id;
    }
}

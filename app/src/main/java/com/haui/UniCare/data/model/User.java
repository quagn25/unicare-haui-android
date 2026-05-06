package com.haui.UniCare.data.model;

public class User {
    public int id;
    public String username;
    public String password;
    public String fullName;
    public String role;

    public User() {
    }

    public User(int id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}

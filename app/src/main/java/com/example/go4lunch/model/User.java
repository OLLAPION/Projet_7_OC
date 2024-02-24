package com.example.go4lunch.model;

public class User {

    /** User Identifier */
    // int ou Interger ???
    // changer le nom des variables "names" et "email" car identique avec restaurant ?
    private int id;

    /** User name */
    private String name;

    /** User email */
    private String email;

    /** User avatar */
    private String avatar;

    /** User choiceOfLunch */
    private boolean choiceOfLunch;


    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }


    /**
     * Constructor
     */
    public User(int id, String name, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
    }
}

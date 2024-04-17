package com.example.go4lunch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class User {

    /** User Identifier */
    // int ou Interger ???
    // changer le nom des variables "names" et "email" car identique avec restaurant ?
    // j'ai mi Boolean au lieu de boolean sinon mon getter/setter ne le detecté pas
    // mon constructeur ne le vois pas non plus mon Boolean
    // reçoit l'id de firebase qui est un String (Uuid)
    private String id;

    /** User name */
    private String name;

    /** User email */
    private String email;

    /** User avatar */
    private String avatar;

    /** User notification */
    private Boolean notification;

    /** User likeOfLunch */
    private List<String> likeOfLunch;




    public String getId() {
        return id;
    }
    public void setId(String id) {
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

    public Boolean getNotification() {
        return notification;
    }

    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    public List<String> getLikeOfLunch() {
        return likeOfLunch;
    }

    public void setLikeOfLunch(List<String> likeOfLunch) {
        this.likeOfLunch = likeOfLunch;
    }


    /**
     * Constructor
     */
    public User(String id, String name, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.notification = false;
        this.likeOfLunch = new ArrayList<>();
    }

    public User(String userName, String userAvatar) {
        this.name = userName;
        this.avatar = userAvatar;
    }

    public User() {}

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("name", name);
        result.put("email", email);
        result.put("avatar", avatar);
        result.put("notification", notification);
        return result;
    }
}

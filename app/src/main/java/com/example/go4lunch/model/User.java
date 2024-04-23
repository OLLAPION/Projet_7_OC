package com.example.go4lunch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Model class representing a user.
 */
public class User {

    /** Unique identifier of the user */
    private String id;

    /** Name of the user */
    private String name;

    /** Email of the user */
    private String email;

    /** Avatar of the user */
    private String avatar;

    /** Notification setting for the user */
    private Boolean notification;

    /** List of IDs of liked lunches by the user */
    private List<String> likeOfLunch;

    /**
     * Get the unique identifier of the user.
     * @return The unique identifier of the user.
     */
    public String getId() {
        return id;
    }
    /**
     * Set the unique identifier of the user.
     * @param id The unique identifier of the user.
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the name of the user.
     * @return The name of the user.
     */
    public String getName() {
        return name;
    }
    /**
     * Set the name of the user.
     * @param name The name of the user.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Get the email of the user.
     * @return The email of the user.
     */
    public String getEmail() {
        return email;
    }
    /**
     * Set the email of the user.
     * @param email The email of the user.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Get the avatar of the user.
     * @return The avatar of the user.
     */
    public String getAvatar() {
        return avatar;
    }
    /**
     * Set the avatar of the user.
     * @param avatar The avatar of the user.
     */
    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    /**
     * Check if the user has notifications enabled.
     * @return True if notifications are enabled, false otherwise.
     */
    public Boolean getNotification() {
        return notification;
    }
    /**
     * Set the notification setting for the user.
     * @param notification True to enable notifications, false otherwise.
     */
    public void setNotification(Boolean notification) {
        this.notification = notification;
    }

    /**
     * Get the list of IDs of liked lunches by the user.
     * @return The list of IDs of liked lunches.
     */
    public List<String> getLikeOfLunch() {
        return likeOfLunch;
    }
    /**
     * Set the list of IDs of liked lunches for the user.
     * @param likeOfLunch The list of IDs of liked lunches.
     */
    public void setLikeOfLunch(List<String> likeOfLunch) {
        this.likeOfLunch = likeOfLunch;
    }

    /**
     * Default constructor.
     */
    public User() {}

    /**
     * Constructor to initialize a user object with all attributes.
     * @param id The unique identifier of the user.
     * @param name The name of the user.
     * @param email The email of the user.
     * @param avatar The avatar of the user.
     */
    public User(String id, String name, String email, String avatar) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.avatar = avatar;
        this.notification = false;
        this.likeOfLunch = new ArrayList<>();
    }

    /**
     * Constructor to initialize a user object with name and avatar only.
     * @param userName The name of the user.
     * @param userAvatar The avatar of the user.
     */
    public User(String userName, String userAvatar) {
        this.name = userName;
        this.avatar = userAvatar;
    }

    /**
     * Convert the user object to a map.
     * @return A map representing the user object.
     */
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

package com.example.go4lunch.model.bo;

/**
 * Model class representing a user.
 */
public class User {
    private String id; // Id of User
    private String name; // Name of User
    private String email; // Email of User
    private String avatar; // Avatar of user
    private Boolean notification; // Indicates whether the User has activated or deactivated notifications

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
}

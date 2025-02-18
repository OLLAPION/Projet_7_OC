package com.example.go4lunch;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;

public class LunchTest {

    private Lunch lunch;
    private User user;
    private Restaurant restaurant;

    @Before
    public void setUp() {
        // Initialize the objects before each test
        user = new User(); // Assume User has a default constructor
        restaurant = new Restaurant(); // Assume Restaurant has a default constructor
        lunch = new Lunch(restaurant, user); // Lunch initialized with a user and a restaurant
    }

    @Test
    public void testGetUser() {
        // Test if the getUser() method returns the correct user object
        assertEquals(user, lunch.getUser());
    }

    @Test
    public void testSetUser() {
        // Test if the setUser() method updates the user correctly
        User newUser = new User(); // Another User object
        lunch.setUser(newUser);
        assertEquals(newUser, lunch.getUser());
    }

    @Test
    public void testGetRestaurant() {
        // Test if the getRestaurant() method returns the correct restaurant object
        assertEquals(restaurant, lunch.getRestaurant());
    }

    @Test
    public void testSetRestaurant() {
        // Test if the setRestaurant() method updates the restaurant correctly
        Restaurant newRestaurant = new Restaurant(); // Another Restaurant object
        lunch.setRestaurant(newRestaurant);
        assertEquals(newRestaurant, lunch.getRestaurant());
    }

    @Test
    public void testGetDayDate() {
        // Test if the getDayDate() method returns the correct date
        long currentTime = System.currentTimeMillis();
        lunch.setDayDate(currentTime);
        assertEquals(currentTime, lunch.getDayDate().longValue());
    }

    @Test
    public void testSetDayDate() {
        // Test if the setDayDate() method sets the correct date
        long newDate = System.currentTimeMillis() + 1000;
        lunch.setDayDate(newDate);
        assertEquals(newDate, lunch.getDayDate().longValue());
    }

    @Test
    public void testParameterizedConstructor() {
        // Test if the parameterized constructor correctly initializes the Lunch object
        Lunch newLunch = new Lunch(restaurant, user);
        assertEquals(restaurant, newLunch.getRestaurant());
        assertEquals(user, newLunch.getUser());
    }
}
package com.example.go4lunch;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.go4lunch.model.Lunch;
import com.example.go4lunch.model.Restaurant;
import com.example.go4lunch.model.User;
import com.example.go4lunch.repository.LunchRepository;
import com.google.firebase.firestore.*;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

public class LunchRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private LunchRepository lunchRepository;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        lunchRepository = mock(LunchRepository.class);
    }

    @Test
    public void testGetLunchesForToday_ReturnsData() {
        // Mocking Firestore response
        List<Lunch> mockLunches = new ArrayList<>();
        mockLunches.add(new Lunch(new Restaurant(), new User()));
        MutableLiveData<List<Lunch>> liveData = new MutableLiveData<>(mockLunches);

        when(lunchRepository.getLunchesForToday()).thenReturn(liveData);

        // Act
        LiveData<List<Lunch>> result = lunchRepository.getLunchesForToday();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getValue().size());
    }

    @Test
    public void testCreateLunch_Success() {
        // Given
        Restaurant restaurant = new Restaurant();
        User user = new User();
        Lunch lunch = new Lunch(restaurant, user);
        lunch.setDayDate(System.currentTimeMillis());

        doNothing().when(lunchRepository).createLunch(eq(restaurant), eq(user), any());

        // Act
        lunchRepository.createLunch(restaurant, user, () -> {});

        // Assert
        verify(lunchRepository, times(1)).createLunch(eq(restaurant), eq(user), any());
    }

    @Test
    public void testGetTodayLunch_ReturnsLunch() {
        // Mocking Firestore response
        Lunch mockLunch = new Lunch(new Restaurant(), new User());
        MutableLiveData<Lunch> liveData = new MutableLiveData<>(mockLunch);

        when(lunchRepository.getTodayLunch("user123")).thenReturn(liveData);

        // Act
        LiveData<Lunch> result = lunchRepository.getTodayLunch("user123");

        // Assert
        assertNotNull(result);
        assertEquals(mockLunch, result.getValue());
    }
}

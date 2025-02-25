package com.example.go4lunch;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import com.example.go4lunch.model.pojo.RestaurantsAnswer;
import com.example.go4lunch.model.pojo.ResultDetails;
import com.example.go4lunch.model.services.RetrofitMapsApi;
import com.example.go4lunch.model.services.RetrofitService;
import com.example.go4lunch.model.pojo.Result;

import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

public class RetrofitServiceTest {

    @Mock
    private RetrofitMapsApi mockRetrofitMapsApi;

    @Mock
    private Call<RestaurantsAnswer> mockRestaurantsCall;

    @Mock
    private Result mockResult;

    private RetrofitService retrofitService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockRetrofitMapsApi = mock(RetrofitMapsApi.class);
        retrofitService = new RetrofitService();
    }


    @Test
    public void testGetRestaurantApi_ReturnsApi() {
        // Test if the method returns the RetrofitMapsApi instance
        RetrofitMapsApi api = retrofitService.getRestaurantApi();
        assertNotNull(api);
    }

    @Test
    public void testGetAllRestaurants_CallExecutedSuccessfully() throws IOException {
        // Simulate a successful API call for getting all restaurants
        String location = "40.748817,-73.985428";
        int radius = 1500;
        String type = "restaurant";
        String key = "API_KEY";

        // Create a mock response
        RestaurantsAnswer mockResponse = new RestaurantsAnswer();
        Response<RestaurantsAnswer> mockApiResponse = Response.success(mockResponse);

        // Mocking the Retrofit call
        when(mockRetrofitMapsApi.getAllRestaurants(location, radius, type, key)).thenReturn(mockRestaurantsCall);
        when(mockRestaurantsCall.execute()).thenReturn(mockApiResponse);

        // Act
        Call<RestaurantsAnswer> call = mockRetrofitMapsApi.getAllRestaurants(location, radius, type, key);
        Response<RestaurantsAnswer> response = call.execute();

        // Assert
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
    }

    @Test
    public void testGetRestaurantDetails_CallExecutedSuccessfully() throws IOException {
        // Simulate a successful API call for getting restaurant details
        String key = "API_KEY";
        String placeId = "ChIJN1t_tDeuEmsRUsoyG83frY4";

        when(mockResult.getPlaceId()).thenReturn(placeId);

        // Create a mock response
        ResultDetails mockDetailsResponse = new ResultDetails(mockResult);
        Response<ResultDetails> mockDetailsApiResponse = Response.success(mockDetailsResponse);

        // Mocking the Retrofit call
        Call<ResultDetails> mockDetailsCall = mock(Call.class);
        when(mockRetrofitMapsApi.getRestaurantDetails(key, placeId)).thenReturn(mockDetailsCall);
        when(mockDetailsCall.execute()).thenReturn(mockDetailsApiResponse);

        // Act
        Call<ResultDetails> call = mockRetrofitMapsApi.getRestaurantDetails(key, placeId);
        Response<ResultDetails> response = call.execute();

        // Assert
        assertTrue(response.isSuccessful());
        assertNotNull(response.body());
        assertEquals(placeId, response.body().getResult().getPlaceId());
    }

    @Test
    public void testGetAllRestaurants_Fails() throws IOException {
        // Simulate a failed API call for getting all restaurants
        String location = "40.748817,-73.985428"; // Example location
        int radius = 1500;
        String type = "restaurant";
        String key = "API_KEY";

        // Simulate a failed response
        Response<RestaurantsAnswer> mockApiErrorResponse = Response.error(404, okhttp3.ResponseBody.create(null, "Not Found"));

        // Mocking the Retrofit call
        when(mockRetrofitMapsApi.getAllRestaurants(location, radius, type, key)).thenReturn(mockRestaurantsCall);
        when(mockRestaurantsCall.execute()).thenReturn(mockApiErrorResponse);

        // Act
        Call<RestaurantsAnswer> call = mockRetrofitMapsApi.getAllRestaurants(location, radius, type, key);
        Response<RestaurantsAnswer> response = call.execute();

        // Assert
        assertFalse(response.isSuccessful());
        assertEquals(404, response.code());
    }
}


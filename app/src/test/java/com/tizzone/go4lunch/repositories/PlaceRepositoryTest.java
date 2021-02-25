package com.tizzone.go4lunch.repositories;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

public class PlaceRepositoryTest {

    private MockWebServer mockWebServer;

    @Before
    private void setUp() throws Exception {
        mockWebServer = new MockWebServer();
        // Schedule some responses.
        mockWebServer.enqueue(new MockResponse().setBody("hello, world!"));
        mockWebServer.enqueue(new MockResponse().setBody("sup, bra?"));
        mockWebServer.enqueue(new MockResponse().setBody("yo dog"));
        mockWebServer.start();
    }

    @After
    private void tearDown() throws Exception {
        mockWebServer.shutdown();
    }

    @Test
    public void testGetNearByPlacesApi() {
    }

    @Test
    public void testGetDetailByPlaceId() {
    }

    @Test
    public void testGetPredictionsApi() {
    }
}
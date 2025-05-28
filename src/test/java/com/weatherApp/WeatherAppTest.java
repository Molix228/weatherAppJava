package com.weatherApp;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.net.HttpURLConnection;

import static org.junit.jupiter.api.Assertions.*;

class WeatherAppTest {

    static MockWebServer server;

    @BeforeAll
    static void startServer() throws IOException {
        server = new MockWebServer();
        server.start();
        WeatherApp.baseUrl = server.url("/data/2.5/weather").toString().replaceAll("/$", "");
    }

    @AfterAll
    static void stopServer() throws IOException {
        server.shutdown();
    }

    @Test
    void testGetWeatherData_Success() {
        String body = """
        {
          "main": {"temp": 22.0, "humidity": 33},
          "wind": {"speed": 4.2},
          "weather": [{"main": "Clear"}]
        }
        """;
        server.enqueue(new MockResponse().setResponseCode(200).setBody(body));

        JSONObject result = WeatherApp.getWeatherData("Moscow");
        assertNotNull(result);
        assertEquals(22.0, (double) result.get("temp"));
        assertEquals(33L, result.get("humidity"));
        assertEquals(4.2, (double) result.get("wind_speed"));
        assertEquals("Clear", result.get("condition"));
    }

    @Test
    void testGetWeatherData_ApiError() {
        server.enqueue(new MockResponse().setResponseCode(404).setBody("{}"));
        JSONObject result = WeatherApp.getWeatherData("UnknownCity");
        assertNull(result);
    }

    @Test
    void testGetWeatherData_BadJson() {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("not json!"));
        JSONObject result = WeatherApp.getWeatherData("BadJsonCity");
        assertNull(result);
    }

    @Test
    void testGetLocationData_OK() {
        String body = """
        { "main": {"temp": 20, "humidity": 62},
          "wind": {"speed": 10},
          "weather": [{"main": "Clouds"}]
        }
        """;
        server.enqueue(new MockResponse().setResponseCode(200).setBody(body));
        JSONObject result = WeatherApp.getLocationData("Testcity");
        assertNotNull(result);
        assertEquals(20.0, ((Number)((JSONObject) result.get("main")).get("temp")).doubleValue());
    }

    @Test
    void testGetLocationData_Error() {
        server.enqueue(new MockResponse().setResponseCode(500));
        JSONObject result = WeatherApp.getLocationData("failcity");
        assertNull(result);
    }

    @Test
    void testFetchApiResponse_ValidUrl() throws IOException {
        server.enqueue(new MockResponse().setResponseCode(200).setBody("{}"));
        String url = WeatherApp.baseUrl + "?q=city&appid=xxx";
        HttpURLConnection conn = WeatherApp.fetchApiResponse(url);
        assertNotNull(conn);
        assertEquals(200, conn.getResponseCode());
        conn.disconnect();
    }

    @Test
    void testFetchApiResponse_InvalidUrl() {
        HttpURLConnection conn = WeatherApp.fetchApiResponse("ht!tp:/bad-url");
        assertNull(conn);
    }

    @Test
    void testParseWeatherData_NullFields() {
        JSONObject bad = new JSONObject();
        assertThrows(NullPointerException.class, () -> WeatherApp.parseWeatherData(bad));
    }

    @Test
    void testConstructor() {
        new WeatherApp();
    }
}
package com.weatherApp;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class WeatherApp {
    // Позволяет тестам подменять базовый URL
    static String baseUrl = "https://api.openweathermap.org/data/2.5/weather";

    public static JSONObject getWeatherData(String locationName) {
        JSONObject locationData = getLocationData(locationName);
        if (locationData == null) return null; // <--- fix NPE

        return parseWeatherData(locationData);
    }

    public static JSONObject getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");
        String apiKey = System.getenv("API_KEY");
        String urlString = baseUrl + "?q=" +
                locationName + "&appid=" + apiKey + "&units=metric";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            if(conn == null || conn.getResponseCode() != 200) {
                System.out.println("Error: Could not connect to API");
                return null;
            } else {
                StringBuilder resultJson = new StringBuilder();

                Scanner scanner = new Scanner(conn.getInputStream());
                while(scanner.hasNext()) {
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();
                conn.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));
                return resultJsonObj;
            }
        }catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    static HttpURLConnection fetchApiResponse(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.connect();
            return conn;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    // Этот метод можно безопасно тестировать отдельно
    public static JSONObject parseWeatherData(JSONObject locationData) {
        JSONObject mainWeatherData = (JSONObject) locationData.get("main");
        double temp = getDouble(mainWeatherData, "temp");
        long humidity = getLong(mainWeatherData, "humidity");

        JSONObject windSpeedData = (JSONObject) locationData.get("wind");
        double speed = getDouble(windSpeedData, "speed");

        JSONArray weatherCondition = (JSONArray) locationData.get("weather");
        JSONObject pos = (JSONObject) weatherCondition.getFirst();
        String condition = (String) pos.get("main");

        JSONObject weatherData = new JSONObject();
        weatherData.put("temp", temp);
        weatherData.put("humidity", humidity);
        weatherData.put("wind_speed", speed);
        weatherData.put("condition", condition);

        return weatherData;
    }

    // Универсальные геттеры для корректного каста!
    private static double getDouble(JSONObject obj, String key) {
        Object value = obj.get(key);
        if (value instanceof Long) {
            return ((Long) value).doubleValue();
        }
        return (Double) value;
    }
    private static long getLong(JSONObject obj, String key) {
        Object value = obj.get(key);
        if (value instanceof Long) return (Long) value;
        return ((Double) value).longValue();
    }
}
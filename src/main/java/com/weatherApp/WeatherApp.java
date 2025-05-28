package com.weatherApp;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

// get weather Data from API
public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        JSONObject locationData = getLocationData(locationName);


        // temp and humidity data
        JSONObject mainWeatherData = (JSONObject) locationData.get("main");
        double temp = (double) mainWeatherData.get("temp");
        long humidity = (long) mainWeatherData.get("humidity");

        // wind speed data
        JSONObject windSpeedData = (JSONObject) locationData.get("wind");
        double speed = (double) windSpeedData.get("speed");

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

    public static JSONObject getLocationData(String locationName) {
        locationName = locationName.replaceAll(" ", "+");

        String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" +
                locationName + "&appid=***REMOVED***&units=metric";

        try {
            HttpURLConnection conn = fetchApiResponse(urlString);

            if(conn.getResponseCode() != 200) {
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

    private static HttpURLConnection fetchApiResponse(String urlString) {
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
}

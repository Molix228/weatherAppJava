package com.weatherApp;

import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static java.awt.Cursor.getPredefinedCursor;

public class WeatherAppGui extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGui() {
        super("Weather App");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(450,650);
        setLocation(100, 100);
        setLayout(null);
        setResizable(false);

        addGuiComponents();
    }
    private void addGuiComponents(){
        //search field
        JTextField searchTextField = new JTextField();

        searchTextField.setBounds(15,15,351, 45);

        searchTextField.setFont(new Font("Dialog", Font.PLAIN, 24));

        add(searchTextField);

        // weather image
        JLabel weatherConditionImage = new JLabel(loadImage("images/cloudy.png"));
        weatherConditionImage.setBounds(0, 105, 450, 217);
        add(weatherConditionImage);

        // weather text

        JLabel tempText = new JLabel("10 °C");
        tempText.setBounds(0, 330, 450, 54);
        tempText.setFont(new Font("Dialog", Font.BOLD, 48));

        tempText.setHorizontalAlignment(SwingConstants.CENTER);
        add(tempText);

        //weather condition description

        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0, 385, 450, 36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        // humidity image

        JLabel humidityImage = new JLabel(loadImage("images/humidity.png"));
        humidityImage.setBounds(10, 460, 70, 70);
        add(humidityImage);

        // humidity text

        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(80, 470, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        // wind speed Image

        JLabel windSpeedImage = new JLabel(loadImage("images/windspeed.png"));
        windSpeedImage.setBounds(240, 460, 80, 70);
        add(windSpeedImage);

        // wind speed Text

        JLabel windSpeedText = new JLabel("<html><b>Wind Speed</b> 20 m/s</html>");
        windSpeedText.setBounds(325, 470, 105, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        // search button
        JButton searchBtn = new JButton(loadImage("images/search.png"));

        searchBtn.setCursor(getPredefinedCursor(Cursor.HAND_CURSOR));
        searchBtn.setBounds(375, 13, 47, 45);
        searchBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchTextField.getText();

                if(userInput.replaceAll("\\s", "").length() <= 0) {
                    return;
                }

                weatherData = WeatherApp.getWeatherData(userInput);

                // update condition image
                String weatherCondition = (String) weatherData.get("condition");

                switch (weatherCondition) {
                    case "Clear":
                        weatherConditionImage.setIcon(loadImage("images/clear.png"));
                        weatherConditionDesc.setText("Clear");
                        break;
                    case "Clouds":
                        weatherConditionImage.setIcon(loadImage("images/cloudy.png"));
                        weatherConditionDesc.setText("Clouds");
                        break;
                    case "Rain":
                        weatherConditionImage.setIcon(loadImage("images/rain.png"));
                        weatherConditionDesc.setText("Rain");
                        break;
                    case "Snow":
                        weatherConditionImage.setIcon(loadImage("images/snow.png"));
                        weatherConditionDesc.setText("Snow");
                        break;
                }

                // update temp
                double temp = (double) weatherData.get("temp");
                tempText.setText(temp + " °C");

                // update humidity
                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity </b>" + humidity + "%</html>");

                // update windSpeed
                double windSpeed = (double) weatherData.get("wind_speed");
                windSpeedText.setText("<html><b>Wind Speed </b>" + windSpeed + "m/s</html>");
            }
        });
        add(searchBtn);
    }

    private ImageIcon loadImage(String resourcePath) {
        try {
            var url = getClass().getClassLoader().getResource(resourcePath);
            if(url != null) {
                return new ImageIcon(url);
            } else {
                System.out.println("Could not find resource: " + resourcePath);
                return null;
            }
        } catch(Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

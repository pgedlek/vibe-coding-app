package com.pgedlek.vibe_coding.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CurrentWeather {
    private double temperature;
    private double windspeed;
    private int winddirection;
    private String time;

    public CurrentWeather() {}

    public CurrentWeather(double temperature, double windspeed, int winddirection, String time) {
        this.temperature = temperature;
        this.windspeed = windspeed;
        this.winddirection = winddirection;
        this.time = time;
    }

    public double getTemperature() { return temperature; }
    public double getWindspeed() { return windspeed; }
    public int getWinddirection() { return winddirection; }
    public String getTime() { return time; }
}

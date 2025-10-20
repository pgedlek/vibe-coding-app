package com.pgedlek.vibe_coding.weather.dto;

import java.util.List;

public class WeatherResponse {
    private String city;
    private double latitude;
    private double longitude;
    private double temperature;
    private double windspeed;
    private int winddirection;
    private String time;
    private List<DailyForecastItem> forecast;

    public WeatherResponse() {}

    public WeatherResponse(String city, double latitude, double longitude, double temperature, double windspeed, int winddirection, String time, List<DailyForecastItem> forecast) {
        this.city = city;
        this.latitude = latitude;
        this.longitude = longitude;
        this.temperature = temperature;
        this.windspeed = windspeed;
        this.winddirection = winddirection;
        this.time = time;
        this.forecast = forecast;
    }

    public String getCity() { return city; }
    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public double getTemperature() { return temperature; }
    public double getWindspeed() { return windspeed; }
    public int getWinddirection() { return winddirection; }
    public String getTime() { return time; }
    public List<DailyForecastItem> getForecast() { return forecast; }
}

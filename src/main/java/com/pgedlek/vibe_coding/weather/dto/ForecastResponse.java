package com.pgedlek.vibe_coding.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {
    private double latitude;
    private double longitude;
    private CurrentWeather current_weather;

    public ForecastResponse() {}

    public ForecastResponse(double latitude, double longitude, CurrentWeather current_weather) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.current_weather = current_weather;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public CurrentWeather getCurrent_weather() { return current_weather; }
}

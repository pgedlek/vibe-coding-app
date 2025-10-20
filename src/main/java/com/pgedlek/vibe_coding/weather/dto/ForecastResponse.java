package com.pgedlek.vibe_coding.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ForecastResponse {
    private double latitude;
    private double longitude;
    private CurrentWeather current_weather;
    private DailyData daily;

    public ForecastResponse() {}

    public ForecastResponse(double latitude, double longitude, CurrentWeather current_weather, DailyData daily) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.current_weather = current_weather;
        this.daily = daily;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public CurrentWeather getCurrent_weather() { return current_weather; }
    public DailyData getDaily() { return daily; }
}

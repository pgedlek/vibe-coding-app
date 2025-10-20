package com.pgedlek.vibe_coding.weather.dto;

public class DailyForecastItem {
    private final String date;
    private final Double tempMax;
    private final Double tempMin;
    private final Integer precipitationProbabilityMax;
    private final Integer weatherCode;

    public DailyForecastItem(String date, Double tempMax, Double tempMin, Integer precipitationProbabilityMax, Integer weatherCode) {
        this.date = date;
        this.tempMax = tempMax;
        this.tempMin = tempMin;
        this.precipitationProbabilityMax = precipitationProbabilityMax;
        this.weatherCode = weatherCode;
    }

    public String getDate() { return date; }
    public Double getTempMax() { return tempMax; }
    public Double getTempMin() { return tempMin; }
    public Integer getPrecipitationProbabilityMax() { return precipitationProbabilityMax; }
    public Integer getWeatherCode() { return weatherCode; }
}

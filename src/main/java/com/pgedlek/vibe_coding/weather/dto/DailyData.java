package com.pgedlek.vibe_coding.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DailyData {
    // Arrays returned by Open-Meteo daily forecast
    private String[] time;
    private Double[] temperature_2m_max;
    private Double[] temperature_2m_min;
    private Integer[] precipitation_probability_max;
    private Integer[] weathercode;

    public DailyData() {
    }

    public DailyData(String[] time,
                     Double[] temperature_2m_max,
                     Double[] temperature_2m_min,
                     Integer[] precipitation_probability_max,
                     Integer[] weathercode) {
        this.time = time;
        this.temperature_2m_max = temperature_2m_max;
        this.temperature_2m_min = temperature_2m_min;
        this.precipitation_probability_max = precipitation_probability_max;
        this.weathercode = weathercode;
    }

    public String[] getTime() { return time; }
    public Double[] getTemperature_2m_max() { return temperature_2m_max; }
    public Double[] getTemperature_2m_min() { return temperature_2m_min; }
    public Integer[] getPrecipitation_probability_max() { return precipitation_probability_max; }
    public Integer[] getWeathercode() { return weathercode; }
}


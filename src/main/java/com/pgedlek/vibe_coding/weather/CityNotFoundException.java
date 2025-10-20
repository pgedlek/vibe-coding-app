package com.pgedlek.vibe_coding.weather;

public class CityNotFoundException extends RuntimeException {
    public CityNotFoundException(String city) {
        super("City not found: " + city);
    }
}


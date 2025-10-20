package com.pgedlek.vibe_coding.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeocodingResponse {
    private List<GeocodingResult> results;

    public GeocodingResponse() {}

    public GeocodingResponse(List<GeocodingResult> results) {
        this.results = results;
    }

    public List<GeocodingResult> getResults() { return results; }
}

package com.pgedlek.vibe_coding.weather;

import com.pgedlek.vibe_coding.weather.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class WeatherService {

    private final WebClient webClient;

    public WeatherService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public Mono<WeatherResponse> getCurrentWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("City must not be blank"));
        }
        String trimmed = city.trim();
        String encodedCity = URLEncoder.encode(trimmed, StandardCharsets.UTF_8);
        String geocodeUrl = "https://geocoding-api.open-meteo.com/v1/search?count=1&language=en&name=" + encodedCity;
        return webClient.get()
                .uri(geocodeUrl)
                .retrieve()
                .bodyToMono(GeocodingResponse.class)
                .flatMap(geo -> {
                    List<GeocodingResult> results = geo.getResults();
                    if (results == null || results.isEmpty()) {
                        return Mono.error(new CityNotFoundException(trimmed));
                    }
                    GeocodingResult result = results.get(0);
                    String forecastUrl = "https://api.open-meteo.com/v1/forecast?current_weather=true&latitude=" + result.getLatitude() + "&longitude=" + result.getLongitude();
                    return webClient.get()
                            .uri(forecastUrl)
                            .retrieve()
                            .bodyToMono(ForecastResponse.class)
                            .map(fr -> new WeatherResponse(
                                    result.getName(),
                                    fr.getLatitude(),
                                    fr.getLongitude(),
                                    fr.getCurrent_weather().getTemperature(),
                                    fr.getCurrent_weather().getWindspeed(),
                                    fr.getCurrent_weather().getWinddirection(),
                                    fr.getCurrent_weather().getTime()
                            ));
                });
    }
}

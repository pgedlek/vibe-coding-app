package com.pgedlek.vibe_coding.weather;

import com.pgedlek.vibe_coding.weather.dto.*;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class WeatherService {

    private final WebClient webClient;
    private final Cache<String, WeatherResponse> weatherCache;

    public WeatherService(WebClient.Builder builder, Cache<String, WeatherResponse> weatherCache) {
        this.webClient = builder.build();
        this.weatherCache = weatherCache;
    }

    public Mono<WeatherResponse> getCurrentWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            return Mono.error(new IllegalArgumentException("City must not be blank"));
        }
        String trimmed = city.trim();
        String normalizedKey = trimmed.toLowerCase();
        WeatherResponse cached = weatherCache.getIfPresent(normalizedKey);
        if (cached != null) {
            return Mono.just(cached);
        }
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
                    String forecastUrl = "https://api.open-meteo.com/v1/forecast?current_weather=true&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max,weathercode&timezone=UTC&latitude=" + result.getLatitude() + "&longitude=" + result.getLongitude();
                    return webClient.get()
                            .uri(forecastUrl)
                            .retrieve()
                            .bodyToMono(ForecastResponse.class)
                            .map(fr -> {
                                List<DailyForecastItem> items = new ArrayList<>();
                                DailyData daily = fr.getDaily();
                                if (daily != null && daily.getTime() != null) {
                                    String[] times = daily.getTime();
                                    Double[] max = daily.getTemperature_2m_max();
                                    Double[] min = daily.getTemperature_2m_min();
                                    Integer[] precip = daily.getPrecipitation_probability_max();
                                    Integer[] codes = daily.getWeathercode();
                                    for (int i = 0; i < times.length; i++) {
                                        items.add(new DailyForecastItem(
                                                times[i],
                                                max != null && i < max.length ? max[i] : null,
                                                min != null && i < min.length ? min[i] : null,
                                                precip != null && i < precip.length ? precip[i] : null,
                                                codes != null && i < codes.length ? codes[i] : null
                                        ));
                                    }
                                }
                                return new WeatherResponse(
                                        result.getName(),
                                        fr.getLatitude(),
                                        fr.getLongitude(),
                                        fr.getCurrent_weather().getTemperature(),
                                        fr.getCurrent_weather().getWindspeed(),
                                        fr.getCurrent_weather().getWinddirection(),
                                        fr.getCurrent_weather().getTime(),
                                        items
                                );
                            });
                })
                .doOnNext(resp -> weatherCache.put(normalizedKey, resp));
    }

    public long cacheSize() {
        return weatherCache.asMap().size();
    }

    public Object cacheStats() {
        return weatherCache.stats();
    }
}

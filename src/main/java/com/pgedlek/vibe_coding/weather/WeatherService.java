package com.pgedlek.vibe_coding.weather;

import com.pgedlek.vibe_coding.weather.dto.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import com.github.benmanes.caffeine.cache.Cache;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private static final String GEOCODE_BASE_URL = "https://geocoding-api.open-meteo.com/v1/search";
    private static final String FORECAST_BASE_URL = "https://api.open-meteo.com/v1/forecast";
    private static final Duration RETRY_BACKOFF = Duration.ofSeconds(1);
    private static final int MAX_RETRIES = 3;

    private final WebClient webClient;
    private final Cache<String, WeatherResponse> weatherCache;

    public WeatherService(WebClient.Builder builder, Cache<String, WeatherResponse> weatherCache) {
        this.webClient = builder.build();
        this.weatherCache = weatherCache;
    }

    private String buildGeocodeUrl(String city) {
        try {
            String encodedCity = URLEncoder.encode(city, StandardCharsets.UTF_8);
            return GEOCODE_BASE_URL + "?count=1&language=en&name=" + encodedCity;
        } catch (Exception e) {
            throw new RuntimeException("Failed to encode city name", e);
        }
    }

    private String buildForecastUrl(double latitude, double longitude) {
        return FORECAST_BASE_URL + "?current_weather=true&daily=temperature_2m_max,temperature_2m_min,precipitation_probability_max,weathercode&timezone=UTC&latitude=" + latitude + "&longitude=" + longitude;
    }

    private Mono<GeocodingResponse> fetchGeocoding(String url) {
        logger.debug("Fetching geocoding data from: {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(GeocodingResponse.class)
                .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF).filter(throwable -> throwable instanceof WebClientException))
                .timeout(Duration.ofSeconds(10));
    }

    private Mono<ForecastResponse> fetchForecast(String url) {
        logger.debug("Fetching forecast data from: {}", url);
        return webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(ForecastResponse.class)
                .retryWhen(Retry.backoff(MAX_RETRIES, RETRY_BACKOFF).filter(throwable -> throwable instanceof WebClientException))
                .timeout(Duration.ofSeconds(10));
    }

    private WeatherResponse mapToWeatherResponse(GeocodingResult geoResult, ForecastResponse forecast) {
        List<DailyForecastItem> items = new ArrayList<>();
        DailyData daily = forecast.getDaily();
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
        String windDirection = convertWindDirectionToCardinal(forecast.getCurrent_weather().getWinddirection());
        return new WeatherResponse(
                geoResult.getName(),
                forecast.getLatitude(),
                forecast.getLongitude(),
                forecast.getCurrent_weather().getTemperature(),
                forecast.getCurrent_weather().getWindspeed(),
                windDirection,
                forecast.getCurrent_weather().getTime(),
                items
        );
    }

    public Mono<WeatherResponse> getCurrentWeather(String city) {
        if (city == null || city.trim().isEmpty()) {
            logger.warn("Invalid city input: null or empty");
            return Mono.error(new IllegalArgumentException("City must not be blank"));
        }
        String trimmed = city.trim();
        String normalizedKey = trimmed.toLowerCase();
        WeatherResponse cached = weatherCache.getIfPresent(normalizedKey);
        if (cached != null) {
            logger.debug("Returning cached weather data for city: {}", trimmed);
            return Mono.just(cached);
        }
        logger.info("Fetching weather data for city: {}", trimmed);
        String geocodeUrl = buildGeocodeUrl(trimmed);
        return fetchGeocoding(geocodeUrl)
                .flatMap(geo -> {
                    List<GeocodingResult> results = geo.getResults();
                    if (results == null || results.isEmpty()) {
                        logger.warn("No geocoding results found for city: {}", trimmed);
                        return Mono.error(new CityNotFoundException(trimmed));
                    }
                    GeocodingResult result = results.get(0);
                    String forecastUrl = buildForecastUrl(result.getLatitude(), result.getLongitude());
                    return fetchForecast(forecastUrl)
                            .map(fr -> mapToWeatherResponse(result, fr));
                })
                .doOnNext(resp -> {
                    weatherCache.put(normalizedKey, resp);
                    logger.debug("Cached weather data for city: {}", trimmed);
                })
                .doOnError(WebClientException.class, e -> logger.error("WebClient error for city {}: {}", trimmed, e.getMessage()))
                .doOnError(CityNotFoundException.class, e -> logger.warn("City not found: {}", trimmed))
                .doOnError(IllegalArgumentException.class, e -> logger.warn("Invalid input: {}", e.getMessage()));
    }

    private String convertWindDirectionToCardinal(int degrees) {
        degrees = degrees % 360;
        if (degrees < 0) degrees += 360;
        if (degrees >= 337.5 || degrees < 22.5) return "N";
        if (degrees >= 22.5 && degrees < 67.5) return "NE";
        if (degrees >= 67.5 && degrees < 112.5) return "E";
        if (degrees >= 112.5 && degrees < 157.5) return "SE";
        if (degrees >= 157.5 && degrees < 202.5) return "S";
        if (degrees >= 202.5 && degrees < 247.5) return "SW";
        if (degrees >= 247.5 && degrees < 292.5) return "W";
        if (degrees >= 292.5 && degrees < 337.5) return "NW";
        return "N"; // fallback
    }

    public long cacheSize() {
        return weatherCache.asMap().size();
    }

    public Object cacheStats() {
        return weatherCache.stats();
    }
}

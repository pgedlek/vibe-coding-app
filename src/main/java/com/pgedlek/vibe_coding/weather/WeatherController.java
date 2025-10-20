package com.pgedlek.vibe_coding.weather;

import com.pgedlek.vibe_coding.weather.dto.WeatherResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping(path = "/api/weather", produces = MediaType.APPLICATION_JSON_VALUE)
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/current")
    public Mono<WeatherResponse> current(@RequestParam("city") String city) {
        return weatherService.getCurrentWeather(city);
    }

    @GetMapping("/cache/size")
    public Mono<Map<String, Long>> cacheSize() {
        return Mono.just(Map.of("size", weatherService.cacheSize()));
    }

    @GetMapping("/cache/stats")
    public Mono<Map<String, Object>> cacheStats() {
        var stats = weatherService.cacheStats();
        return Mono.just(Map.of(
                "size", weatherService.cacheSize(),
                "stats", stats.toString()
        ));
    }
}

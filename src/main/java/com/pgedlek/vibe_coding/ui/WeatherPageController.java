package com.pgedlek.vibe_coding.ui;

import com.pgedlek.vibe_coding.weather.WeatherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@Controller
public class WeatherPageController {

    private final WeatherService weatherService;

    public WeatherPageController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather")
    public Mono<String> weather(@RequestParam(name = "city", required = false) String city, Model model) {
        if (city == null) {
            model.addAttribute("city", "");
            return Mono.just("weather");
        }
        String trimmed = city.trim();
        if (trimmed.isEmpty()) {
            model.addAttribute("city", "");
            model.addAttribute("error", "City must not be blank");
            return Mono.just("weather");
        }
        model.addAttribute("city", trimmed);
        return weatherService.getCurrentWeather(trimmed)
                .map(resp -> {
                    model.addAttribute("weather", resp);
                    return "weather";
                })
                .onErrorResume(ex -> {
                    model.addAttribute("error", ex.getMessage());
                    return Mono.just("weather");
                });
    }
}

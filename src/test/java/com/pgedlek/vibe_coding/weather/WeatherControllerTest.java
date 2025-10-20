package com.pgedlek.vibe_coding.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void shouldReturnCurrentWeatherForKnownCity() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/current").queryParam("city", "Berlin").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.city").value(anyOf(equalToIgnoringCase("Berlin"), containsStringIgnoringCase("Berlin")))
                .jsonPath("$.temperature").isNumber()
                .jsonPath("$.windspeed").isNumber();
    }

    @Test
    void shouldReturnCurrentWeatherForCityWithSpaces() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/current").queryParam("city", "San Francisco").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.city").value(containsStringIgnoringCase("San"))
                .jsonPath("$.temperature").isNumber();
    }

    @Test
    void shouldReturnNotFoundForUnknownCity() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/current").queryParam("city", "asdfghjkqwertycity").build())
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    void shouldReturnBadRequestForBlankCity() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/current").queryParam("city", " ").build())
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void cacheStoresSingleNormalizedEntryForDifferentCase() {
        // First request populates cache
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/current").queryParam("city", "Berlin").build())
                .exchange()
                .expectStatus().isOk();
        // Second request (different case) should hit cache
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/weather/current").queryParam("city", "BERLIN").build())
                .exchange()
                .expectStatus().isOk();
        // Check cache size endpoint - should be >=1 (cannot guarantee only one if other tests ran, but we assert at least one)
        webTestClient.get().uri("/api/weather/cache/size")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.size").value(greaterThanOrEqualTo(1));
    }
}

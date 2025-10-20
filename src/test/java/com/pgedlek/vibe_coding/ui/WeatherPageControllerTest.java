package com.pgedlek.vibe_coding.ui;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class WeatherPageControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void formRendersWithoutCity() {
        webTestClient.get().uri("/weather")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.contains("Weather Lookup"));
                    assertTrue(html.contains("name=\"city\""));
                });
    }

    @Test
    void showsWeatherForBerlin() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/weather").queryParam("city", "Berlin").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> {
                    assertTrue(html.toLowerCase().contains("berlin"));
                    assertTrue(html.matches("(?s).*Temperature.*</th>.*<td>[-0-9.]+</td>.*"));
                });
    }

    @Test
    void showsErrorForUnknownCity() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/weather").queryParam("city", "asdfghjkqwertycity").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertTrue(html.toLowerCase().contains("city not found")));
    }

    @Test
    void showsErrorForBlankCity() {
        webTestClient.get().uri(uriBuilder -> uriBuilder.path("/weather").queryParam("city", " ").build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .value(html -> assertTrue(html.toLowerCase().contains("city must not be blank")));
    }
}

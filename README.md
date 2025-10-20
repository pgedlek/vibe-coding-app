# Vibe Coding Weather App (Backend)

Reactive Spring Boot application providing current weather data for a given city using the free [Open-Meteo](https://open-meteo.com/) APIs (no API key required).

## Endpoint

GET /api/weather/current?city={cityName}

Response JSON:
```
{
  "city": "Berlin",
  "latitude": 52.52,
  "longitude": 13.41,
  "temperature": 11.8,
  "windspeed": 8.9,
  "winddirection": 260,
  "time": "2025-10-20T20:00"
}
```

Errors:
- 400 if city parameter is blank
- 404 if city not found
- 500 for unexpected errors

## Run

```bash
mvn spring-boot:run
```

Then test:
```bash
curl 'http://localhost:8080/api/weather/current?city=Berlin'
```

## Tests
Run the test suite:
```bash
mvn test
```

## Next Steps (Frontend with Thymeleaf)
- Create a form to input city
- Display weather result or error messages
- Add basic styling

## Notes
- External calls use Open-Meteo Geocoding and Forecast API.
- For production, consider caching responses and adding retries/timeouts.
package com.pgedlek.vibe_coding.weather.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeocodingResult(
        String name,
        double latitude,
        double longitude,
        String country
) {}


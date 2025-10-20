# Vibe Coding Weather App (Backend + UI)

Reactive Spring Boot application providing current weather data for a given city using the free [Open-Meteo](https://open-meteo.com/) APIs (no API key required). Now includes a simple Thymeleaf UI.

## Endpoints

REST API:
GET /api/weather/current?city={cityName}

UI Pages:
GET / (redirects to /weather)
GET /weather?city={cityName}

## Running
```bash
mvn spring-boot:run
```
Open the UI in a browser:
```
http://localhost:8080/weather
```
Or just:
```
http://localhost:8080/
```

## Sample API Response
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

## Tests
Run all tests:
```bash
mvn test
```

## UI Behavior
- Blank city submission: displays inline error message.
- Unknown city: displays error returned from backend.
- Successful lookup: renders table with weather data and retains city in input field.

## Tech Notes
- WebFlux + Thymeleaf used (server-side rendering, non-blocking external calls via WebClient).
- Basic timeouts configured in `WebClientConfig`.
- Consider adding caching & resilience (retry/backoff) for production.

## Postman
Import the collection in `postman/WeatherApp.postman_collection.json` and environment `postman/WeatherLocal.postman_environment.json`.

## Next Ideas
- Add client-side enhancement (fetch without full page reload).
- Add metric endpoints with Micrometer.
- Add Dockerfile for containerization.

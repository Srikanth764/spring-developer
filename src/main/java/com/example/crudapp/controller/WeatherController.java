package com.example.crudapp.controller;

import com.example.crudapp.dto.WeatherDto;
import com.example.crudapp.service.WeatherService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Weather operations.
 * Provides endpoints for weather forecast retrieval.
 */
@RestController
@RequestMapping("/api/weather")
@CrossOrigin(origins = "*")
public class WeatherController {

    private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Get 7-day weather forecast for a given zip code.
     * 
     * @param zipCode the zip code to get weather for
     * @return ResponseEntity containing WeatherDto with 7-day forecast
     */
    @GetMapping("/forecast/{zipCode}")
    public ResponseEntity<WeatherDto> getSevenDayForecast(@PathVariable String zipCode) {
        logger.info("GET /api/weather/forecast/{} - Fetching 7-day weather forecast", zipCode);
        
        try {
            WeatherDto weatherForecast = weatherService.getSevenDayForecast(zipCode);
            logger.info("Successfully retrieved weather forecast for zip code: {}", zipCode);
            return ResponseEntity.ok(weatherForecast);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid zip code provided: {}", zipCode);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving weather forecast for zip code: {}", zipCode, e);
            throw e;
        }
    }

    /**
     * Get current weather for a given zip code.
     * 
     * @param zipCode the zip code to get current weather for
     * @return ResponseEntity containing current day's weather
     */
    @GetMapping("/current/{zipCode}")
    public ResponseEntity<WeatherDto.DailyForecast> getCurrentWeather(@PathVariable String zipCode) {
        logger.info("GET /api/weather/current/{} - Fetching current weather", zipCode);
        
        try {
            WeatherDto weatherForecast = weatherService.getSevenDayForecast(zipCode);
            WeatherDto.DailyForecast currentWeather = weatherForecast.getForecast().get(0);
            logger.info("Successfully retrieved current weather for zip code: {}", zipCode);
            return ResponseEntity.ok(currentWeather);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid zip code provided: {}", zipCode);
            throw e;
        } catch (Exception e) {
            logger.error("Error retrieving current weather for zip code: {}", zipCode, e);
            throw e;
        }
    }
}

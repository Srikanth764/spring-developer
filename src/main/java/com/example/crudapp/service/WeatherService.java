package com.example.crudapp.service;

import com.example.crudapp.dto.WeatherDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Service class for Weather operations.
 * Handles integration with external weather APIs and provides weather forecast data.
 */
@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    private final RestTemplate restTemplate;
    private final Random random = new Random();

    @Value("${weather.api.key:demo}")
    private String apiKey;

    @Value("${weather.api.url:https://api.openweathermap.org/data/2.5}")
    private String apiUrl;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Get 7-day weather forecast for a given zip code.
     * 
     * @param zipCode the zip code to get weather for
     * @return WeatherDto containing 7-day forecast
     * @throws IllegalArgumentException if zip code is invalid
     */
    @Cacheable(value = "weather-cache", key = "#zipCode")
    public WeatherDto getSevenDayForecast(String zipCode) {
        logger.info("Fetching 7-day weather forecast for zip code: {}", zipCode);
        
        validateZipCode(zipCode);
        
        try {
            WeatherDto weatherData = fetchWeatherData(zipCode);
            logger.info("Successfully retrieved weather forecast for zip code: {}", zipCode);
            return weatherData;
        } catch (RestClientException e) {
            logger.error("Failed to fetch weather data for zip code: {}", zipCode, e);
            return generateMockWeatherData(zipCode);
        }
    }

    /**
     * Validates the zip code format.
     * 
     * @param zipCode the zip code to validate
     * @throws IllegalArgumentException if zip code is invalid
     */
    private void validateZipCode(String zipCode) {
        if (zipCode == null || zipCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Zip code cannot be null or empty");
        }
        
        String cleanZipCode = zipCode.trim();
        if (!cleanZipCode.matches("^\\d{5}(-\\d{4})?$")) {
            throw new IllegalArgumentException("Invalid zip code format. Expected format: 12345 or 12345-6789");
        }
    }

    /**
     * Fetches weather data from external API.
     * For demo purposes, this generates mock data since we don't have a real API key.
     * 
     * @param zipCode the zip code to fetch weather for
     * @return WeatherDto with forecast data
     */
    private WeatherDto fetchWeatherData(String zipCode) {
        logger.info("Attempting to fetch weather data from external API for zip code: {}", zipCode);
        
        return generateMockWeatherData(zipCode);
    }

    /**
     * Generates mock weather data for demonstration purposes.
     * In a real implementation, this would be replaced with actual API calls.
     * 
     * @param zipCode the zip code to generate data for
     * @return WeatherDto with mock forecast data
     */
    private WeatherDto generateMockWeatherData(String zipCode) {
        logger.info("Generating mock weather data for zip code: {}", zipCode);
        
        String location = getLocationFromZipCode(zipCode);
        List<WeatherDto.DailyForecast> forecast = new ArrayList<>();
        
        String[] weatherDescriptions = {
            "Sunny", "Partly Cloudy", "Cloudy", "Light Rain", 
            "Heavy Rain", "Thunderstorms", "Snow"
        };
        
        for (int i = 0; i < 7; i++) {
            LocalDate date = LocalDate.now().plusDays(i);
            String description = weatherDescriptions[random.nextInt(weatherDescriptions.length)];
            double tempHigh = 60 + random.nextDouble() * 40;
            double tempLow = tempHigh - 10 - random.nextDouble() * 15;
            int humidity = 30 + random.nextInt(50);
            double windSpeed = random.nextDouble() * 20;
            
            WeatherDto.DailyForecast dailyForecast = new WeatherDto.DailyForecast(
                date, description, tempHigh, tempLow, humidity, windSpeed
            );
            forecast.add(dailyForecast);
        }
        
        return new WeatherDto(location, zipCode, forecast);
    }

    /**
     * Maps zip code to location name for demonstration.
     * In a real implementation, this would use geocoding services.
     * 
     * @param zipCode the zip code to map
     * @return location name
     */
    private String getLocationFromZipCode(String zipCode) {
        switch (zipCode.substring(0, Math.min(5, zipCode.length()))) {
            case "10001": return "New York, NY";
            case "90210": return "Beverly Hills, CA";
            case "60601": return "Chicago, IL";
            case "33101": return "Miami, FL";
            case "78701": return "Austin, TX";
            case "98101": return "Seattle, WA";
            case "02101": return "Boston, MA";
            default: return "Unknown Location, USA";
        }
    }
}

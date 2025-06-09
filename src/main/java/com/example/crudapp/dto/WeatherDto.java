package com.example.crudapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for Weather information.
 * Contains weather forecast data for a specific location.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WeatherDto {

    private String location;
    private String zipCode;
    private List<DailyForecast> forecast;

    /**
     * Represents a single day's weather forecast.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyForecast {
        private LocalDate date;
        private String description;
        private double temperatureHigh;
        private double temperatureLow;
        private int humidity;
        private double windSpeed;
    }
}

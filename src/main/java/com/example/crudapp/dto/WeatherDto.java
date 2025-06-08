package com.example.crudapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.List;

/**
 * Data Transfer Object for Weather information.
 * Contains weather forecast data for a specific location.
 */
public class WeatherDto {

    private String location;
    private String zipCode;
    private List<DailyForecast> forecast;

    public WeatherDto() {}

    public WeatherDto(String location, String zipCode, List<DailyForecast> forecast) {
        this.location = location;
        this.zipCode = zipCode;
        this.forecast = forecast;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public List<DailyForecast> getForecast() {
        return forecast;
    }

    public void setForecast(List<DailyForecast> forecast) {
        this.forecast = forecast;
    }

    /**
     * Represents a single day's weather forecast.
     */
    public static class DailyForecast {
        private LocalDate date;
        private String description;
        private double temperatureHigh;
        private double temperatureLow;
        private int humidity;
        private double windSpeed;

        public DailyForecast() {}

        public DailyForecast(LocalDate date, String description, double temperatureHigh, 
                           double temperatureLow, int humidity, double windSpeed) {
            this.date = date;
            this.description = description;
            this.temperatureHigh = temperatureHigh;
            this.temperatureLow = temperatureLow;
            this.humidity = humidity;
            this.windSpeed = windSpeed;
        }

        public LocalDate getDate() {
            return date;
        }

        public void setDate(LocalDate date) {
            this.date = date;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public double getTemperatureHigh() {
            return temperatureHigh;
        }

        public void setTemperatureHigh(double temperatureHigh) {
            this.temperatureHigh = temperatureHigh;
        }

        public double getTemperatureLow() {
            return temperatureLow;
        }

        public void setTemperatureLow(double temperatureLow) {
            this.temperatureLow = temperatureLow;
        }

        public int getHumidity() {
            return humidity;
        }

        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }

        public double getWindSpeed() {
            return windSpeed;
        }

        public void setWindSpeed(double windSpeed) {
            this.windSpeed = windSpeed;
        }
    }
}

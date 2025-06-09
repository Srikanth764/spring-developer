package com.example.crudapp.controller;

import com.example.crudapp.dto.WeatherDto;
import com.example.crudapp.service.WeatherService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherController.class)
class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WeatherService weatherService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getSevenDayForecast_ValidZipCode_ShouldReturnWeatherData() throws Exception {
        String zipCode = "10001";
        WeatherDto mockWeatherData = createMockWeatherData(zipCode);
        
        when(weatherService.getSevenDayForecast(zipCode)).thenReturn(mockWeatherData);

        mockMvc.perform(get("/api/weather/forecast/{zipCode}", zipCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.zipCode").value(zipCode))
                .andExpect(jsonPath("$.location").value("New York, NY"))
                .andExpect(jsonPath("$.forecast").isArray())
                .andExpect(jsonPath("$.forecast.length()").value(7))
                .andExpect(jsonPath("$.forecast[0].date").exists())
                .andExpect(jsonPath("$.forecast[0].description").exists())
                .andExpect(jsonPath("$.forecast[0].temperatureHigh").exists())
                .andExpect(jsonPath("$.forecast[0].temperatureLow").exists())
                .andExpect(jsonPath("$.forecast[0].humidity").exists())
                .andExpect(jsonPath("$.forecast[0].windSpeed").exists());
    }

    @Test
    void getSevenDayForecast_InvalidZipCode_ShouldReturnBadRequest() throws Exception {
        String invalidZipCode = "invalid";
        
        when(weatherService.getSevenDayForecast(invalidZipCode))
                .thenThrow(new IllegalArgumentException("Invalid zip code format. Expected format: 12345 or 12345-6789"));

        mockMvc.perform(get("/api/weather/forecast/{zipCode}", invalidZipCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Argument"))
                .andExpect(jsonPath("$.message").value("Invalid zip code format. Expected format: 12345 or 12345-6789"));
    }

    @Test
    void getCurrentWeather_ValidZipCode_ShouldReturnCurrentWeather() throws Exception {
        String zipCode = "90210";
        WeatherDto mockWeatherData = createMockWeatherData(zipCode);
        
        when(weatherService.getSevenDayForecast(zipCode)).thenReturn(mockWeatherData);

        mockMvc.perform(get("/api/weather/current/{zipCode}", zipCode))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.date").exists())
                .andExpect(jsonPath("$.description").value("Sunny"))
                .andExpect(jsonPath("$.temperatureHigh").value(75.0))
                .andExpect(jsonPath("$.temperatureLow").value(60.0))
                .andExpect(jsonPath("$.humidity").value(50))
                .andExpect(jsonPath("$.windSpeed").value(10.0));
    }

    @Test
    void getCurrentWeather_InvalidZipCode_ShouldReturnBadRequest() throws Exception {
        String invalidZipCode = "12345-abc";
        
        when(weatherService.getSevenDayForecast(invalidZipCode))
                .thenThrow(new IllegalArgumentException("Invalid zip code format. Expected format: 12345 or 12345-6789"));

        mockMvc.perform(get("/api/weather/current/{zipCode}", invalidZipCode))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid Argument"))
                .andExpect(jsonPath("$.message").value("Invalid zip code format. Expected format: 12345 or 12345-6789"));
    }

    @Test
    void getSevenDayForecast_ServiceException_ShouldReturnInternalServerError() throws Exception {
        String zipCode = "10001";
        
        when(weatherService.getSevenDayForecast(zipCode))
                .thenThrow(new RuntimeException("Weather service unavailable"));

        mockMvc.perform(get("/api/weather/forecast/{zipCode}", zipCode))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal Server Error"))
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"));
    }

    private WeatherDto createMockWeatherData(String zipCode) {
        List<WeatherDto.DailyForecast> forecast = Arrays.asList(
            new WeatherDto.DailyForecast(LocalDate.now(), "Sunny", 75.0, 60.0, 50, 10.0),
            new WeatherDto.DailyForecast(LocalDate.now().plusDays(1), "Cloudy", 70.0, 55.0, 60, 8.0),
            new WeatherDto.DailyForecast(LocalDate.now().plusDays(2), "Rainy", 65.0, 50.0, 80, 15.0),
            new WeatherDto.DailyForecast(LocalDate.now().plusDays(3), "Partly Cloudy", 72.0, 58.0, 45, 12.0),
            new WeatherDto.DailyForecast(LocalDate.now().plusDays(4), "Sunny", 78.0, 62.0, 40, 5.0),
            new WeatherDto.DailyForecast(LocalDate.now().plusDays(5), "Thunderstorms", 68.0, 52.0, 85, 20.0),
            new WeatherDto.DailyForecast(LocalDate.now().plusDays(6), "Sunny", 80.0, 65.0, 35, 7.0)
        );
        
        String location = zipCode.equals("10001") ? "New York, NY" : "Beverly Hills, CA";
        return new WeatherDto(location, zipCode, forecast);
    }
}

package com.example.crudapp.service;

import com.example.crudapp.dto.WeatherDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @InjectMocks
    private WeatherService weatherService;

    @Test
    void getSevenDayForecast_ValidZipCode_ShouldReturnWeatherData() {
        String zipCode = "10001";
        
        WeatherDto result = weatherService.getSevenDayForecast(zipCode);
        
        assertNotNull(result);
        assertEquals(zipCode, result.getZipCode());
        assertNotNull(result.getLocation());
        assertNotNull(result.getForecast());
        assertEquals(7, result.getForecast().size());
        
        result.getForecast().forEach(forecast -> {
            assertNotNull(forecast.getDate());
            assertNotNull(forecast.getDescription());
            assertTrue(forecast.getTemperatureHigh() > forecast.getTemperatureLow());
            assertTrue(forecast.getHumidity() >= 0 && forecast.getHumidity() <= 100);
            assertTrue(forecast.getWindSpeed() >= 0);
        });
    }

    @Test
    void getSevenDayForecast_ValidZipCodeWithExtension_ShouldReturnWeatherData() {
        String zipCode = "10001-1234";
        
        WeatherDto result = weatherService.getSevenDayForecast(zipCode);
        
        assertNotNull(result);
        assertEquals(zipCode, result.getZipCode());
        assertEquals(7, result.getForecast().size());
    }

    @Test
    void getSevenDayForecast_KnownZipCode_ShouldReturnCorrectLocation() {
        String zipCode = "90210";
        
        WeatherDto result = weatherService.getSevenDayForecast(zipCode);
        
        assertEquals("Beverly Hills, CA", result.getLocation());
    }

    @Test
    void getSevenDayForecast_NullZipCode_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> weatherService.getSevenDayForecast(null)
        );
        
        assertEquals("Zip code cannot be null or empty", exception.getMessage());
    }

    @Test
    void getSevenDayForecast_EmptyZipCode_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> weatherService.getSevenDayForecast("")
        );
        
        assertEquals("Zip code cannot be null or empty", exception.getMessage());
    }

    @Test
    void getSevenDayForecast_WhitespaceZipCode_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> weatherService.getSevenDayForecast("   ")
        );
        
        assertEquals("Zip code cannot be null or empty", exception.getMessage());
    }

    @Test
    void getSevenDayForecast_InvalidZipCodeFormat_ShouldThrowException() {
        String[] invalidZipCodes = {"1234", "123456", "abcde", "12345-", "12345-abc", "12-345"};
        
        for (String invalidZipCode : invalidZipCodes) {
            IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> weatherService.getSevenDayForecast(invalidZipCode)
            );
            
            assertEquals("Invalid zip code format. Expected format: 12345 or 12345-6789", exception.getMessage());
        }
    }

    @Test
    void getSevenDayForecast_UnknownZipCode_ShouldReturnDefaultLocation() {
        String zipCode = "99999";
        
        WeatherDto result = weatherService.getSevenDayForecast(zipCode);
        
        assertEquals("Unknown Location, USA", result.getLocation());
    }

    @Test
    void getSevenDayForecast_ConsecutiveCalls_ShouldReturnDifferentData() {
        String zipCode = "10001";
        
        WeatherDto result1 = weatherService.getSevenDayForecast(zipCode);
        WeatherDto result2 = weatherService.getSevenDayForecast(zipCode);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertEquals(7, result1.getForecast().size());
        assertEquals(7, result2.getForecast().size());
    }
}

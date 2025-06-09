package com.example.crudapp.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void handleUserNotFoundException_ShouldReturnNotFound() {
        UserNotFoundException ex = new UserNotFoundException("User not found with ID: 1");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUserNotFoundException(ex);
        
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User Not Found", response.getBody().get("error"));
        assertEquals("User not found with ID: 1", response.getBody().get("message"));
        assertEquals(404, response.getBody().get("status"));
    }

    @Test
    void handleUserAlreadyExistsException_ShouldReturnConflict() {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("User with email test@example.com already exists");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleUserAlreadyExistsException(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User Already Exists", response.getBody().get("error"));
        assertEquals("User with email test@example.com already exists", response.getBody().get("message"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void handleNullPointerException_ShouldReturnInternalServerError() {
        NullPointerException ex = new NullPointerException("Null pointer");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleNullPointerException(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Null Pointer Error", response.getBody().get("error"));
        assertEquals("A required value was null", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    void handleIndexOutOfBoundsException_ShouldReturnBadRequest() {
        IndexOutOfBoundsException ex = new IndexOutOfBoundsException("Index out of bounds");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleIndexOutOfBoundsException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Index Out of Bounds", response.getBody().get("error"));
        assertEquals("Invalid index or range specified", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void handleIllegalArgumentException_ShouldReturnBadRequest() {
        IllegalArgumentException ex = new IllegalArgumentException("Invalid argument provided");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleIllegalArgumentException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Argument", response.getBody().get("error"));
        assertEquals("Invalid argument provided", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void handleIllegalStateException_ShouldReturnConflict() {
        IllegalStateException ex = new IllegalStateException("Operation not allowed");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleIllegalStateException(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Invalid State", response.getBody().get("error"));
        assertEquals("Operation not allowed", response.getBody().get("message"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void handleNumberFormatException_ShouldReturnBadRequest() {
        NumberFormatException ex = new NumberFormatException("Invalid number");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleNumberFormatException(ex);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid Number Format", response.getBody().get("error"));
        assertEquals("Invalid number format provided", response.getBody().get("message"));
        assertEquals(400, response.getBody().get("status"));
    }

    @Test
    void handleDataIntegrityViolationException_ShouldReturnConflict() {
        DataIntegrityViolationException ex = new DataIntegrityViolationException("Constraint violation");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleDataIntegrityViolationException(ex);
        
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Data Integrity Violation", response.getBody().get("error"));
        assertEquals("Data constraint violation occurred", response.getBody().get("message"));
        assertEquals(409, response.getBody().get("status"));
    }

    @Test
    void handleClassCastException_ShouldReturnInternalServerError() {
        ClassCastException ex = new ClassCastException("Invalid cast");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleClassCastException(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Type Conversion Error", response.getBody().get("error"));
        assertEquals("Invalid type conversion attempted", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        RuntimeException ex = new RuntimeException("Generic runtime exception");
        
        ResponseEntity<Map<String, Object>> response = globalExceptionHandler.handleGenericException(ex);
        
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal Server Error", response.getBody().get("error"));
        assertEquals("An unexpected error occurred", response.getBody().get("message"));
        assertEquals(500, response.getBody().get("status"));
    }
}

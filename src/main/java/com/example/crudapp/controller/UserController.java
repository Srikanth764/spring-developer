package com.example.crudapp.controller;

import com.example.crudapp.dto.UserDto;
import com.example.crudapp.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for User management operations.
 * Provides endpoints for CRUD operations on User entities.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user.
     * 
     * @param userDto the user data to create
     * @return ResponseEntity containing the created user
     */
    @PostMapping
    public ResponseEntity<UserDto> createUser(@Valid @RequestBody UserDto userDto) {
        logger.info("POST /api/users - Creating user with email: {}", userDto.getEmail());
        
        UserDto createdUser = userService.createUser(userDto);
        
        logger.info("POST /api/users - User created successfully with ID: {}", createdUser.getId());
        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    /**
     * Get all users.
     * 
     * @return ResponseEntity containing list of all users
     */
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        logger.info("GET /api/users - Retrieving all users");
        
        List<UserDto> users = userService.getAllUsers();
        
        logger.info("GET /api/users - Retrieved {} users", users.size());
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    /**
     * Get user by ID.
     * 
     * @param id the user ID
     * @return ResponseEntity containing the user
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        logger.info("GET /api/users/{} - Retrieving user", id);
        
        UserDto user = userService.getUserById(id);
        
        logger.info("GET /api/users/{} - User retrieved successfully", id);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    /**
     * Update an existing user.
     * 
     * @param id the user ID to update
     * @param userDto the updated user data
     * @return ResponseEntity containing the updated user
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @Valid @RequestBody UserDto userDto) {
        logger.info("PUT /api/users/{} - Updating user", id);
        
        UserDto updatedUser = userService.updateUser(id, userDto);
        
        logger.info("PUT /api/users/{} - User updated successfully", id);
        return new ResponseEntity<>(updatedUser, HttpStatus.OK);
    }

    /**
     * Delete a user.
     * 
     * @param id the user ID to delete
     * @return ResponseEntity with no content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        logger.info("DELETE /api/users/{} - Deleting user", id);
        
        userService.deleteUser(id);
        
        logger.info("DELETE /api/users/{} - User deleted successfully", id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

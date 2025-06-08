package com.example.crudapp.service;

import com.example.crudapp.dto.UserDto;
import com.example.crudapp.entity.User;
import com.example.crudapp.exception.UserAlreadyExistsException;
import com.example.crudapp.exception.UserNotFoundException;
import com.example.crudapp.mapper.UserMapper;
import com.example.crudapp.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class for User management operations.
 * Contains business logic for CRUD operations on User entities.
 */
@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    /**
     * Create a new user in the system.
     * 
     * @param userDto the user data to create
     * @return the created user as DTO
     * @throws UserAlreadyExistsException if user with email already exists
     */
    public UserDto createUser(UserDto userDto) {
        logger.info("Creating new user with email: {}", userDto.getEmail());
        
        if (userRepository.existsByEmail(userDto.getEmail())) {
            logger.warn("User creation failed - email already exists: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }

        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        
        logger.info("User created successfully with ID: {}", savedUser.getId());
        return userMapper.toDto(savedUser);
    }

    /**
     * Retrieve all users from the system.
     * 
     * @return list of all users as DTOs
     */
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        logger.info("Retrieving all users");
        
        List<User> users = userRepository.findAll();
        List<UserDto> userDtos = users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        
        logger.info("Retrieved {} users", userDtos.size());
        return userDtos;
    }

    /**
     * Retrieve a user by their ID.
     * 
     * @param id the user ID to search for
     * @return the user as DTO
     * @throws UserNotFoundException if user is not found
     */
    @Transactional(readOnly = true)
    public UserDto getUserById(Long id) {
        logger.info("Retrieving user with ID: {}", id);
        
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });
        
        logger.info("User retrieved successfully: {}", user.getEmail());
        return userMapper.toDto(user);
    }

    /**
     * Update an existing user in the system.
     * 
     * @param id the ID of the user to update
     * @param userDto the updated user data
     * @return the updated user as DTO
     * @throws UserNotFoundException if user is not found
     * @throws UserAlreadyExistsException if email is already taken by another user
     */
    public UserDto updateUser(Long id, UserDto userDto) {
        logger.info("Updating user with ID: {}", id);
        
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found for update with ID: {}", id);
                    return new UserNotFoundException("User not found with ID: " + id);
                });

        if (!existingUser.getEmail().equals(userDto.getEmail()) && 
            userRepository.existsByEmail(userDto.getEmail())) {
            logger.warn("User update failed - email already exists: {}", userDto.getEmail());
            throw new UserAlreadyExistsException("User with email " + userDto.getEmail() + " already exists");
        }

        existingUser.setName(userDto.getName());
        existingUser.setEmail(userDto.getEmail());
        existingUser.setAge(userDto.getAge());
        
        User updatedUser = userRepository.save(existingUser);
        
        logger.info("User updated successfully with ID: {}", updatedUser.getId());
        return userMapper.toDto(updatedUser);
    }

    /**
     * Delete a user from the system.
     * 
     * @param id the ID of the user to delete
     * @throws UserNotFoundException if user is not found
     */
    public void deleteUser(Long id) {
        logger.info("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            logger.warn("User not found for deletion with ID: {}", id);
            throw new UserNotFoundException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {}", id);
    }
}

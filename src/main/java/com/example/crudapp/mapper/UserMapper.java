package com.example.crudapp.mapper;

import com.example.crudapp.dto.UserDto;
import com.example.crudapp.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper class for converting between User entity and UserDto.
 * Provides methods to map data between different layers of the application.
 */
@Component
public class UserMapper {

    /**
     * Convert User entity to UserDto.
     * 
     * @param user the User entity to convert
     * @return UserDto representation of the user
     */
    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getAge());
    }

    /**
     * Convert UserDto to User entity.
     * 
     * @param userDto the UserDto to convert
     * @return User entity representation
     */
    public User toEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        User user = new User(userDto.getName(), userDto.getEmail(), userDto.getAge());
        user.setId(userDto.getId());
        return user;
    }

    /**
     * Update existing User entity with data from UserDto.
     * 
     * @param existingUser the existing User entity to update
     * @param userDto the UserDto containing new data
     */
    public void updateEntityFromDto(User existingUser, UserDto userDto) {
        if (existingUser != null && userDto != null) {
            existingUser.setName(userDto.getName());
            existingUser.setEmail(userDto.getEmail());
            existingUser.setAge(userDto.getAge());
        }
    }
}

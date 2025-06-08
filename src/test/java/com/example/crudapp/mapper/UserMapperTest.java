package com.example.crudapp.mapper;

import com.example.crudapp.dto.UserDto;
import com.example.crudapp.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private UserMapper userMapper;
    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        userMapper = new UserMapper();
        
        testUser = new User(1L, "John Doe", "john@example.com", 25);
        
        testUserDto = new UserDto(1L, "John Doe", "john@example.com", 25);
    }

    @Test
    void toDto_ValidUser_ReturnsUserDto() {
        UserDto result = userMapper.toDto(testUser);

        assertNotNull(result);
        assertEquals(testUser.getId(), result.getId());
        assertEquals(testUser.getName(), result.getName());
        assertEquals(testUser.getEmail(), result.getEmail());
        assertEquals(testUser.getAge(), result.getAge());
    }

    @Test
    void toDto_NullUser_ReturnsNull() {
        UserDto result = userMapper.toDto(null);

        assertNull(result);
    }

    @Test
    void toEntity_ValidUserDto_ReturnsUser() {
        User result = userMapper.toEntity(testUserDto);

        assertNotNull(result);
        assertEquals(testUserDto.getId(), result.getId());
        assertEquals(testUserDto.getName(), result.getName());
        assertEquals(testUserDto.getEmail(), result.getEmail());
        assertEquals(testUserDto.getAge(), result.getAge());
    }

    @Test
    void toEntity_NullUserDto_ReturnsNull() {
        User result = userMapper.toEntity(null);

        assertNull(result);
    }

    @Test
    void updateEntityFromDto_ValidInputs_UpdatesEntity() {
        UserDto updateDto = new UserDto(1L, "Jane Doe", "jane@example.com", 30);

        userMapper.updateEntityFromDto(testUser, updateDto);

        assertEquals("Jane Doe", testUser.getName());
        assertEquals("jane@example.com", testUser.getEmail());
        assertEquals(30, testUser.getAge());
    }

    @Test
    void updateEntityFromDto_NullInputs_DoesNothing() {
        String originalName = testUser.getName();
        String originalEmail = testUser.getEmail();
        Integer originalAge = testUser.getAge();

        userMapper.updateEntityFromDto(null, testUserDto);
        userMapper.updateEntityFromDto(testUser, null);

        assertEquals(originalName, testUser.getName());
        assertEquals(originalEmail, testUser.getEmail());
        assertEquals(originalAge, testUser.getAge());
    }
}

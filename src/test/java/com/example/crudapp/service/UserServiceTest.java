package com.example.crudapp.service;

import com.example.crudapp.dto.UserDto;
import com.example.crudapp.entity.User;
import com.example.crudapp.exception.UserAlreadyExistsException;
import com.example.crudapp.exception.UserNotFoundException;
import com.example.crudapp.mapper.UserMapper;
import com.example.crudapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserDto testUserDto;

    @BeforeEach
    void setUp() {
        testUser = new User("John Doe", "john@example.com", 25);
        testUser.setId(1L);
        
        testUserDto = new UserDto(1L, "John Doe", "john@example.com", 25);
    }

    @Test
    void createUser_Success() {
        when(userRepository.existsByEmail(testUserDto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(testUserDto)).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.createUser(testUserDto);

        assertNotNull(result);
        assertEquals(testUserDto.getEmail(), result.getEmail());
        verify(userRepository).existsByEmail(testUserDto.getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    void createUser_EmailAlreadyExists_ThrowsException() {
        when(userRepository.existsByEmail(testUserDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(testUserDto));
        verify(userRepository).existsByEmail(testUserDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(testUser);
        List<UserDto> userDtos = Arrays.asList(testUserDto);
        
        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        List<UserDto> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testUserDto.getEmail(), result.get(0).getEmail());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDto(testUser)).thenReturn(testUserDto);

        UserDto result = userService.getUserById(1L);

        assertNotNull(result);
        assertEquals(testUserDto.getEmail(), result.getEmail());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(1L));
        verify(userRepository).findById(1L);
    }

    @Test
    void updateUser_Success() {
        UserDto updateDto = new UserDto(1L, "Jane Doe", "john@example.com", 30);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(userMapper.toDto(testUser)).thenReturn(updateDto);

        UserDto result = userService.updateUser(1L, updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getName(), result.getName());
        verify(userRepository).findById(1L);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void updateUser_UserNotFound_ThrowsException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(1L, testUserDto));
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUser_EmailAlreadyExists_ThrowsException() {
        UserDto updateDto = new UserDto(1L, "Jane Doe", "jane@example.com", 30);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmail(updateDto.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.updateUser(1L, updateDto));
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail(updateDto.getEmail());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteUser(1L);

        verify(userRepository).existsById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_UserNotFound_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(1L));
        verify(userRepository).existsById(1L);
        verify(userRepository, never()).deleteById(anyLong());
    }
}

package com.virtuallibrary.services.impl;

import com.virtuallibrary.entities.Role;
import com.virtuallibrary.entities.User;
import com.virtuallibrary.repositories.UserRepository;
import com.virtuallibrary.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserDetailsServiceImplTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    UserService userService;
    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Test
    void testLoadUserByUsername() {
        // Given
        User user = new User(1L, "Test", "Test", "7777777777", Set.of(new Role(1L, "ROLE_USER")), "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        when(userService.findByEmail("test@test.com")).thenReturn(Optional.of(user));
//        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        // When
        UserDetails userDetails = userDetailsService.loadUserByUsername("test@test.com");

        // Then
        assertEquals("test@test.com", userDetails.getUsername());
        assertEquals(user.getPassword(), userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        verify(userService, times(1)).findByEmail("test@test.com");
//        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void testLoadUserByUsername_UserNotFound() {
        // Given
        String username = "notexists@test.com";

        // Configure mock repository behavior
        when(userService.findByEmail(username)).thenReturn(Optional.empty());
//        when(userRepository.findByEmail(username)).thenReturn(Optional.empty());

        // When Then
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(username);
        });
        assertEquals(String.format("Email %s not exist", username), exception.getMessage());
        verify(userService, times(1)).findByEmail(username);
//        verify(userRepository, times(1)).findByEmail(username);
    }
}
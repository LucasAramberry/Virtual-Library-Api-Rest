package com.virtuallibrary.services;

import com.virtuallibrary.entities.User;
import com.virtuallibrary.repositories.RoleRepository;
import com.virtuallibrary.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class UserServiceTest {

    @MockBean
    UserRepository userRepository;
    @MockBean
    RoleRepository roleRepository;
    @Autowired
    UserService userService;

    @Test
    void testFindAll() {
        // Given
        List<User> userList = Arrays.asList(
                new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now()),
                new User(2L, "Test", "Second", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now())
        );

        when(userRepository.findAll()).thenReturn(userList);

        // When
        List<User> users = userService.findAll();

        // Then
        assertEquals(2, users.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        User user = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // When
        User u = userService.findById(1L).orElseThrow();

        // Then
        assertNotNull(u);
        assertEquals("Test", u.getName());
        assertEquals("First", u.getLastName());
        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        // Given
        User user = new User(null, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        when(userRepository.save(user)).then(invocation -> {
            User p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        User save = userService.save(user);

        // Then
        assertNotNull(save);
        assertEquals("Test", save.getName());
        assertEquals(1, save.getId());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testUpdate() {
        // Given
        User user = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        when(userRepository.save(user)).then(invocation -> {
            User p = invocation.getArgument(0);
            p.setName("User Test");
            return p;
        });

        // When
        User save = userService.save(user);

        // Then
        assertNotNull(save);
        assertEquals("User Test", save.getName());
        assertEquals(1, save.getId());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        // Given
        User user = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        // When
        userService.delete(user);

        // Then
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void testFindByEmail() {
        // Given
        User user = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        when(userRepository.findByEmail("test@test.com")).thenReturn(Optional.of(user));

        // When
        Optional<User> p = userService.findByEmail("test@test.com");

        // Then
        assertTrue(p.isPresent());
        assertEquals("Test", p.get().getName());
        assertEquals("test@test.com", p.get().getEmail());
        assertEquals(1, p.get().getId());
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void testExistsByEmail() {
        // Given
        User user = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        // When
        boolean exists = userService.existsByEmail("test@test.com");

        // Then
        assertTrue(exists);
        verify(userRepository).existsByEmail("test@test.com");
    }

    @Test
    void testFindByEnabledTrue() {
        // Given
        List<User> userList = Arrays.asList(
                new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now()),
                new User(2L, "Test", "Second", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), false, LocalDateTime.now())
        );

        when(userRepository.findByEnabledTrue()).thenReturn(Collections.singletonList(userList.get(0)));

        // When
        List<User> users = userService.findByEnabledTrue();

        // Then
        assertEquals(1, users.size());
        verify(userRepository, times(1)).findByEnabledTrue();
    }

    @Test
    void testFindByEnabledFalse() {
        // Given
        List<User> userList = Arrays.asList(
                new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now()),
                new User(2L, "Test", "Second", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), false, LocalDateTime.now())
        );

        when(userRepository.findByEnabledFalse()).thenReturn(Collections.singletonList(userList.get(1)));

        // When
        List<User> users = userService.findByEnabledFalse();

        // Then
        assertEquals(1, users.size());
        verify(userRepository, times(1)).findByEnabledFalse();
    }

    @Test
    void testActivate() {
        // Given
        User user = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), false, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).then(invocation -> {
            User p = invocation.getArgument(0);
            p.setEnabled(true);
            return p;
        });

        // When
        userService.activate(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any());
    }

    @Test
    void testDeactivate() {
        // Given
        User user = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).then(invocation -> {
            User p = invocation.getArgument(0);
            p.setEnabled(false);
            return p;
        });

        // When
        userService.deactivate(1L);

        // Then
        verify(userRepository).findById(1L);
        verify(userRepository).save(any());
    }
}
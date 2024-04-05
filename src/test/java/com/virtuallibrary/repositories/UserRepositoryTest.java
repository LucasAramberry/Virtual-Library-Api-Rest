package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;

    @Test
    void findByEmail() {
        Optional<User> userOptional = userRepository.findByEmail("lucasaramberry@admin.com");

        assertTrue(userOptional.isPresent());
        assertEquals("lucasaramberry@admin.com", userOptional.orElseThrow().getEmail());

        Optional<User> userOptional2 = userRepository.findByEmail("lucasaramberry@user.com");

        assertTrue(userOptional2.isPresent());
        assertEquals("lucasaramberry@user.com", userOptional2.orElseThrow().getEmail());
    }

    @Test
    void existsByEmail() {
        assertTrue(userRepository.existsByEmail("lucasaramberry@user.com"));
        assertFalse(userRepository.existsByEmail("lucas@aramberry.com"));
    }

    @Test
    void findByEnabledTrue() {
        List<User> userList = userRepository.findByEnabledTrue();

        assertNotNull(userList);
        assertEquals(2, userList.size());
        assertNotEquals(10, userList.size());
    }

    @Test
    void findByEnabledFalse() {
        List<User> userList = userRepository.findByEnabledFalse();

        assertEquals(0, userList.size());
        assertNotEquals(10, userList.size());
    }

    @Test
    void testFindById() {
        Optional<User> userOptional = userRepository.findById(2L);

        assertTrue(userOptional.isPresent());
        assertEquals(2, userOptional.orElseThrow().getId());
    }

    @Test
    void testFindAll() {
        List<User> userList = (List<User>) userRepository.findAll();

        assertTrue(!userList.isEmpty());
        assertEquals(2, userList.size());
    }

    @Test
    void testSave() {
        User user = userRepository.save(User
                .builder()
                        .name("Test")
                        .lastName("Test")
                        .phone("1111111111")
                        .email("test@test.com")
                        .password(new BCryptPasswordEncoder().encode("12345"))
                        .roles(Collections.singleton(roleRepository.findByName("ROLE_USER").orElseThrow()))
                        .createdAt(LocalDateTime.now())
                        .enabled(true)
                .build());

        assertNotNull(user);
        assertEquals("test@test.com",user.getEmail());
        assertTrue(user.isEnabled());
    }

    @Test
    void testUpdate() {
        User user = userRepository.findById(3L).orElseThrow();

        user.setPhone("1231231231");

        User userUpdate = userRepository.save(user);

        assertNotNull(userUpdate);
        assertEquals("1231231231", userUpdate.getPhone());
    }


    @Test
    void testDelete() {
        Optional<User> userOptional = userRepository.findById(1L);

        userRepository.delete(userOptional.orElseThrow());

        assertNull(userRepository.findById(1L).orElse(null));
    }
}
package com.virtuallibrary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.virtuallibrary.dto.UserDto;
import com.virtuallibrary.entities.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    @Order(1)
    void testRegister() throws JsonProcessingException {

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name", "Test");
        userMap.put("lastName", "Test");
        userMap.put("phone", "1111111111");
        userMap.put("email", "test@test.com");
        userMap.put("password", "lucas123");
        userMap.put("matchingPassword", "lucas123");

        String json = objectMapper.writeValueAsString(userMap);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(json, headers);

        ResponseEntity<String> response = client.postForEntity(createUri("/api/user/register"), request, String.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Order(1)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    class sessionRoleUser {

        private String jwtToken;

        @BeforeEach
        void setUp() throws JsonProcessingException {
            if (jwtToken == null) jwtToken = getToken("lucasaramberry@user.com", "123456");
        }

        @Test
        @Order(1)
        void testProfile() {
            // Build authorization headers with JWT token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            // Build the HTTP request with authorization headers
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<UserDto> response = client.exchange(createUri("/api/user/profile/3"), HttpMethod.GET, request, UserDto.class);

            UserDto userDto = response.getBody();

            assertNotNull(userDto);
            assertEquals("test@test.com", userDto.getEmail());
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        }

        @Test
        @Order(2)
        void testUpdateProfile() throws JsonProcessingException {

            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", "Test");
            userMap.put("lastName", "Update");
            userMap.put("phone", "1111111111");
            userMap.put("email", "test@test.com");
            userMap.put("password", "123456");
            userMap.put("matchingPassword", "123456");

            // Build authorization headers with JWT token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            // Build the HTTP request with authorization headers
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(userMap), headers);

            ResponseEntity<String> response = client.exchange(createUri("/api/user/edit-profile/3"), HttpMethod.PUT, request, String.class);

            String responseBody = response.getBody();

            assertNotNull(responseBody);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("Updated profile successfully", responseBody);
        }
    }

    @Order(2)
    @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
    @Nested
    class sessionRoleAdmin {

        private String jwtToken;

        @BeforeEach
        void setUp() throws JsonProcessingException {
            if (jwtToken == null) jwtToken = getToken("lucasaramberry@admin.com", "123456");
        }

        @Test
        @Order(1)
        void testUsers() throws JsonProcessingException {
            // Build authorization headers with JWT token
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + jwtToken);

            // Build the HTTP request with authorization headers
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<User[]> response = client.exchange(createUri("/api/user/list"), HttpMethod.GET, request, User[].class);

            List<User> users = Arrays.asList(response.getBody());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

            assertTrue(users.size() >= 3);
            assertEquals(1L, users.get(0).getId());

            JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(users));

            assertEquals("Lucas", json.get(0).path("name").asText());
            assertEquals("Aramberry", json.get(1).path("lastName").asText());
        }

        @Test
        @Order(2)
        void testDelete() {
            // Build authorization headers with JWT token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + jwtToken);

            // Build the HTTP request with authorization headers
            HttpEntity<String> request = new HttpEntity<>(headers);

            ResponseEntity<String> response = client.exchange(createUri("/api/user/delete/3"), HttpMethod.DELETE, request, String.class);

            String responseBody = response.getBody();

            assertNotNull(responseBody);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals("User delete successfully", responseBody);
        }

    }

    private String createUri(String uri) {
        return "http://localhost:" + port + uri;
    }

    private String getToken(String email, String password) throws JsonProcessingException {
        // Build login credentials
        Map<String, String> credentials = new HashMap<>();
        credentials.put("email", email);
        credentials.put("password", password);

        // Build the login HTTP request
        HttpHeaders loginHeaders = new HttpHeaders();
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> loginRequest = new HttpEntity<>(credentials, loginHeaders);

        // Send the login request
        ResponseEntity<String> loginResponse = client.postForEntity(createUri("/login"), loginRequest, String.class);

        // Check if the login response is successful
        if (loginResponse.getStatusCode().is2xxSuccessful()) {
            return objectMapper.readTree(loginResponse.getBody()).get("token").asText();
        } else {
            throw new RuntimeException("Error get token JWT. Response status: " + loginResponse.getStatusCodeValue());
        }
    }
}
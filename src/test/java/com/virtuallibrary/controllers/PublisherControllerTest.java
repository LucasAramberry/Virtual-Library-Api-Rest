package com.virtuallibrary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.virtuallibrary.dto.PublisherDto;
import com.virtuallibrary.entities.Publisher;
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
@ActiveProfiles("test")
class PublisherControllerTest {

    @Autowired
    private TestRestTemplate client;

    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private String jwtTokenUser, jwtTokenAdmin;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        if (jwtTokenAdmin == null) jwtTokenAdmin = getToken("lucasaramberry@admin.com", "123456");
//        else if (jwtTokenUser == null) jwtTokenUser = getToken("lucasaramberry@user.com", "123456");
    }

    @Test
    @Order(1)
    void testPublishers() throws JsonProcessingException {
        ResponseEntity<Publisher[]> response = client.getForEntity(createUri("/api/publishers"), Publisher[].class);

        if (response.getStatusCode() == HttpStatus.OK) {

            List<Publisher> publishers = Arrays.asList(response.getBody());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

            assertTrue(publishers.size() >= 5);
            assertEquals(1L, publishers.get(0).getId());
            assertEquals(5L, publishers.get(4).getId());

            JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(publishers));

            assertEquals("Publisher 1", json.get(0).path("name").asText());
            assertEquals("Publisher 2", json.get(1).path("name").asText());
        } else {
            System.out.println("Error get list of publishers. Response status: " + response.getStatusCodeValue());
        }
    }

    @Test
    @Order(2)
    void testRegister() {

        PublisherDto publisherDto = PublisherDto.builder().name("Publisher Test").build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<PublisherDto> request = new HttpEntity<>(publisherDto, headers);

        ResponseEntity<Publisher> response = client.postForEntity(createUri("/api/publishers/register"), request, Publisher.class);

        Publisher publisher = response.getBody();

        assertNotNull(publisher);
        assertNotNull(publisher.getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Publisher Test", publisher.getName());
    }

    @Test
    @Order(3)
    void testShowPublisher() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Publisher> response = client.exchange(createUri("/api/publishers/show/5"), HttpMethod.GET, request, Publisher.class);

        Publisher publisher = response.getBody();

        assertNotNull(publisher);
        assertEquals(5L, publisher.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Publisher 5", publisher.getName());
    }

    @Test
    @Order(4)
    void testUpdatePublisher() {
        PublisherDto publisherDto = PublisherDto.builder().name("Publisher Test Update").build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<PublisherDto> request = new HttpEntity<>(publisherDto, headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/publishers/update/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Publisher updated successfully", responseBody);
    }

    @Test
    @Order(5)
    void deactivate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/publishers/deactivate/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Publisher with id " + 6 + " was disabled.", responseBody);
    }

    @Test
    @Order(6)
    void activate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/publishers/activate/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Publisher with id " + 6 + " was enabled.", responseBody);
    }

    @Test
    @Order(7)
    void delete() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/publishers/delete/6"), HttpMethod.DELETE, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Publisher delete successfully.", responseBody);
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
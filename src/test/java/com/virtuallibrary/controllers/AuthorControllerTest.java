package com.virtuallibrary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.virtuallibrary.dto.AuthorDto;
import com.virtuallibrary.entities.Author;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class AuthorControllerTest {

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
    void testAuthors() throws JsonProcessingException {
        ResponseEntity<Author[]> response = client.getForEntity(createUri("/api/authors"), Author[].class);

        if (response.getStatusCode() == HttpStatus.OK) {

            List<Author> authors = Arrays.asList(response.getBody());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

            assertTrue(authors.size() >= 5);
            assertEquals(1L, authors.get(0).getId());
            assertEquals(5L, authors.get(4).getId());

            JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(authors));

            assertEquals("Author 1", json.get(0).path("name").asText());
            assertEquals("Author 2", json.get(1).path("name").asText());
        } else {
            System.out.println("Error get list of authors. Response status: " + response.getStatusCodeValue());
        }
    }

    @Test
    @Order(2)
    void testRegister() {

        AuthorDto authorDto = AuthorDto.builder().name("Author Test").build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<AuthorDto> request = new HttpEntity<>(authorDto, headers);

        ResponseEntity<Author> response = client.postForEntity(createUri("/api/authors/register"), request, Author.class);

        Author author = response.getBody();

        assertNotNull(author);
        assertNotNull(author.getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Author Test", author.getName());
    }

    @Test
    @Order(3)
    void testShowAuthor() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Author> response = client.exchange(createUri("/api/authors/show/5"), HttpMethod.GET, request, Author.class);

        Author author = response.getBody();

        assertNotNull(author);
        assertEquals(5L, author.getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Author 5", author.getName());
    }

    @Test
    @Order(4)
    void testUpdateAuthor() {
        AuthorDto authorDto = AuthorDto.builder().name("Author Test Update").build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<AuthorDto> request = new HttpEntity<>(authorDto, headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/authors/update/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Author updated successfully", responseBody);
    }

    @Test
    @Order(5)
    void deactivate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/authors/deactivate/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Author with id " + 6 + " was disabled.", responseBody);
    }

    @Test
    @Order(6)
    void activate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/authors/activate/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Author with id " + 6 + " was enabled.", responseBody);
    }

    @Test
    @Order(7)
    void delete() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/authors/delete/6"), HttpMethod.DELETE, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Author delete successfully.", responseBody);
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
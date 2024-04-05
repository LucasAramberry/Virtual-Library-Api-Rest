package com.virtuallibrary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.virtuallibrary.dto.BookDto;
import com.virtuallibrary.entities.Book;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("test")
class BookControllerTest {
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
    void testBooks() throws JsonProcessingException {
        ResponseEntity<Book[]> response = client.getForEntity(createUri("/api/books"), Book[].class);

        if (response.getStatusCode() == HttpStatus.OK) {

            List<Book> books = Arrays.asList(response.getBody());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

            assertTrue(books.size() >= 5);
            assertEquals(1L, books.get(0).getId());
            assertEquals(5L, books.get(4).getId());

            JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(books));

            assertEquals("Book 1", json.get(0).path("title").asText());
            assertEquals("Book 2", json.get(1).path("title").asText());
        } else {
            System.out.println("Error get list of books. Response status: " + response.getStatusCodeValue());
        }
    }

    @Test
    @Order(2)
    void testRegister() {

        BookDto bookDto = BookDto.builder()
                .isbn("9999999999999")
                .title("Book Test")
                .description("This is the description for Book Test")
                .datePublication(LocalDate.parse("2024-01-01"))
                .amountPages(500)
                .amountCopies(150)
                .amountCopiesBorrowed(50)
                .idAuthor(5L)
                .idPublisher(5L)
                .build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<BookDto> request = new HttpEntity<>(bookDto, headers);

        ResponseEntity<Book> response = client.postForEntity(createUri("/api/books/register"), request, Book.class);

        Book book = response.getBody();

        assertNotNull(book);
        assertNotNull(book.getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Book Test", book.getTitle());
    }

    @Test
    @Order(3)
    void testShowBook() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<BookDto> response = client.exchange(createUri("/api/books/view/5"), HttpMethod.GET, request, BookDto.class);

        BookDto bookDto = response.getBody();

        assertNotNull(bookDto);
        assertEquals("9780140449297", bookDto.getIsbn());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals("Book 5", bookDto.getTitle());
    }

    @Test
    @Order(4)
    void testUpdateBook() {
        BookDto bookDto = BookDto.builder()
                .isbn("9999999999999")
                .title("Book Test Update")
                .description("This is the description for Book Test Update")
                .datePublication(LocalDate.parse("2024-01-01"))
                .amountPages(500)
                .amountCopies(150)
                .amountCopiesBorrowed(50)
                .idAuthor(5L)
                .idPublisher(5L)
                .build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<BookDto> request = new HttpEntity<>(bookDto, headers);

        ResponseEntity<Book> response = client.exchange(createUri("/api/books/update/6"), HttpMethod.PUT, request, Book.class);

        Book book = response.getBody();

        assertNotNull(book);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(6L, book.getId());
        assertEquals("Book Test Update", book.getTitle());
    }

    @Test
    @Order(5)
    void deactivate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/books/deactivate/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book with id " + 6 + " was disabled.", responseBody);
    }

    @Test
    @Order(6)
    void activate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/books/activate/6"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book with id " + 6 + " was enabled.", responseBody);
    }

    @Test
    @Order(7)
    void delete() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/books/delete/6"), HttpMethod.DELETE, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Book delete successfully.", responseBody);
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
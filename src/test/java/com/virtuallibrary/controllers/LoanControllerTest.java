package com.virtuallibrary.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.virtuallibrary.dto.LoanDto;
import com.virtuallibrary.entities.Loan;
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
class LoanControllerTest {

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
    void testLoans() throws JsonProcessingException {
        ResponseEntity<Loan[]> response = client.getForEntity(createUri("/api/loans"), Loan[].class);

        if (response.getStatusCode() == HttpStatus.OK) {

            List<Loan> loans = Arrays.asList(response.getBody());

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());

            assertTrue(loans.size() >= 5);
            assertEquals(1L, loans.get(0).getId());
            assertEquals(3L, loans.get(2).getId());

            JsonNode json = objectMapper.readTree(objectMapper.writeValueAsString(loans));

            assertEquals("2024-04-01", json.get(0).path("dateLoan").asText());
        } else {
            System.out.println("Error get list of loans. Response status: " + response.getStatusCodeValue());
        }
    }

    @Test
    @Order(2)
    void testRegister() {

        LoanDto loanDto = LoanDto.builder()
                .dateDevolution(LocalDate.parse("2024-12-12"))
                .idBook(5L)
                .idUser(1L)
                .build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<LoanDto> request = new HttpEntity<>(loanDto, headers);

        ResponseEntity<Loan> response = client.postForEntity(createUri("/api/loans/register"), request, Loan.class);

        Loan loan = response.getBody();

        assertNotNull(loan);
        assertNotNull(loan.getId());
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
        assertEquals(1L, loan.getUser().getId());
    }

    @Test
    @Order(3)
    void testMyLoans() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<Loan[]> response = client.exchange(createUri("/api/loans/my-loans"), HttpMethod.GET, request, Loan[].class);

        List<Loan> loans = Arrays.asList(response.getBody());

        assertNotNull(loans);
        assertTrue(loans.size() == 1);
        assertEquals(1L, loans.get(0).getUser().getId());
        assertEquals(5L, loans.get(0).getBook().getId());
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, response.getHeaders().getContentType());
    }

    @Test
    @Order(4)
    void testUpdateLoan() {
        LoanDto loanDto = LoanDto.builder()
                .dateDevolution(LocalDate.parse("2024-06-06"))
                .idBook(3L)
                .idUser(1L)
                .build();

        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<LoanDto> request = new HttpEntity<>(loanDto, headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/loans/update/3"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Loan updated successfully", responseBody);
    }

    @Test
    @Order(5)
    void deactivate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/loans/deactivate/3"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Loan with id " + 3 + " was disabled.", responseBody);
    }

    @Test
    @Order(6)
    void activate() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/loans/activate/3"), HttpMethod.PUT, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Loan with id " + 3 + " was enabled.", responseBody);
    }

    @Test
    @Order(7)
    void delete() {
        // Build authorization headers with JWT token
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtTokenAdmin);

        // Build the HTTP request with authorization headers
        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = client.exchange(createUri("/api/loans/delete/3"), HttpMethod.DELETE, request, String.class);

        String responseBody = response.getBody();

        assertNotNull(responseBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Loan delete successfully.", responseBody);
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
package com.virtuallibrary.services;

import com.virtuallibrary.entities.*;
import com.virtuallibrary.repositories.LoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
@ActiveProfiles("test")
class LoanServiceTest {

    @MockBean
    LoanRepository loanRepository;
    @Autowired
    LoanService loanService;

    private Book book1, book2;
    private User user1, user2;
    private Loan loan1, loan2;
    private List<Loan> loanList;

    @BeforeEach
    void setUp() {

        book1 = Book.builder()
                .id(1L)
                .isbn("9898989898989")
                .title("Book Test 1")
                .description("This is the description for the book test")
                .datePublication(LocalDate.now())
                .amountPages(150)
                .amountCopies(100)
                .amountCopiesBorrowed(50)
                .amountCopiesRemaining(50)
                .author(Author.builder().id(1L).name("Author 1").enabled(true).createdAt(LocalDateTime.now()).build())
                .publisher(Publisher.builder().id(1L).name("Publisher 1").enabled(true).createdAt(LocalDateTime.now()).build())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        book2 = Book.builder()
                .id(2L)
                .isbn("1212121212121")
                .title("Book Test 2")
                .description("This is the description for the book test 2")
                .datePublication(LocalDate.now())
                .amountPages(200)
                .amountCopies(120)
                .amountCopiesBorrowed(40)
                .amountCopiesRemaining(80)
                .author(Author.builder().id(2L).name("Author 2").enabled(true).createdAt(LocalDateTime.now()).build())
                .publisher(Publisher.builder().id(2L).name("Publisher 2").enabled(true).createdAt(LocalDateTime.now()).build())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        user1 = new User(1L, "Test", "First", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());
        user2 = new User(2L, "Test", "Second", "7777777777", null, "test@test.com", new BCryptPasswordEncoder().encode("12345"), true, LocalDateTime.now());

        loan1 = Loan.builder()
                .id(1L)
                .dateLoan(LocalDate.now())
                .dateDevolution(LocalDate.parse("2024-12-31"))
                .book(book1)
                .user(user1)
                .enabled(true)
                .build();

        loan2 = Loan.builder()
                .id(2L)
                .dateLoan(LocalDate.now())
                .dateDevolution(LocalDate.parse("2024-12-31"))
                .book(book2)
                .user(user2)
                .enabled(false)
                .build();

        loanList = Arrays.asList(loan1, loan2);
    }

    @Test
    void testFindAll() {
        // Given
        when(loanRepository.findAll()).thenReturn(loanList);

        // When
        List<Loan> loans = loanService.findAll();

        // Then
        assertEquals(2, loans.size());
        verify(loanRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan1));

        // When
        Loan loan = loanService.findById(1L).orElseThrow();

        // Then
        assertNotNull(loan);
        assertEquals(book1, loan.getBook());
        verify(loanRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        // Given
        when(loanRepository.save(loan1)).thenReturn(loan1);

        // When
        Loan save = loanService.save(loan1);

        // Then
        assertNotNull(save);
        assertEquals(1, save.getId());
        verify(loanRepository, times(1)).save(loan1);
    }

    @Test
    void testUpdate() {
        // Given
        when(loanRepository.save(loan1)).thenReturn(loan1);

        // When
        Loan save = loanService.save(loan1);

        // Then
        assertNotNull(save);
        assertEquals(user1, save.getUser());
        assertEquals(1, save.getId());
        verify(loanRepository, times(1)).save(loan1);
    }

    @Test
    void testDelete() {
        // Given
        // When
        loanService.delete(loan1);

        // Then
        verify(loanRepository, times(1)).delete(loan1);
    }

    @Test
    void testFindByEnabledTrue() {
        // Given
        when(loanRepository.findByEnabledTrue()).thenReturn(Collections.singletonList(loanList.get(0)));

        // When
        List<Loan> loans = loanService.findByEnabledTrue();

        // Then
        assertEquals(1, loans.size());
        verify(loanRepository, times(1)).findByEnabledTrue();
    }

    @Test
    void testFindByEnabledFalse() {
        // Given
        when(loanRepository.findByEnabledFalse()).thenReturn(Collections.singletonList(loanList.get(1)));

        // When
        List<Loan> loans = loanService.findByEnabledFalse();

        // Then
        assertEquals(1, loans.size());
        verify(loanRepository, times(1)).findByEnabledFalse();
    }

    @Test
    void testActivate() {
        // Given
        when(loanRepository.findById(2L)).thenReturn(Optional.of(loan2));
        when(loanRepository.save(loan2)).then(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setEnabled(true);
            return loan;
        });

        // When
        loanService.activate(2L);

        // Then
        verify(loanRepository).findById(2L);
        verify(loanRepository).save(any());
    }

    @Test
    void testDeactivate() {
        // Given
        when(loanRepository.findById(1L)).thenReturn(Optional.of(loan1));
        when(loanRepository.save(loan1)).then(invocation -> {
            Loan loan = invocation.getArgument(0);
            loan.setEnabled(false);
            return loan;
        });

        // When
        loanService.deactivate(1L);

        // Then
        verify(loanRepository).findById(1L);
        verify(loanRepository).save(loan1);
    }

    @Test
    void testFindByUser() {
        // Given
        when(loanRepository.findByUser(user1)).thenReturn(Collections.singletonList(loan1));

        // When
        List<Loan> loans = loanService.findByUser(user1);

        // Then
        assertEquals(1, loans.size());
        loans.forEach(book -> {
            assertEquals("Author 1", book.getBook().getAuthor().getName());
        });
        verify(loanRepository).findByUser(user1);
    }

}
package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Loan;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LoanRepositoryTest {

    @Autowired
    LoanRepository loanRepository;
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @Test
    void findByUser() {
        List<Loan> loanList = loanRepository.findByUser(userRepository.findById(2L).orElseThrow());

        assertEquals(4, loanList.size());
        loanList.forEach(loan -> {
            assertTrue(loan.getUser().getEmail().equals("lucasaramberry@user.com"));
        });
    }

    @Test
    void testFindByEnabledTrue() {
        List<Loan> loanList = loanRepository.findByEnabledTrue();

        assertNotNull(loanList);
        assertFalse(loanList.isEmpty());
        assertEquals(4, loanList.size());
    }

    @Test
    void testFindByEnabledFalse() {
        List<Loan> loanList = loanRepository.findByEnabledFalse();

        assertTrue(loanList.isEmpty());
        assertEquals(0, loanList.size());
    }

    @Test
    void testFindById() {
        Optional<Loan> loanOptional = loanRepository.findById(7L);

        assertFalse(loanOptional.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            loanOptional.orElseThrow();
        });
    }

    @Test
    void testFindAll() {
        List<Loan> loanList = loanRepository.findAll();

        assertEquals(3, loanList.size());
        assertNotNull(loanList);
    }

    @Test
    void testSave() {
        Loan loan = loanRepository.save(Loan.builder()
                .dateLoan(LocalDate.now())
                .dateDevolution(LocalDate.parse("2024-12-31"))
                .user(userRepository.findById(2L).orElseThrow())
                .book(bookRepository.findById(5L).orElseThrow())
                .enabled(true)
                .build());

        assertNotNull(loan);
        assertEquals(LocalDate.parse("2024-12-31"), loan.getDateDevolution());
    }

    @Test
    void testUpdate() {
        Optional<Loan> loanOptional = loanRepository.findById(3L);

        assertTrue(loanOptional.isPresent());

        Loan loan = loanOptional.orElseThrow();

        loan.setEnabled(false);

        Loan loanUpdate = loanRepository.save(loan);

        assertFalse(loanUpdate.isEnabled());
        assertEquals(3, loanUpdate.getId());
    }

    @Test
    void testDelete() {
        loanRepository.delete(loanRepository.findById(4L).orElseThrow());

        assertThrows(NoSuchElementException.class, () -> {
            loanRepository.findById(4L).orElseThrow();
        });
    }
}
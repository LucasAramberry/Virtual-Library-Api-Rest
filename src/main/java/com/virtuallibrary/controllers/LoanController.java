package com.virtuallibrary.controllers;

import com.virtuallibrary.dto.LoanDto;
import com.virtuallibrary.entities.Book;
import com.virtuallibrary.entities.Loan;
import com.virtuallibrary.entities.User;
import com.virtuallibrary.services.BookService;
import com.virtuallibrary.services.LoanService;
import com.virtuallibrary.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    @Autowired
    private LoanService loanService;
    @Autowired
    private BookService bookService;
    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<Loan>> loans() {
        return ResponseEntity.ok(loanService.findAll());
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/my-loans")
    public ResponseEntity<?> myLoans() {
        Object userSessionEmail = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Optional<User> userOptional = userService.findByEmail(userSessionEmail.toString());

        if (userOptional.isPresent()) {
            List<Loan> myLoans = loanService.findByUser(userOptional.orElseThrow());
            return ResponseEntity.ok(myLoans);
        }

        return ResponseEntity.badRequest().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PostMapping("/register")
    public ResponseEntity<?> registerLoan(@Valid @RequestBody LoanDto loanDto) {

        Optional<User> userOptional = userService.findByEmail(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());

        if (userOptional.isPresent()) {

            User userSession = userOptional.orElseThrow();
            Optional<Book> bookOptional = bookService.findById(loanDto.getIdBook());

            if (bookOptional.isPresent()) {
                Book book = bookOptional.orElseThrow();

                if (book.getAmountCopiesRemaining() >= 1 && book.isEnabled()) {

                    Loan loan = new Loan();

                    loan.setDateLoan(LocalDate.now());
                    loan.setDateDevolution(loanDto.getDateDevolution());
                    loan.setEnabled(true);
                    loan.setBook(book);

                    if (userSession.getRoles().size() == 1 && userSession.getRoles().stream().map(role -> role.getName()).allMatch(role -> role.equals("ROLE_USER")))
                        loan.setUser(userSession);
                    else loan.setUser(userService.findById(loanDto.getIdUser()).orElseThrow());

                    return ResponseEntity.status(HttpStatus.CREATED).body(loanService.save(loan));
                } else {
                    return ResponseEntity.badRequest().body("The book entered does not have enough copies available to make the loan or this disabled.");
                }
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateLoan(@PathVariable Long id, @Valid @RequestBody LoanDto loanDto) {

        if (id != null) {
            Optional<Loan> loanOptional = loanService.findById(id);
            if (loanOptional.isPresent()) {
                Loan loan = loanOptional.orElseThrow();

                if (loan.isEnabled() && loan.getDateDevolution().isAfter(LocalDate.now())) {

                    loan.setDateDevolution(loanDto.getDateDevolution());

                    if (!loan.getBook().getId().equals(loanDto.getIdBook())) {
                        Optional<Book> bookOptional = bookService.findById(loanDto.getIdBook());
                        if (bookOptional.isPresent()) {
                            Book book = bookOptional.orElseThrow();
                            if (book.getAmountCopiesRemaining() >= 1 && book.isEnabled()) {

                                //we carry out the setting of the borrowed copy in the new book
                                bookService.lendBook(book);
                                //We return the copy of the book that we had ordered
                                bookService.devolutionBook(loan.getBook());

                                loan.setBook(book);
                            } else {
                                return ResponseEntity.badRequest().body("The book entered does not have enough copies available to make the loan or this disabled.");
                            }
                        }
                    }
                    loanService.save(loan);

                    return ResponseEntity.ok("Loan updated successfully");
                }else {
                    return ResponseEntity.badRequest().body("The loan selected is disabled.");
                }

            }
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        if (id != null) {
            loanService.activate(id);
            return ResponseEntity.ok("Loan with id " + id + " was enabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        if (id != null) {
            loanService.deactivate(id);
            return ResponseEntity.ok("Loan with id " + id + " was disabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (id != null) {
            Optional<Loan> loanOptional = loanService.findById(id);
            if (loanOptional.isPresent()) {
                Loan loan = loanOptional.orElseThrow();
                if (loan.isEnabled()) bookService.devolutionBook(loan.getBook());
                loanService.delete(loan);
                return ResponseEntity.ok("Loan delete successfully.");
            }
        }
        return ResponseEntity.notFound().build();
    }
}

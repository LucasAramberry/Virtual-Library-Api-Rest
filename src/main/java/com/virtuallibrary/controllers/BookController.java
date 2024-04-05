package com.virtuallibrary.controllers;

import com.virtuallibrary.dto.BookDto;
import com.virtuallibrary.entities.Author;
import com.virtuallibrary.entities.Book;
import com.virtuallibrary.entities.Publisher;
import com.virtuallibrary.services.AuthorService;
import com.virtuallibrary.services.BookService;
import com.virtuallibrary.services.PublisherService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/books")
public class BookController {

    @Autowired
    private BookService bookService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private PublisherService publisherService;

    @GetMapping
    public ResponseEntity<List<Book>> books() {
        return ResponseEntity.ok(bookService.findAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewBooks(@PathVariable Long id) {

        Optional<Book> bookOptional = bookService.findById(id);
        if (bookOptional.isPresent()) {
            Book book = bookOptional.orElseThrow();
            return ResponseEntity.ok(BookDto.builder()
                    .isbn(book.getIsbn())
                    .title(book.getTitle())
                    .description(book.getDescription())
                    .datePublication(book.getDatePublication())
                    .amountPages(book.getAmountPages())
                    .amountCopies(book.getAmountCopies())
                    .amountCopiesBorrowed(book.getAmountCopiesBorrowed())
                    .idAuthor(book.getAuthor().getId())
                    .idPublisher(book.getPublisher().getId())
                    .build()
            );
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> registerBook(@Valid @RequestBody BookDto bookDto) {

        Optional<Author> authorOptional = authorService.findById(bookDto.getIdAuthor());
        Optional<Publisher> publisherOptional = publisherService.findById(bookDto.getIdPublisher());

        if (authorOptional.isPresent() && publisherOptional.isPresent()) {
            Book book = bookService.save(Book.builder()
                    .isbn(bookDto.getIsbn())
                    .title(bookDto.getTitle())
                    .description(bookDto.getDescription())
                    .datePublication(bookDto.getDatePublication())
                    .amountPages(bookDto.getAmountPages())
                    .amountCopies(bookDto.getAmountCopies())
                    .amountCopiesBorrowed(bookDto.getAmountCopiesBorrowed())
                    .amountCopiesRemaining(bookDto.getAmountCopies() - bookDto.getAmountCopiesBorrowed())
                    .author(authorOptional.orElseThrow())
                    .publisher(publisherOptional.orElseThrow())
                    .createdAt(LocalDateTime.now())
                    .enabled(true)
                    .build());

            return ResponseEntity.status(HttpStatus.CREATED).body(book);
        }
        return ResponseEntity.badRequest().body("Author or Publisher id incorrect.");
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @Valid @RequestBody BookDto bookDto) {

        if (id != null) {
            Optional<Book> bookOptional = bookService.findById(id);

            if (bookOptional.isPresent()) {

                Book book = bookOptional.orElseThrow();

                book.setIsbn(bookDto.getIsbn());
                book.setTitle(bookDto.getTitle());
                book.setDescription(bookDto.getDescription());
                book.setDatePublication(bookDto.getDatePublication());
                book.setAmountPages(bookDto.getAmountPages());
                book.setAmountCopies(bookDto.getAmountCopies());
                book.setAmountCopiesBorrowed(bookDto.getAmountCopiesBorrowed());
                book.setAmountCopiesRemaining(bookDto.getAmountCopies() - bookDto.getAmountCopiesBorrowed());

                if (!bookDto.getIdAuthor().equals(book.getAuthor().getId())) {
                    Optional<Author> authorOptional = authorService.findById(bookDto.getIdAuthor());
                    authorOptional.ifPresent(author -> book.setAuthor(author));
                }

                if (!bookDto.getIdPublisher().equals(book.getPublisher().getId())) {
                    Optional<Publisher> publisherOptional = publisherService.findById(bookDto.getIdPublisher());
                    publisherOptional.ifPresent(publisher -> book.setPublisher(publisher));
                }

                bookService.save(book);

                return ResponseEntity.ok(book);
            }
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate/{id}")
    public ResponseEntity<?> enabled(@PathVariable Long id) {
        if (id != null) {
            bookService.activate(id);
            return ResponseEntity.ok("Book with id " + id + " was enabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> disabled(@PathVariable Long id) {
        if (id != null) {
            bookService.deactivate(id);
            return ResponseEntity.ok("Book with id " + id + " was disabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (id != null) {
            Optional<Book> bookOptional = bookService.findById(id);
            if (bookOptional.isPresent()) {
                bookService.delete(bookOptional.orElseThrow());
                return ResponseEntity.ok("Book delete successfully.");
            }
        }
        return ResponseEntity.notFound().build();
    }
}

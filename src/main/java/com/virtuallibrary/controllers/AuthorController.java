package com.virtuallibrary.controllers;

import com.virtuallibrary.dto.AuthorDto;
import com.virtuallibrary.entities.Author;
import com.virtuallibrary.services.AuthorService;
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
@RequestMapping("/api/authors")
public class AuthorController {

    @Autowired
    private AuthorService authorService;

    @GetMapping
    public ResponseEntity<List<Author>> authors() {
        return ResponseEntity.ok(authorService.findAll());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthorDto authorDto) {

        Author author = authorService.save(Author.builder()
                .name(authorDto.getName())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(author);
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/show/{id}")
    public ResponseEntity<?> showAuthor(@PathVariable Long id) {

        Optional<Author> authorOptional = authorService.findById(id);

        if (authorOptional.isPresent()) {
            Author author = authorOptional.orElseThrow();
            return ResponseEntity.ok(author);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorDto authorDto) {

        Optional<Author> authorOptional = authorService.findById(id);

        if (authorOptional.isPresent()) {
            Author author = authorOptional.orElseThrow();

            author.setName(authorDto.getName());

            authorService.save(author);

            return ResponseEntity.ok("Author updated successfully");
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        if (id != null) {
            authorService.activate(id);
            return ResponseEntity.ok("Author with id " + id + " was enabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        if (id != null) {
            authorService.deactivate(id);
            return ResponseEntity.ok("Author with id " + id + " was disabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (id != null) {
            Optional<Author> authorOptional = authorService.findById(id);
            if (authorOptional.isPresent()) {
                authorService.delete(authorOptional.orElseThrow());
                return ResponseEntity.ok("Author delete successfully.");
            }
        }
        return ResponseEntity.notFound().build();
    }
}

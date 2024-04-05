package com.virtuallibrary.controllers;

import com.virtuallibrary.dto.PublisherDto;
import com.virtuallibrary.entities.Publisher;
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
@RequestMapping("/api/publishers")
public class PublisherController {

    @Autowired
    private PublisherService publisherService;

    @GetMapping()
    public ResponseEntity<List<Publisher>> publishers() {
        return ResponseEntity.ok(publisherService.findAll());
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/show/{id}")
    public ResponseEntity<?> showPublisher(@PathVariable Long id) {

        Optional<Publisher> publisherOptional = publisherService.findById(id);

        if (publisherOptional.isPresent()) {
            Publisher publisher = publisherOptional.orElseThrow();
            return ResponseEntity.ok(publisher);
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody PublisherDto publisherDtoDto) {

        Publisher publisher = publisherService.save(Publisher.builder()
                .name(publisherDtoDto.getName())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(publisher);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updatePublisher(@PathVariable Long id, @Valid @RequestBody PublisherDto publisherDto) {

        Optional<Publisher> publisherOptional = publisherService.findById(id);

        if (publisherOptional.isPresent()) {
            Publisher publisher = publisherOptional.orElseThrow();

            publisher.setName(publisherDto.getName());

            publisherService.save(publisher);

            return ResponseEntity.ok("Publisher updated successfully");
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/activate/{id}")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        if (id != null) {
            publisherService.activate(id);
            return ResponseEntity.ok("Publisher with id " + id + " was enabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/deactivate/{id}")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        if (id != null) {
            publisherService.deactivate(id);
            return ResponseEntity.ok("Publisher with id " + id + " was disabled.");
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (id != null) {
            Optional<Publisher> publisherOptional = publisherService.findById(id);
            if (publisherOptional.isPresent()) {
                publisherService.delete(publisherOptional.orElseThrow());
                return ResponseEntity.ok("Publisher delete successfully.");
            }
        }
        return ResponseEntity.notFound().build();
    }
}

package com.virtuallibrary.controllers;

import com.virtuallibrary.dto.UserDto;
import com.virtuallibrary.entities.User;
import com.virtuallibrary.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userdto) {

        User user = userService.save(User.builder()
                .name(userdto.getName())
                .lastName(userdto.getLastName())
                .phone(userdto.getPhone())
                .email(userdto.getEmail())
                .password(userdto.getPassword())
                .build());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @GetMapping("/profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id) {

        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.orElseThrow();

            return ResponseEntity.ok(UserDto.builder()
                    .name(user.getName())
                    .lastName(user.getLastName())
                    .phone(user.getPhone())
                    .email(user.getEmail())
                    .build()
            );
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    @PutMapping("/edit-profile/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @Valid @RequestBody UserDto userdto) {

        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.orElseThrow();

            user.setName(userdto.getName());
            user.setLastName(userdto.getLastName());
            user.setPhone(userdto.getPhone());
            user.setEmail(userdto.getEmail());
            user.setPassword(userdto.getPassword());

            userService.save(user);

            return ResponseEntity.ok("Updated profile successfully");
        }

        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public List<User> users() {
        return userService.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {

        if (id != null) {
            Optional<User> userOptional = userService.findById(id);
            if (userOptional.isPresent()) {
                userService.delete(userOptional.orElseThrow());
                return ResponseEntity.ok("User delete successfully");
            }
        }
        return ResponseEntity.notFound().build();
    }
}

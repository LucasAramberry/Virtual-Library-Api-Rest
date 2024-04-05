package com.virtuallibrary.services;

import com.virtuallibrary.entities.User;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<User> findAll();

    Optional<User> findById(Long id);

    User save(User user);

    void delete(User user);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByEnabledTrue();

    List<User> findByEnabledFalse();

    void activate(Long id);

    void deactivate(Long id);
}

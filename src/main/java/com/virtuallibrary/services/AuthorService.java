package com.virtuallibrary.services;

import com.virtuallibrary.entities.Author;

import java.util.List;
import java.util.Optional;

public interface AuthorService {

    List<Author> findAll();

    Optional<Author> findById(Long id);

    Author save(Author author);

    void delete(Author author);

    Optional<Author> findByName(String name);

    List<Author> findByEnabledTrue();

    List<Author> findByEnabledFalse();

    void activate(Long id);

    void deactivate(Long id);
}

package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {

    List<Author> findByEnabledTrue();

    List<Author> findByEnabledFalse();

    Optional<Author> findByName(String name);
}

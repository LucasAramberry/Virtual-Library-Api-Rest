package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PublisherRepository extends JpaRepository<Publisher, Long> {

    List<Publisher> findByEnabledTrue();

    List<Publisher> findByEnabledFalse();

    Optional<Publisher> findByName(String name);
}

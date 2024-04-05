package com.virtuallibrary.services;

import com.virtuallibrary.entities.Publisher;

import java.util.List;
import java.util.Optional;

public interface PublisherService {

    List<Publisher> findAll();

    Optional<Publisher> findById(Long id);

    Publisher save(Publisher publisher);

    void delete(Publisher publisher);

    Optional<Publisher> findByName(String name);

    List<Publisher> findByEnabledTrue();

    List<Publisher> findByEnabledFalse();

    void activate(Long id);

    void deactivate(Long id);
}

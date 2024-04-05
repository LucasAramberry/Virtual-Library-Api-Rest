package com.virtuallibrary.services.impl;

import com.virtuallibrary.entities.Publisher;
import com.virtuallibrary.repositories.PublisherRepository;
import com.virtuallibrary.services.BookService;
import com.virtuallibrary.services.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private PublisherRepository publisherRepository;
    @Autowired
    private BookService bookService;

    @Transactional(readOnly = true)
    @Override
    public List<Publisher> findAll() {
        return (List<Publisher>) publisherRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Publisher> findById(Long id) {
        return publisherRepository.findById(id);
    }

    @Transactional
    @Override
    public Publisher save(Publisher publisher) {
        return publisherRepository.save(publisher);
    }

    @Transactional
    @Override
    public void delete(Publisher publisher) {
        publisherRepository.delete(publisher);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Publisher> findByName(String name) {
        return publisherRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publisher> findByEnabledTrue() {
        return publisherRepository.findByEnabledTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Publisher> findByEnabledFalse() {
        return publisherRepository.findByEnabledFalse();
    }

    @Transactional
    @Override
    public void activate(Long id) {
        Optional<Publisher> publisherOptional = findById(id);
        publisherOptional.ifPresent(publisher -> {
            if (!publisher.isEnabled()) {
                publisher.setEnabled(true);
                save(publisher);
                bookService.findByPublisher(publisher).stream().filter(b -> !b.isEnabled()).forEach(book -> bookService.activate(book.getId()));
            }
        });
    }

    @Transactional
    @Override
    public void deactivate(Long id) {
        Optional<Publisher> publisherOptional = findById(id);
        publisherOptional.ifPresent(publisher -> {
            if (publisher.isEnabled()) {
                publisher.setEnabled(false);
                save(publisher);
                bookService.findByPublisher(publisher).stream().filter(b -> !b.isEnabled()).forEach(book -> bookService.deactivate(book.getId()));
            }
        });
    }
}

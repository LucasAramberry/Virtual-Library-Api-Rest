package com.virtuallibrary.services.impl;

import com.virtuallibrary.entities.Author;
import com.virtuallibrary.repositories.AuthorRepository;
import com.virtuallibrary.services.AuthorService;
import com.virtuallibrary.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private BookService bookService;

    @Transactional(readOnly = true)
    @Override
    public List<Author> findAll() {
        return authorRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Author> findById(Long id) {
        return authorRepository.findById(id);
    }

    @Transactional
    @Override
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    @Override
    public void delete(Author author) {
        authorRepository.delete(author);
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Author> findByName(String name) {
        return authorRepository.findByName(name);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Author> findByEnabledTrue() {
        return authorRepository.findByEnabledTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Author> findByEnabledFalse() {
        return authorRepository.findByEnabledFalse();
    }

    @Transactional
    @Override
    public void activate(Long id) {
        Optional<Author> authorOptional = findById(id);
        authorOptional.ifPresent(author -> {
            if (!author.isEnabled()) {
                author.setEnabled(true);
                save(author);
                bookService.findByAuthor(author).forEach(book -> bookService.activate(book.getId()));
            }
        });
    }

    @Transactional
    @Override
    public void deactivate(Long id) {
        Optional<Author> authorOptional = findById(id);
        authorOptional.ifPresent(author -> {
            if (author.isEnabled()) {
                author.setEnabled(false);
                save(author);
                bookService.findByAuthor(author).forEach(book -> bookService.deactivate(book.getId()));
            }
        });
    }
}

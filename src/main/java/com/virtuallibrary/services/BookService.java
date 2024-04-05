package com.virtuallibrary.services;

import com.virtuallibrary.entities.Author;
import com.virtuallibrary.entities.Book;
import com.virtuallibrary.entities.Publisher;

import java.util.List;
import java.util.Optional;

public interface BookService {

    List<Book> findAll();

    Optional<Book> findById(Long id);

    Book save(Book book);

    void delete(Book book);

    List<Book> findByAuthor(Author author);

    List<Book> findByPublisher(Publisher publisher);

    List<Book> findByEnabledTrue();

    List<Book> findByEnabledFalse();

    void activate(Long id);

    void deactivate(Long id);

    void lendBook(Book book);

    void devolutionBook(Book book);
}

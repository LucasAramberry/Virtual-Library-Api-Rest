package com.virtuallibrary.services.impl;

import com.virtuallibrary.entities.Author;
import com.virtuallibrary.entities.Book;
import com.virtuallibrary.entities.Publisher;
import com.virtuallibrary.repositories.BookRepository;
import com.virtuallibrary.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BookServiceImpl implements BookService {

    @Autowired
    private BookRepository bookRepository;

    @Transactional(readOnly = true)
    @Override
    public List<Book> findAll() {
        return (List<Book>) bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    @Override
    public Optional<Book> findById(Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    @Override
    public Book save(Book book) {
        return bookRepository.save(book);
    }

    @Transactional
    @Override
    public void delete(Book book) {
        bookRepository.delete(book);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findByAuthor(Author author) {
        return bookRepository.findByAuthor(author);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findByPublisher(Publisher publisher) {
        return bookRepository.findByPublisher(publisher);
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findByEnabledTrue() {
        return bookRepository.findByEnabledTrue();
    }

    @Transactional(readOnly = true)
    @Override
    public List<Book> findByEnabledFalse() {
        return bookRepository.findByEnabledFalse();
    }

    @Transactional
    @Override
    public void activate(Long id) {
        Optional<Book> bookOptional = findById(id);
        bookOptional.ifPresent(book -> {
            if (!book.isEnabled()) {
                book.setEnabled(true);
                save(book);
            }
        });
    }

    @Transactional
    @Override
    public void deactivate(Long id) {
        Optional<Book> bookOptional = findById(id);
        bookOptional.ifPresent(book -> {
            if (book.isEnabled()) {
                book.setEnabled(false);
                save(book);
            }
        });
    }

    @Transactional
    @Override
    public void lendBook(Book book) {
        book.setAmountCopiesBorrowed(book.getAmountCopiesBorrowed() + 1);
        book.setAmountCopiesRemaining(book.getAmountCopiesRemaining() - 1);
        save(book);
    }

    @Transactional
    @Override
    public void devolutionBook(Book book) {
        book.setAmountCopiesBorrowed(book.getAmountCopiesBorrowed() - 1);
        book.setAmountCopiesRemaining(book.getAmountCopiesRemaining() + 1);
        save(book);
    }
}

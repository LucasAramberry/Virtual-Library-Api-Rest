package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Author;
import com.virtuallibrary.entities.Book;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class BookRepositoryTest {

    @Autowired
    BookRepository bookRepository;
    @Autowired
    AuthorRepository authorRepository;
    @Autowired
    PublisherRepository publisherRepository;

    @Test
    void findByAuthor() {
        List<Book> bookList = bookRepository.findByAuthor(new Author(1L, null, null, true));

        assertEquals(2, bookList.size());
        assertNotNull(bookList);
    }

    @Test
    void findByPublisher() {
        List<Book> bookList = bookRepository.findByPublisher(publisherRepository.findById(1L).orElseThrow());

        assertEquals(1, bookList.size());
        assertNotNull(bookList);
    }

    @Test
    void testFindByEnabledTrue() {
        List<Book> bookList = bookRepository.findByEnabledTrue();

        assertNotNull(bookList);
        assertFalse(bookList.isEmpty());
        assertEquals(6, bookList.size());
    }

    @Test
    void testFindByEnabledFalse() {
        List<Book> bookList = bookRepository.findByEnabledFalse();

        assertTrue(bookList.isEmpty());
        assertEquals(0, bookList.size());
    }

    @Test
    void testFindById() {
        Optional<Book> bookOptional = bookRepository.findById(7L);

        assertFalse(bookOptional.isPresent());
        assertThrows(NoSuchElementException.class, () -> {
            bookOptional.orElseThrow();
        });
    }

    @Test
    void testFindAll() {
        List<Book> bookList = bookRepository.findAll();

        assertEquals(5, bookList.size());
        assertNotNull(bookList);
    }

    @Test
    void testSave() {
        Book book = bookRepository.save(Book.builder()
                .isbn("9898989898989")
                .title("Book Test")
                .description("This is the description for the book test")
                .datePublication(LocalDate.now())
                .amountPages(150)
                .amountCopies(100)
                .amountCopiesBorrowed(50)
                .amountCopiesRemaining(50)
                .author(authorRepository.findById(1L).orElseThrow())
                .publisher(publisherRepository.findById(1L).orElseThrow())
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build());

        assertNotNull(book);
        assertEquals("Book Test", book.getTitle());
    }

    @Test
    void testUpdate() {
        Optional<Book> bookOptional = bookRepository.findById(5L);

        assertTrue(bookOptional.isPresent());

        Book book = bookOptional.orElseThrow();

        book.setDescription("Book Test Updated");

        Book bookUpdate = bookRepository.save(book);

        assertEquals("Book Test Updated", bookUpdate.getDescription());
        assertEquals(5, bookUpdate.getId());
    }

    @Test
    void testDelete() {
        bookRepository.delete(bookRepository.findById(6L).orElseThrow());

        assertThrows(NoSuchElementException.class, () -> {
            bookRepository.findById(6L).orElseThrow();
        });
    }
}
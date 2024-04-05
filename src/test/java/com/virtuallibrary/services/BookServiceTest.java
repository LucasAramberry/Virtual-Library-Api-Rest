package com.virtuallibrary.services;

import com.virtuallibrary.entities.Author;
import com.virtuallibrary.entities.Book;
import com.virtuallibrary.entities.Publisher;
import com.virtuallibrary.repositories.AuthorRepository;
import com.virtuallibrary.repositories.BookRepository;
import com.virtuallibrary.repositories.PublisherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class BookServiceTest {

    @MockBean
    BookRepository bookRepository;
    @MockBean
    AuthorRepository authorRepository;
    @MockBean
    PublisherRepository publisherRepository;
    @Autowired
    BookService bookService;

    private Book book1, book2;
    private Author author1, author2;
    private Publisher publisher1, publisher2;
    private List<Book> bookList;

    @BeforeEach
    void setUp() {
        author1 = Author.builder().id(1L).name("Author 1").enabled(true).createdAt(LocalDateTime.now()).build();
        author2 = Author.builder().id(2L).name("Author 2").enabled(true).createdAt(LocalDateTime.now()).build();
        publisher1 = Publisher.builder().id(1L).name("Publisher 1").enabled(true).createdAt(LocalDateTime.now()).build();
        publisher2 = Publisher.builder().id(2L).name("Publisher 2").enabled(true).createdAt(LocalDateTime.now()).build();

        book1 = Book.builder()
                .id(1L)
                .isbn("9898989898989")
                .title("Book Test 1")
                .description("This is the description for the book test")
                .datePublication(LocalDate.now())
                .amountPages(150)
                .amountCopies(100)
                .amountCopiesBorrowed(50)
                .amountCopiesRemaining(50)
                .author(author1)
                .publisher(publisher1)
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build();

        book2 = Book.builder()
                .id(2L)
                .isbn("1212121212121")
                .title("Book Test 2")
                .description("This is the description for the book test 2")
                .datePublication(LocalDate.now())
                .amountPages(200)
                .amountCopies(120)
                .amountCopiesBorrowed(40)
                .amountCopiesRemaining(80)
                .author(author2)
                .publisher(publisher2)
                .createdAt(LocalDateTime.now())
                .enabled(false)
                .build();

        bookList = Arrays.asList(book1, book2);
    }

    @Test
    void testFindAll() {
        // Given
        when(bookRepository.findAll()).thenReturn(bookList);

        // When
        List<Book> books = bookService.findAll();

        // Then
        assertEquals(2, books.size());
        verify(bookRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));

        // When
        Book b = bookService.findById(1L).orElseThrow();

        // Then
        assertNotNull(b);
        assertEquals("Book Test 1", b.getTitle());
        assertEquals("This is the description for the book test", b.getDescription());
        verify(bookRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        // Given
        when(bookRepository.save(book1)).then(invocation -> {
            Book b = invocation.getArgument(0);
            b.setId(1L);
            return b;
        });

        // When
        Book save = bookService.save(book1);

        // Then
        assertNotNull(save);
        assertEquals("Book Test 1", save.getTitle());
        assertEquals(1, save.getId());
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    void testUpdate() {
        // Given
        when(bookRepository.save(book1)).then(invocation -> {
            Book b = invocation.getArgument(0);
            b.setTitle("Book Test");
            return b;
        });

        // When
        Book save = bookService.save(book1);

        // Then
        assertNotNull(save);
        assertEquals("Book Test", save.getTitle());
        assertEquals(1, save.getId());
        verify(bookRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        // Given
        // When
        bookService.delete(book1);

        // Then
        verify(bookRepository, times(1)).delete(book1);
    }

    @Test
    void testFindByEnabledTrue() {
        // Given
        when(bookRepository.findByEnabledTrue()).thenReturn(Collections.singletonList(bookList.get(0)));

        // When
        List<Book> books = bookService.findByEnabledTrue();

        // Then
        assertEquals(1, books.size());
        verify(bookRepository, times(1)).findByEnabledTrue();
    }

    @Test
    void testFindByEnabledFalse() {
        // Given
        when(bookRepository.findByEnabledFalse()).thenReturn(Collections.singletonList(bookList.get(1)));

        // When
        List<Book> books = bookService.findByEnabledFalse();

        // Then
        assertEquals(1, books.size());
        verify(bookRepository, times(1)).findByEnabledFalse();
    }

    @Test
    void testActivate() {
        // Given
        when(bookRepository.findById(2L)).thenReturn(Optional.of(book2));
        when(bookRepository.save(book2)).then(invocation -> {
            Book b = invocation.getArgument(0);
            b.setEnabled(true);
            return b;
        });

        // When
        bookService.activate(2L);

        // Then
        verify(bookRepository).findById(2L);
        verify(bookRepository).save(book2);
    }

    @Test
    void testDeactivate() {
        // Given
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book1));
        when(bookRepository.save(book1)).then(invocation -> {
            Book b = invocation.getArgument(0);
            b.setEnabled(false);
            return b;
        });

        // When
        bookService.deactivate(1L);

        // Then
        verify(bookRepository).findById(1L);
        verify(bookRepository).save(any());
    }

    @Test
    void testFindByAuthor() {
        // Given
        when(bookRepository.findByAuthor(author1)).thenReturn(Collections.singletonList(book1));

        // When
        List<Book> books = bookService.findByAuthor(author1);

        // Then
        assertEquals(1, books.size());
        books.forEach(book -> {
            assertEquals("Author 1", book.getAuthor().getName());
        });
        verify(bookRepository).findByAuthor(author1);
    }

    @Test
    void testFindByPublisher() {
        // Given
        when(bookRepository.findByPublisher(publisher2)).thenReturn(Collections.singletonList(book2));

        // When
        List<Book> books = bookService.findByPublisher(publisher2);

        // Then
        assertEquals(1, books.size());
        books.forEach(book -> {
            assertEquals("Publisher 2", book.getPublisher().getName());
        });
        verify(bookRepository).findByPublisher(publisher2);
    }

    @Test
    void testLendBook() {
        // Given
        when(bookRepository.save(book1)).then(invocation -> {
            Book b = invocation.getArgument(0);
            b.setAmountCopiesBorrowed(b.getAmountCopiesBorrowed() - 1);
            b.setAmountCopiesRemaining(b.getAmountCopiesRemaining() + 1);
            return b;
        });

        // When
        bookService.lendBook(book1);

        // Then
        verify(bookRepository).save(book1);
    }

    @Test
    void testDevolutionBook() {
        // Given
        when(bookRepository.save(book1)).then(invocation -> {
            Book b = invocation.getArgument(0);
            b.setAmountCopiesBorrowed(b.getAmountCopiesBorrowed() + 1);
            b.setAmountCopiesRemaining(b.getAmountCopiesRemaining() - 1);
            return b;
        });

        // When
        bookService.devolutionBook(book1);

        // Then
        verify(bookRepository).save(book1);
    }
}
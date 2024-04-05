package com.virtuallibrary.services;

import com.virtuallibrary.entities.Author;
import com.virtuallibrary.repositories.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ActiveProfiles("test")
class AuthorServiceTest {

    @MockBean
    AuthorRepository authorRepository;
    @Autowired
    AuthorService authorService;

    @Test
    void testFindAll() {
        // Given
        List<Author> authorList = Arrays.asList(
                new Author(1L, "Author 1", LocalDateTime.now(), true),
                new Author(2L, "Author 2", LocalDateTime.now(), true)
        );

        when(authorRepository.findAll()).thenReturn(authorList);

        // When
        List<Author> authors = authorService.findAll();

        // Then
        assertEquals(2, authors.size());
        verify(authorRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        Author author = new Author(1L, "Author 1", LocalDateTime.now(), true);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));

        // When
        Author authorFind = authorService.findById(1L).orElseThrow();

        // Then
        assertNotNull(authorFind);
        assertEquals("Author 1", authorFind.getName());
        verify(authorRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        // Given
        Author author = new Author(null, "Author 1", LocalDateTime.now(), true);

        when(authorRepository.save(author)).then(invocation -> {
            Author a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });

        // When
        Author save = authorService.save(author);

        // Then
        assertNotNull(save);
        assertEquals("Author 1", save.getName());
        assertEquals(1, save.getId());
        verify(authorRepository, times(1)).save(any());
    }

    @Test
    void testUpdate() {
        // Given
        Author author = new Author(1L, "Author 1", LocalDateTime.now(), true);

        when(authorRepository.save(author)).then(invocation -> {
            Author a = invocation.getArgument(0);
            a.setName("Author Test");
            return a;
        });

        // When
        Author save = authorService.save(author);

        // Then
        assertNotNull(save);
        assertEquals("Author Test", save.getName());
        assertEquals(1, save.getId());
        verify(authorRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        // Given
        Author author = new Author(1L, "Author 1", LocalDateTime.now(), true);

        // When
        authorService.delete(author);

        // Then
        verify(authorRepository, times(1)).delete(author);
    }

    @Test
    void testFindByName() {
        // Given
        Author author = new Author(1L, "Author 1", LocalDateTime.now(), true);

        when(authorRepository.findByName("Author 1")).thenReturn(Optional.of(author));
        // When

        Optional<Author> a = authorService.findByName("Author 1");

        // Then
        assertTrue(a.isPresent());
        assertEquals("Author 1", a.get().getName());
        assertEquals(1, a.get().getId());
        verify(authorRepository, times(1)).findByName("Author 1");
    }

    @Test
    void testFindByEnabledTrue() {
        // Given
        List<Author> authorList = Arrays.asList(
                new Author(1L, "Author 1", LocalDateTime.now(), true),
                new Author(2L, "Author 2", LocalDateTime.now(), false)
        );

        when(authorRepository.findByEnabledTrue()).thenReturn(Collections.singletonList(authorList.get(0)));

        // When
        List<Author> authors = authorService.findByEnabledTrue();

        // Then
        assertEquals(1, authors.size());
        verify(authorRepository, times(1)).findByEnabledTrue();
    }

    @Test
    void testFindByEnabledFalse() {
        // Given
        List<Author> authorList = Arrays.asList(
                new Author(1L, "Author 1", LocalDateTime.now(), true),
                new Author(2L, "Author 2", LocalDateTime.now(), false)
        );

        when(authorRepository.findByEnabledFalse()).thenReturn(Collections.singletonList(authorList.get(1)));

        // When
        List<Author> authors = authorService.findByEnabledFalse();

        // Then
        assertEquals(1, authors.size());
        verify(authorRepository, times(1)).findByEnabledFalse();
    }

    @Test
    void testActivate() {
        // Given
        Author author = new Author(1L, "Author 1", LocalDateTime.now(), false);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).then(invocation -> {
            Author a = invocation.getArgument(0);
            a.setEnabled(true);
            return a;
        });

        // When
        authorService.activate(1L);

        // Then
        verify(authorRepository).findById(1L);
        verify(authorRepository).save(any());
    }

    @Test
    void testDeactivate() {
        // Given
        Author author = new Author(1L, "Author 1", LocalDateTime.now(), true);

        when(authorRepository.findById(1L)).thenReturn(Optional.of(author));
        when(authorRepository.save(author)).then(invocation -> {
            Author a = invocation.getArgument(0);
            a.setEnabled(false);
            return a;
        });

        // When
        authorService.deactivate(1L);

        // Then
        verify(authorRepository).findById(1L);
        verify(authorRepository).save(any());
    }
}
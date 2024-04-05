package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Author;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
//@DataJpaTest
class AuthorRepositoryTest {

    @Autowired
    AuthorRepository authorRepository;

    @Test
    void testFindById() {
        Optional<Author> authorOptional = authorRepository.findById(1L);
        assertTrue(authorOptional.isPresent());
        assertEquals("Author 1", authorOptional.orElseThrow().getName());
    }

    @Test
    void testFindByName() {
        Optional<Author> authorOptional = authorRepository.findByName("Author 1");
        assertTrue(authorOptional.isPresent());
        assertEquals("Author 1", authorOptional.orElseThrow().getName());
    }

    @Test
    void testFindByEnabledTrue() {
        List<Author> authorList = authorRepository.findByEnabledTrue();

        assertNotNull(authorList);
        assertFalse(authorList.isEmpty());
        assertEquals(6, authorList.size());
    }

    @Test
    void testFindByEnabledFalse() {
        List<Author> authorList = authorRepository.findByEnabledFalse();

        assertTrue(authorList.isEmpty());
        assertEquals(0, authorList.size());
    }

    @Test
    void testFindAll() {
        List<Author> authorList = (List<Author>) authorRepository.findAll();

        assertTrue(!authorList.isEmpty());
        assertEquals(5, authorList.size());
    }

    @Test
    void testSave() {
        Author author = authorRepository.save(Author
                .builder()
                .name("Author")
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build()
        );

        assertEquals("Author", author.getName());
    }

    @Test
    void testUpdate() {
        Optional<Author> authorOptional = authorRepository.findById(5L);

        authorOptional.orElseThrow().setName("Author");

        Author author = authorRepository.save(authorOptional.orElseThrow());

        assertEquals("Author", author.getName());
        assertEquals(5, author.getId());
    }

    @Test
    void testDelete() {
        Author author = authorRepository.findById(6L).orElseThrow();

        authorRepository.delete(author);

        assertThrows(NoSuchElementException.class, () -> {
            // Se lanza exception xq esta vacio
            authorRepository.findById(6L).orElseThrow();
        });

        assertEquals(5, authorRepository.findAll().size());
    }
}
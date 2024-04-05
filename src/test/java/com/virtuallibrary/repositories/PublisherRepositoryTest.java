package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Publisher;
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
class PublisherRepositoryTest {

    @Autowired
    PublisherRepository publisherRepository;

    @Test
    void findByEnabledTrue() {
        List<Publisher> publisherList = publisherRepository.findByEnabledTrue();

        assertNotNull(publisherList);
        assertFalse(publisherList.isEmpty());
        assertEquals(5, publisherList.stream().filter(publisher -> !publisher.getName().isBlank()).count());
        assertEquals(5, publisherList.size());
    }

    @Test
    void findByEnabledFalse() {
        List<Publisher> publisherList = publisherRepository.findByEnabledFalse();

        assertTrue(publisherList.isEmpty());
    }

    @Test
    void testFindByName() {
        Optional<Publisher> publisherOptional = publisherRepository.findByName("Publisher 1");
        assertTrue(publisherOptional.isPresent());
        assertEquals("Publisher 1", publisherOptional.orElseThrow().getName());
    }

    @Test
    void testFindAll() {
        List<Publisher> publisherList = publisherRepository.findAll();

        assertTrue(!publisherList.isEmpty());
        assertEquals(5, publisherList.size());
    }

    @Test
    void testSave() {
        Publisher publisher = publisherRepository.save(Publisher
                .builder()
                .name("Publisher")
                .createdAt(LocalDateTime.now())
                .enabled(true)
                .build()
        );

        assertEquals("Publisher", publisher.getName());
    }

    @Test
    void testUpdate() {
        Optional<Publisher> publisherOptional = publisherRepository.findById(5L);

        publisherOptional.orElseThrow().setName("Publisher");

        Publisher publisher = publisherRepository.save(publisherOptional.orElseThrow());

        assertEquals("Publisher", publisher.getName());
        assertEquals(5, publisher.getId());
    }

    @Test
    void testDelete() {
        Publisher publisher = publisherRepository.findById(6L).orElseThrow();

        publisherRepository.delete(publisher);

        assertThrows(NoSuchElementException.class, () -> {
            // Se lanza exception xq esta vacio
            publisherRepository.findById(6L).orElseThrow();
        });

        assertEquals(5, publisherRepository.findAll().size());
    }
}
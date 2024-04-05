package com.virtuallibrary.services;

import com.virtuallibrary.entities.Publisher;
import com.virtuallibrary.repositories.PublisherRepository;
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
class PublisherServiceTest {

    @MockBean
    PublisherRepository publisherRepository;
    @Autowired
    PublisherService publisherService;

    @Test
    void testFindAll() {
        // Given
        List<Publisher> publisherList = Arrays.asList(
                new Publisher(1L, "Publisher 1", LocalDateTime.now(), true),
                new Publisher(2L, "Publisher 2", LocalDateTime.now(), true)
        );

        when(publisherRepository.findAll()).thenReturn(publisherList);

        // When
        List<Publisher> publishers = publisherService.findAll();

        // Then
        assertEquals(2, publishers.size());
        verify(publisherRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        // Given
        Publisher publisher = new Publisher(1L, "Publisher 1", LocalDateTime.now(), true);

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));

        // When
        Publisher p = publisherService.findById(1L).orElseThrow();

        // Then
        assertNotNull(p);
        assertEquals("Publisher 1", p.getName());
        verify(publisherRepository, times(1)).findById(1L);
    }

    @Test
    void testSave() {
        // Given
        Publisher publisher = new Publisher(null, "Publisher 1", LocalDateTime.now(), true);

        when(publisherRepository.save(publisher)).then(invocation -> {
            Publisher p = invocation.getArgument(0);
            p.setId(1L);
            return p;
        });

        // When
        Publisher save = publisherService.save(publisher);

        // Then
        assertNotNull(save);
        assertEquals("Publisher 1", save.getName());
        assertEquals(1, save.getId());
        verify(publisherRepository, times(1)).save(any());
    }

    @Test
    void testUpdate() {
        // Given
        Publisher publisher = new Publisher(1L, "Publisher 1", LocalDateTime.now(), true);

        when(publisherRepository.save(publisher)).then(invocation -> {
            Publisher p = invocation.getArgument(0);
            p.setName("Publisher Test");
            return p;
        });

        // When
        Publisher save = publisherService.save(publisher);

        // Then
        assertNotNull(save);
        assertEquals("Publisher Test", save.getName());
        assertEquals(1, save.getId());
        verify(publisherRepository, times(1)).save(any());
    }

    @Test
    void testDelete() {
        // Given
        Publisher publisher = new Publisher(1L, "Publisher 1", LocalDateTime.now(), true);

        // When
        publisherService.delete(publisher);

        // Then
        verify(publisherRepository, times(1)).delete(publisher);
    }

    @Test
    void testFindByName() {
        // Given
        Publisher publisher = new Publisher(1L, "Publisher 1", LocalDateTime.now(), true);

        when(publisherRepository.findByName("Publisher 1")).thenReturn(Optional.of(publisher));

        // When
        Optional<Publisher> p = publisherService.findByName("Publisher 1");

        // Then
        assertTrue(p.isPresent());
        assertEquals("Publisher 1", p.get().getName());
        assertEquals(1, p.get().getId());
        verify(publisherRepository, times(1)).findByName("Publisher 1");
    }

    @Test
    void testFindByEnabledTrue() {
        // Given
        List<Publisher> publisherList = Arrays.asList(
                new Publisher(1L, "Publisher 1", LocalDateTime.now(), true),
                new Publisher(2L, "Publisher 2", LocalDateTime.now(), false)
        );

        when(publisherRepository.findByEnabledTrue()).thenReturn(Collections.singletonList(publisherList.get(0)));

        // When
        List<Publisher> publishers = publisherService.findByEnabledTrue();

        // Then
        assertEquals(1, publishers.size());
        verify(publisherRepository, times(1)).findByEnabledTrue();
    }

    @Test
    void testFindByEnabledFalse() {
        // Given
        List<Publisher> publisherList = Arrays.asList(
                new Publisher(1L, "Publisher 1", LocalDateTime.now(), true),
                new Publisher(2L, "Publisher 2", LocalDateTime.now(), false)
        );

        when(publisherRepository.findByEnabledFalse()).thenReturn(Collections.singletonList(publisherList.get(1)));

        // When
        List<Publisher> publishers = publisherService.findByEnabledFalse();

        // Then
        assertEquals(1, publishers.size());
        verify(publisherRepository, times(1)).findByEnabledFalse();
    }

    @Test
    void testActivate() {
        // Given
        Publisher publisher = new Publisher(1L, "Publisher 1", LocalDateTime.now(), false);

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(publisherRepository.save(publisher)).then(invocation -> {
            Publisher p = invocation.getArgument(0);
            p.setEnabled(true);
            return p;
        });

        // When
        publisherService.activate(1L);

        // Then
        verify(publisherRepository).findById(1L);
        verify(publisherRepository).save(any());
    }

    @Test
    void testDeactivate() {
        // Given
        Publisher publisher = new Publisher(1L, "Publisher 1", LocalDateTime.now(), true);

        when(publisherRepository.findById(1L)).thenReturn(Optional.of(publisher));
        when(publisherRepository.save(publisher)).then(invocation -> {
            Publisher p = invocation.getArgument(0);
            p.setEnabled(false);
            return p;
        });

        // When
        publisherService.deactivate(1L);

        // Then
        verify(publisherRepository).findById(1L);
        verify(publisherRepository).save(any());
    }
}
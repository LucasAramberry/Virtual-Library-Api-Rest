package com.virtuallibrary.repositories;

import com.virtuallibrary.entities.Author;
import com.virtuallibrary.entities.Book;
import com.virtuallibrary.entities.Publisher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {

    List<Book> findByAuthor(Author author);

    List<Book> findByPublisher(Publisher publisher);

    List<Book> findByEnabledTrue();

    List<Book> findByEnabledFalse();
}

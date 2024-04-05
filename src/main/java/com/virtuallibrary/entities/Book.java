package com.virtuallibrary.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String isbn;

    private String title;

    private String description;

    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @Column(name = "date_publication", columnDefinition = "DATE")
    private LocalDate datePublication;

    @Column(name = "amount_pages")
    private Integer amountPages;

    @Column(name = "amount_copies")
    @Min(0)
    private Integer amountCopies;

    @Column(name = "amount_copies_borrowed")
    @Min(0)
    private Integer amountCopiesBorrowed;

    @Column(name = "amount_copies_remaining")
    @Min(0)
    private Integer amountCopiesRemaining;

    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @Column(name = "created_at", columnDefinition = "DATETIME")
    private LocalDateTime createdAt;

    private boolean enabled;

    @ManyToOne(targetEntity = Author.class)
    @JoinColumn(name = "id_author")
    private Author author;

    @ManyToOne(targetEntity = Publisher.class)
    @JoinColumn(name = "id_publisher")
    private Publisher publisher;
}

package com.virtuallibrary.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookDto {

    @NotBlank(message = "Isbn cannot be null.")
    @Size(min = 13, max = 13, message = "Isbn must contains 13 digits.")
    private String isbn;

    @NotBlank(message = "Title cannot be null.")
    private String title;

    @NotBlank(message = "Description cannot be null.")
    @Size(min = 25, max = 255, message = "Description contains min 25 characters.")
    private String description;

    @NotNull(message = "Date of publication cannot be null.")
    @PastOrPresent(message = "Date of publication is invalid.")
    private LocalDate datePublication;

    @NotNull(message = "Amount of pages is invalid.")
    @Min(value = 5, message = "Amount of pages cannot be < 5.")
    private Integer amountPages;

    @NotNull(message = "Amount of copies invalid.")
    @Min(value = 1, message = "Amount of copies invalid.")
    private Integer amountCopies;

    @NotNull(message = "Amount of copies borrowed invalid.")
    @Min(value = 0, message = "Amount of copies borrowed invalid.")
    private Integer amountCopiesBorrowed;

    @NotNull(message = "Author cannot be null.")
    private Long idAuthor;

    @NotNull(message = "Publisher cannot be null.")
    private Long idPublisher;
}

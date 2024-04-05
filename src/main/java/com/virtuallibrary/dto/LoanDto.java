package com.virtuallibrary.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoanDto {

    @NotNull(message = "Date devolution cannot be null.")
    @Future(message = "The devolution date cannot be present or past.")
    private LocalDate dateDevolution;

    @NotNull(message = "The book cannot be null.")
    private Long idBook;

    //    @NotBlank(message = "The user cannot be null.")
    private Long idUser;
}

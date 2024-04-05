package com.virtuallibrary.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PublisherDto {

    @NotBlank(message = "Name of publisher cannot be null.")
    @Size(min = 3, max = 25, message = "Length of name publisher cannot be less 3 and greater than 5.")
    private String name;
}

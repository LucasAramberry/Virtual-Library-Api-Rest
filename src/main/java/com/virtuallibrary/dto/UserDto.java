package com.virtuallibrary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.virtuallibrary.validations.annotations.ValidEmail;
import com.virtuallibrary.validations.annotations.ValidPassword;
import com.virtuallibrary.validations.annotations.ValidPasswordMatches;
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
@ValidPasswordMatches
public class UserDto {

    @NotBlank(message = "Name cannot be null.")
    @Size(min = 3, max = 12, message = "Name min length is 3.")
    private String name;

    @NotBlank(message = "Lastname cannot be null.")
    @Size(min = 3, max = 25, message = "Lastname min length is 3.")
    private String lastName;

    @NotBlank(message = "Phone cannot be null.")
    @Size(min = 8, max = 20, message = "Phone min length is 8.")
    private String phone;

    @ValidEmail
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @ValidPassword
    private String password;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String matchingPassword;
}

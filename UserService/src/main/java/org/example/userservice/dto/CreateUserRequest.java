package org.example.userservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.example.userservice.enums.UserIdentificationType;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateUserRequest {
    String name;

    String email;

    @NotBlank
    String phoneNo;

    @NotBlank
    String password;

    @NotNull
    UserIdentificationType useridentificationType;

    @NotNull
    String userIdentificationValue;
}

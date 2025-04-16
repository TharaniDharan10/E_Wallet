package com.example.transactionservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class InitiateTransactionRequest {
    @NotBlank
    String receiverPhoneNo;

    @Positive
    Double amount;

    String message; //didnot add anything in annotation as it is optional
}

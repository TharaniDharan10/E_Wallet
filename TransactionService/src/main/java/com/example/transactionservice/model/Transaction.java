package com.example.transactionservice.model;

import com.example.transactionservice.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int id;

    String senderPhoneNo;

    String receiverPhoneNo;

    String transactionId;

    String transactionStatusMessage;

    Double amount;

    String purpose;

    @Enumerated(value = EnumType.STRING)
    TransactionStatus transactionStatus;

    @CreationTimestamp
    Date createdOn;

    @UpdateTimestamp
    Date updatedOn;
}

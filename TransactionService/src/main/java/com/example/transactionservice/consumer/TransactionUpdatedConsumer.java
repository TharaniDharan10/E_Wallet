package com.example.transactionservice.consumer;

import com.example.transactionservice.enums.TransactionStatus;
import com.example.transactionservice.repository.TransactionRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.example.transactionservice.constants.KafkaConstants.TRANSACTION_UPDATED_TOPIC;
import static com.example.transactionservice.constants.TransactionUpdatedConstants.*;

@Service
@Slf4j
public class TransactionUpdatedConsumer {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    TransactionRepository repository;

    @KafkaListener(topics = TRANSACTION_UPDATED_TOPIC, groupId = "transaction-group")
    public void transactionInitiated(String message) throws JsonProcessingException {
        log.info("Transaction updated, message received in transactionService: {}", message);

        ObjectNode node = objectMapper.readValue(message, ObjectNode.class);  //converting string back to dto value   //2nd parameter is the type to which we want to convert the read data
        String status = node.get(STATUS).textValue();
        String statusMessage = node.get(STATUSMESSAGE).textValue();
        String transactionId = node.get(TRANSACTIONID).textValue();
        repository.updateTransactionStatus(TransactionStatus.valueOf(status), statusMessage, transactionId);    //value of is used since status was of type enum.SO we used inorder to convert string into enum
        log.info("Transaction updated successfully in transactionService");
    }
}

package com.example.transactionservice.controller;

import com.example.transactionservice.dto.InitiateTransactionRequest;
import com.example.transactionservice.service.TransactionService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping("/transaction")
    public String initiateTransaction(@RequestBody @Valid InitiateTransactionRequest request) {
        log.info("Controller invoked");
        //fetching sendersPhoneNo from SecurityContext
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String sendersPhoneNo = userDetails.getUsername();
        log.info("sendersPhoneNo received as: " + sendersPhoneNo);
        return transactionService.initiateTransaction(sendersPhoneNo, request);
    }
}

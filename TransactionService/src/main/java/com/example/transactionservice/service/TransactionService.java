package com.example.transactionservice.service;

import com.example.transactionservice.client.UserServiceClient;
import com.example.transactionservice.dto.InitiateTransactionRequest;
import com.example.transactionservice.enums.TransactionStatus;
import com.example.transactionservice.model.Transaction;
import com.example.transactionservice.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import  com.example.transactionservice.constants.KafkaConstants;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static com.example.transactionservice.constants.KafkaConstants.TRANSACTION_INITIATED_TOPIC;
import static com.example.transactionservice.constants.TransactionInitiatedConstants.*;
import static javax.swing.Action.NAME;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {

    @Autowired
    UserServiceClient userServiceClient;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    KafkaTemplate kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Override
    public UserDetails loadUserByUsername(String phoneNo) throws UsernameNotFoundException {
        String auth = "transaction-service:transaction-service";
        byte[] encodeAuth = Base64.getEncoder().encode(auth.getBytes());

        String authValue = "Basic "+new String(encodeAuth);
        log.info("Auth value {}", authValue);
        ObjectNode node = userServiceClient.getUser(phoneNo, authValue);    //this is senders phoneNo. API call made to other UserService
        log.info("User fetched  {}", node); //here node is the user whose credentials are entered in postman i.e the sender

        if(node == null){
            throw new UsernameNotFoundException("User does not exist");
        }
        //this node has authorities, as a list of maps, so we need to derive out authorities from it, we can see that by debugging or by printing node
        ArrayNode authorities = (ArrayNode) node.get("authorities");

        final List<GrantedAuthority> authorityList = new ArrayList<>();

        authorities.iterator().forEachRemaining(arrayNode -> {
            authorityList.add(new SimpleGrantedAuthority(arrayNode.get("authority").textValue()));
        });

        User user = new User(node.get("phoneNo").textValue(),
                node.get("password").textValue(), authorityList);
        return user;
    }


    public String initiateTransaction(String sendersPhoneNo, InitiateTransactionRequest request) {

        Transaction transaction = Transaction.builder()
                .transactionId(UUID.randomUUID().toString())
                .senderPhoneNo(sendersPhoneNo)
                .receiverPhoneNo(request.getReceiverPhoneNo())
                .amount(request.getAmount())
                .purpose(request.getMessage())
                .transactionStatus(TransactionStatus.INITIATED)
                .build();

        transactionRepository.save(transaction);
        log.info("Transaction initiated {}", transaction);

        //kafka producer part

//      to send these data to kafka, we can either create our own dto and then map it to string using objectMapper.writeValueAsString (or) can use class ObjectNode (or) class JsonObject
//        Dto can be created if i know that i will be using this at multiple places.Just that here i know that i used only ones, so used ObjectNode

        ObjectNode objectNode = objectMapper.createObjectNode();    //helps to create key value pairs of an object
        objectNode.put(SENDERPHONENO,transaction.getSenderPhoneNo());
        objectNode.put(RECEIVERPHONENO,transaction.getReceiverPhoneNo());
        objectNode.put(AMOUNT, transaction.getAmount());
        objectNode.put(TRANSACTIONID,transaction.getTransactionId());

        String kafkaMessage = objectNode.toString();    //stringifed
        kafkaTemplate.send(TRANSACTION_INITIATED_TOPIC, kafkaMessage); //specifying topic and message to be send to kafka

        log.info("Message published to kafka: " + kafkaMessage);

        return transaction.getTransactionId();

    }

    public List<Transaction> findTransactions(String senderPhoneNo, Integer pageNo, Integer limit) {
        Pageable pageable = PageRequest.of(pageNo, limit);  //Pageable is request
        return transactionRepository.findBySenderPhoneNo(senderPhoneNo,pageable); //This is used when we want to return
//        Page<Transaction> response = transactionRepository.findBySenderPhoneNo(senderPhoneNo,pageable); //Page is response  //This Page class is used when we want to know how many pages are there and stuff.
//        return response.getContent();   //this Page has some more methods to explore
    }
}

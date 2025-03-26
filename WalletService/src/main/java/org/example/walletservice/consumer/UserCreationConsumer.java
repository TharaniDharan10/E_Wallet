package org.example.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.example.walletservice.model.Wallet;
import org.example.walletservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static javax.sound.sampled.FloatControl.Type.BALANCE;
import static org.example.walletservice.constants.KafkaConstants.USER_CREATION_TOPIC;
import static org.example.walletservice.constants.UserCreationTopicConstants.PHONENO;
import static org.example.walletservice.constants.UserCreationTopicConstants.USERID;

@Service
@Slf4j
public class UserCreationConsumer {

    @Value("${wallet.initial.amount}")
    Double walletAmount;

    @Autowired
    ObjectMapper mapper;

    @Autowired
    WalletRepository walletRepository;

    @KafkaListener(topics = USER_CREATION_TOPIC, groupId = "wallet-group")//both are mandatory.It creates one consumer in this consumer groupId and since message is passed into that topic, passed msg are read by this consumer from this consumer groupId //group id is consumer group id,so that only one instance of consumer from that particular group listens
    public void userCreated(String message) throws JsonProcessingException {
        log.info("User created, message received: {}", message);

        ObjectNode node = mapper.readValue(message, ObjectNode.class);  //converting string back to dto value   //2nd parameter is the type to which we want to convert the read data

        String phoneNo = node.get(PHONENO).textValue();
        Integer userId = node.get(USERID).intValue();

        Wallet wallet = Wallet.builder()
                .phoneNo(phoneNo)
                .userId(userId)
                .balance(walletAmount)
                .build();

        walletRepository.save(wallet);
        log.info("Wallet saved for user {}", userId);

    }
}

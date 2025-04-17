package org.example.walletservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.example.walletservice.model.Wallet;
import org.example.walletservice.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import static org.example.walletservice.constants.KafkaConstants.TRANSACTION_INITIATED_TOPIC;
import static org.example.walletservice.constants.KafkaConstants.TRANSACTION_UPDATED_TOPIC;
import static org.example.walletservice.constants.TransactionInitiatedConstants.*;
import static org.example.walletservice.constants.TransactionUpdatedConstants.STATUS;
import static org.example.walletservice.constants.TransactionUpdatedConstants.STATUSMESSAGE;

@Slf4j
@Service
public class TransactionInitiatedConsumer {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    WalletRepository walletRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = TRANSACTION_INITIATED_TOPIC, groupId = "wallet-group")
    public void transactionInitiated(String message) throws JsonProcessingException {
        log.info("Transaction initiated, message received in walletService: {}", message);

        ObjectNode node = objectMapper.readValue(message, ObjectNode.class);  //converting string back to dto value   //2nd parameter is the type to which we want to convert the read data

        String senderPhoneNo = node.get(SENDERPHONENO).textValue();
        String receiverPhoneNo = node.get(RECEIVERPHONENO).textValue();
        String transactionId = node.get(TRANSACTIONID).textValue();
        Double amount = node.get(AMOUNT).doubleValue();

        //fetching wallets of the users
        Wallet senderWallet = walletRepository.findByPhoneNo(senderPhoneNo);
        Wallet receiverWallet = walletRepository.findByPhoneNo(receiverPhoneNo);

        String status;
        String statusMessage;

        if (senderWallet == null) {
            log.info("Sender Wallet is not present");
            status = "FAILED";
            statusMessage = "Sender wallet does not exist in our System";
        } else if (receiverWallet == null) {
            log.info("Receiver Wallet is not present");
            status = "FAILED";
            statusMessage = "Receiver wallet does not exist in our System";
        } else if (amount > senderWallet.getBalance()) {
            log.info("Sender Wallet does not have enough balance");
            status = "FAILED";
            statusMessage = "Sender wallet does not have enough balance to make transaction";
        } else {
            //successful transaction
            log.info("Transaction made successful");
            updateWallets(senderWallet, receiverWallet, amount);
            status = "SUCCESSFUL";
            statusMessage = "Transaction completed successfully";
            log.info("Wallet updated successfully after transaction");
        }

        //publish message back to kafka
        sendMessageToKafka(transactionId, status, statusMessage);
        log.info("Message send to kafka: {}", statusMessage);
    }

    private void sendMessageToKafka(String transactionId, String status, String statusMessage) {
        ObjectNode node = objectMapper.createObjectNode();
        node.put(TRANSACTIONID, transactionId);
        node.put(STATUS, status);
        node.put(STATUSMESSAGE, statusMessage);
        kafkaTemplate.send(TRANSACTION_UPDATED_TOPIC, node.toString());
    }

    @Transactional
    public void updateWallets(Wallet senderWallet, Wallet receiverWallet, double amount) {

        walletRepository.updateWallet(senderWallet.getPhoneNo(), -amount);  //debiting money, so -ve

        walletRepository.updateWallet(receiverWallet.getPhoneNo(), amount); //crediting money
    }
}

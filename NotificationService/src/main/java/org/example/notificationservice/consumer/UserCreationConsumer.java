package org.example.notificationservice.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.beans.beancontext.BeanContextChild;

import static org.example.notificationservice.constants.KafkaConstants.USER_CREATION_TOPIC;
import static org.example.notificationservice.constants.UserCreationTopicConstants.EMAIL;
import static org.example.notificationservice.constants.UserCreationTopicConstants.NAME;

@Service
@Slf4j
public class UserCreationConsumer {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    JavaMailSender javaMailSender;

    @KafkaListener(topics = USER_CREATION_TOPIC, groupId = "notification-group")//both are mandatory.It creates one consumer in this consumer groupId and since message is passed into that topic, passed msg are read by this consumer from this consumer groupId //group id is consumer group id,so that only one instance of consumer from that particular group listens
    public void userCreated(String message) throws JsonProcessingException {
        log.info("User created, message received: {}", message);

        ObjectNode node = mapper.readValue(message, ObjectNode.class);  //converting string back to dto value   //2nd parameter is the type to which we want to convert the read data

        String name = node.get(NAME).textValue();
        String email = node.get(EMAIL).textValue(); //if it was of type say double. then use doubleValue() instead of textValue()

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        //FROM
        //TO
        //SUBJECT
        //BODY
        //TOP FOUR ARE MANDATORY WHEREAS BOTTOM 2 ARE OPTIONAL
        //CC
        //BCC
        //for this, there are many applications we have got to use, but we use mailtrap.io
        mailMessage.setFrom("wallet-service@gmail.com");//custom mail
        mailMessage.setTo(email);
        mailMessage.setSubject("Welcome to E-Wallet");
        mailMessage.setText("Hey "+name+", welcome to the E-Wallet service");
        javaMailSender.send(mailMessage);

        log.info("User creation, mail sent");   //when we run both user and notification service and hit /user, we can see response in logs, also in mailtrap.io inbox by refreshing.

    }
}

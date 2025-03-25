package org.example.userservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.constants.UserCreationTopicConstants;
import org.example.userservice.dto.CreateUserRequest;
import org.example.userservice.enums.UserType;
import org.example.userservice.mapper.UserMapper;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static org.example.userservice.constants.KafkaConstants.USER_CREATION_TOPIC;
import static org.example.userservice.constants.UserCreationTopicConstants.*;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    ObjectMapper objectMapper;  //we can also use Gson.I use objectMapper here to convert json into string as i need to pass value to kafka

    @Autowired
    PasswordEncoder passwordEncoder;    //object of BCryptPasswordEncoder which was created by bean

    @Override
    public User loadUserByUsername(String phoneNo) throws UsernameNotFoundException {    //i modified UserDetails to User
        User user = userRepository.findByPhoneNo(phoneNo);  //username is phoneNo
        if (user == null) {
            throw new UsernameNotFoundException("User does not exist");
        }
        return user;
    }

    public User createUser(@Valid CreateUserRequest userRequest) {
        User user = UserMapper.mapToUser(userRequest);
        user.setUserType(UserType.USER);
        user.setAuthorities("USER");
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));    //encrypts password b4 saving to db

        log.info("User created: " + user);
        userRepository.save(user);
        log.info("User saved: " + user);

        //publish data to kafka
        //Things that other services would require from UserService:
           //NotificationService would require username, email
           //WalletService would require phoneNo, userStatus

//      to send these data to kafka, we can either create our own dto and then map it to string using objectMapper.writeValueAsString (or) can use class ObjectNode (or) class JsonObject
//        Dto can be created if i knoww that i will be using this at multiple places.Just that here i know that i used only ones, so used ObjectNode
        ObjectNode objectNode = objectMapper.createObjectNode();    //helps to create key value pairs of an object
        objectNode.put(EMAIL,user.getEmail());
        objectNode.put(PHONENO,user.getPhoneNo());
        objectNode.put(NAME,user.getName());
        objectNode.put(USERID,user.getId());

        String kafkaMessage = objectNode.toString();    //stringifed
        kafkaTemplate.send(USER_CREATION_TOPIC, kafkaMessage); //specifying topic and message to be send to kafka

        log.info("Message published to kafka: " + kafkaMessage);

        return user;

    }//when i run this application and hit /user, it published data to kafka .That can be viewed by
//    cd C:\kafka_2.12-3.9.0\bin\windows
//    .\kafka-topics.bat --bootstrap-server localhost:9092 –list	//lists topics.We can see a new name created for this.Ensure while hitting /user, check if is it 8080 or some other port and hit accordingly.Also by default its relpication is 1 and its partition is 1
    //In companies what they do is, they create topics separately with required configurations and then add then to application.


}

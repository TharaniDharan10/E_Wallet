package com.example.transactionservice.service;

import com.example.transactionservice.client.UserServiceClient;
import com.example.transactionservice.dto.InitiateTransactionRequest;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
@Slf4j
public class TransactionService implements UserDetailsService {

    @Autowired
    UserServiceClient userServiceClient;

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


    public String initiateTransaction(@Valid InitiateTransactionRequest request) {
        return null;
    }
}

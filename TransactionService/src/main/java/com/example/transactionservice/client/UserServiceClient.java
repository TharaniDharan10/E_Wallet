package com.example.transactionservice.client;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "userServiceClient" , url = "http://localhost:8001")  //value can be any unique identifier
public interface UserServiceClient {

    @GetMapping("/user")
    ObjectNode getUser(@RequestParam("phoneNo") String phoneNo, @RequestHeader("Authorization") String authValue);  //if we had used rest template, then we would have had to map all parameters, but here in feign client, we just need to pass this line of declaration and backend handles all.
    //instead of ObjectNode, we can also use JsonNode

}

package org.example.userservice.controller;

import jakarta.validation.Valid;
import org.example.userservice.dto.CreateUserRequest;
import org.example.userservice.model.User;
import org.example.userservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/user")
    public User createUser(@RequestBody @Valid CreateUserRequest userRequest) {
    return userService.createUser(userRequest);
    }

}

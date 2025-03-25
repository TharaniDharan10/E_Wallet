package org.example.userservice.mapper;

import lombok.experimental.UtilityClass;
import org.example.userservice.dto.CreateUserRequest;
import org.example.userservice.enums.UserStatus;
import org.example.userservice.enums.UserType;
import org.example.userservice.model.User;

@UtilityClass
public class UserMapper {

    public User mapToUser(CreateUserRequest request) {
        return User.builder()
                .name(request.getName())
                .email(request.getEmail())
//                .password(request.getPassword())  //removed this as i want to encode password received b4 getting it saved into db
                .phoneNo(request.getPhoneNo())
                .useridentificationType(request.getUseridentificationType())
                .userIdentificationValue(request.getUserIdentificationValue())
                .userStatus(UserStatus.ACTIVE)
                .build();
    }
}

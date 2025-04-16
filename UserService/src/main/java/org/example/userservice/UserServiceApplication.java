package org.example.userservice;

import org.example.userservice.enums.UserStatus;
import org.example.userservice.enums.UserType;
import org.example.userservice.model.User;
import org.example.userservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class UserServiceApplication implements CommandLineRunner {

	@Autowired
	UserRepository userRepository;

	@Autowired
	PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		//we use CommandLineRunner whenever we want to make use of something which is not static.Also we use this method whenever we want to load something when app is up
		//We would have created an API to insert this aswell, but since we had to store only one value, creating an api is not preferred.
		//This we store in User table sp that it could be used for service to service call
		User transactionService = User.builder()
				.phoneNo("transaction-service")	//instead of phoneNo, we could have renamed as UserIdentifier or something else
				.password(passwordEncoder.encode("transaction-service"))
				.userStatus(UserStatus.ACTIVE)
				.userType(UserType.SERVICE)
				.authorities("SERVICE")
				.build();

		if (userRepository.findByPhoneNo("transaction-service") == null){
			User user = userRepository.save(transactionService);	//bcoz only for the first time it saves in db, running app from 2nd time onwards, it throws error as duplicate entry, so to prevent that we do this
		}

	}
}

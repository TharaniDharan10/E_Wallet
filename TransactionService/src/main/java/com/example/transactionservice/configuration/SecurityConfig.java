package com.example.transactionservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@Slf4j
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        log.info("Inside Security filterchain");
        http.authorizeHttpRequests(authorize->authorize
                        .requestMatchers(HttpMethod.POST, "/transaction").hasAuthority("USER")  //bcoz anyone with authority user should only be able to initiate a transaction, not a service
                        .anyRequest().permitAll())
                .formLogin(withDefaults())//browser
                .httpBasic(withDefaults())//clients
                .csrf(csrf->csrf.disable());
        log.info("Security filterchain completed");
        return http.build();

    }
    @Bean
    public PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }   //always use same password encoder class for sender and receiver services, only then it works
}

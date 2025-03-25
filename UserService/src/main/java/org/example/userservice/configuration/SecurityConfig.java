package org.example.userservice.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(authorize->authorize
                        .requestMatchers(HttpMethod.POST, "/user").permitAll()
//                        .requestMatchers(HttpMethod.GET, "/user").hasAnyAuthority("USER","SERVICE")
                        .anyRequest().permitAll())
                .formLogin(withDefaults())//browser
                .httpBasic(withDefaults())//clients
                .csrf(csrf->csrf.disable());
        return http.build();
    }
    @Bean
    public PasswordEncoder getEncoder(){
        return new BCryptPasswordEncoder();
    }
}

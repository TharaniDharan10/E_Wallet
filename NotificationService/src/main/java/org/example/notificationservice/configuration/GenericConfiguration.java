package org.example.notificationservice.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenericConfiguration {
    @Bean
    ObjectMapper objectMapper() {   //it provides easy ways to basically convert json into string and vice versa
        //this can also be created within the method of some service also,but for objects like ObjectMapper and things which are used globally, create one bean only in configuration
        return new ObjectMapper();
    }
}

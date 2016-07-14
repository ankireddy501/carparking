package com.parking.management;

import com.parking.management.externalservices.AuthenticationHelper;
import com.parking.management.externalservices.CollaborationAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfiguration {

    @Bean
    CollaborationAdapter getCollaborationAdapter() {
        return new CollaborationAdapter();
    }

    @Bean
    AuthenticationHelper getAuthenticationHelper() {
        return new AuthenticationHelper();
    }

    @Bean
    PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}

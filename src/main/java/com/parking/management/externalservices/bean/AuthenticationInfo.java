package com.parking.management.externalservices.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationInfo {

    @Autowired
    Environment environment;

    public String getHost() {
        return environment.getProperty("host");
    }

    public String getPort() {
        return environment.getProperty("port");
    }

    public String getUserName() {
        return environment.getProperty("user");
    }

    public String getPassword() {
        return environment.getProperty("password");
    }

    public String getTenantId() {
        return environment.getProperty("tenant");
    }
}
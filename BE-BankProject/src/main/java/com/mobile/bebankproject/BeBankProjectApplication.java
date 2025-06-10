package com.mobile.bebankproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeBankProjectApplication {

    public static void main(String[] args) {
        System.setProperty("javax.net.ssl.trustStore", "NONE");
        SpringApplication.run(BeBankProjectApplication.class, args);
    }
}

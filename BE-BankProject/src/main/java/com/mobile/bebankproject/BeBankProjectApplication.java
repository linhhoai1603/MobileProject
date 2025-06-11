package com.mobile.bebankproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BeBankProjectApplication {

    public static void main(String[] args) {
        System.out.println("JAVA_HOME is: " + System.getProperty("java.home"));
        SpringApplication.run(BeBankProjectApplication.class, args);
    }
}

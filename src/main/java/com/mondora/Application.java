package com.mondora;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jms.annotation.EnableJms;

import java.util.Objects;

@SpringBootApplication
@EnableJms
public class Application {

    public static void main(String[] args) {

        Objects.requireNonNull(System.getenv("BUS_USER"), "BUS_USER must not be null");
        Objects.requireNonNull(System.getenv("BUS_PASS"), "BUS_PASS must not be null");
        Objects.requireNonNull(System.getenv("BUS_HOST"), "BUS_HOST must not be null");
        Objects.requireNonNull(System.getenv("TOPIC_NAME"), "TOPIC_NAME must not be null");
        Objects.requireNonNull(System.getenv("SUBSCRIPTION_NAME"), "SUBSCRIPTION_NAME must not be null");

        SpringApplication.run(Application.class, args);
    }

}
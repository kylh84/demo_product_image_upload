package com.example.ecommerce;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class EcommerceApplication {
    public static void main(String[] args) {

        Dotenv dotenv = Dotenv.load();
        System.setProperty("DATABASE_URL", dotenv.get("DATABASE_URL"));
        System.setProperty("DATABASE_USERNAME", dotenv.get("DATABASE_USERNAME"));
        System.setProperty("DATABASE_PASSWORD", dotenv.get("DATABASE_PASSWORD"));
        System.setProperty("JPA_HIBERNATE_DDL_AUTO", dotenv.get("JPA_HIBERNATE_DDL_AUTO"));
        System.setProperty("JPA_HIBERNATE_DIALECT", dotenv.get("JPA_HIBERNATE_DIALECT"));

        SpringApplication.run(EcommerceApplication.class, args);
    }
}
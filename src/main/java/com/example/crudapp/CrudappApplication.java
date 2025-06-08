package com.example.crudapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class for the CRUD application.
 * This application provides REST APIs for User management with H2 database integration.
 */
@SpringBootApplication
public class CrudappApplication {

    /**
     * Main method to start the Spring Boot application.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(CrudappApplication.class, args);
    }
}

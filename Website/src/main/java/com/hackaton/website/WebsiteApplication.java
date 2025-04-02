package com.hackaton.website;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the Spring Boot application.
 * Responsible for bootstrapping and starting the application.
 * 
 * @author Gabriel Proença
 */
@SpringBootApplication
public class WebsiteApplication {

    /**
     * The main method that serves as the entry point for the application.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(WebsiteApplication.class, args);
    }
}

package ru.home.security_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.telegram.telegrambots.ApiContextInitializer;

@SpringBootApplication
public class SecurityBotApplication {

    public static void main(String[] args) {
        ApiContextInitializer.init();
        SpringApplication.run(SecurityBotApplication.class, args);
    }

}

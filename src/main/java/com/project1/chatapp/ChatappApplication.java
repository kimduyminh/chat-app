package com.project1.chatapp;

import com.project1.chatapp.config.tlsConfig.KeystoreService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication()
public class ChatappApplication {

	public static void main(String[] args) throws Exception {
		SpringApplication.run(ChatappApplication.class, args);
	}
}

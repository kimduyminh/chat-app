package com.project1.chatapp.config.tlsConfig;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.project1.chatapp")
public class KeystoreConstruct {
    @Autowired
    private KeystoreService keystoreService;

    @PostConstruct
    public void init() throws Exception {
        System.out.println("PostConstruct");
        keystoreService.generateKeystore();
        String keyStorePassword = keystoreService.getKeyPass();
        String trustStorePassword = keystoreService.getStorePass();

        System.setProperty("app.key-store-password", keyStorePassword);
        System.setProperty("app.trust-store-password", trustStorePassword);
    }
}

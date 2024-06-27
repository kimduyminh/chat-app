//package com.project1.chatapp.config;
//
//import org.springframework.stereotype.Service;
//
//import java.security.SecureRandom;
//import java.util.Base64;
//
//@Service
//public class rAnDoMnEsS {
//    public static String generateRandom(int length) {
//        SecureRandom secureRandom = new SecureRandom();
//        byte[] key = new byte[length];
//        secureRandom.nextBytes(key);
//        return Base64.getEncoder().encodeToString(key);
//    }
//    public static int generateRandomInt(int min, int max) {
//        SecureRandom secureRandom = new SecureRandom();
//        return secureRandom.nextInt((max - min) + 1) + min;
//    }
//}

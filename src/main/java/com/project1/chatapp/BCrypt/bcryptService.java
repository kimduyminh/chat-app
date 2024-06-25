package com.project1.chatapp.BCrypt;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class bcryptService {
    public String hashPassword(String pass){
        return BCrypt.withDefaults().hashToString(12, pass.toCharArray());
    }
    public boolean checkPassword(String pass, String hash) {
        if (pass == null || hash == null) {
            return false;
        }
        try {
            return BCrypt.verifyer().verify(pass.toCharArray(), hash).verified;
        } catch (Exception e) {
            return false;
        }
    }
}

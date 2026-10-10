package com.leilao.login.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final BCryptPasswordEncoder encoder =
            new BCryptPasswordEncoder();

    public String criptografar(
            String senha
    ) {

        return encoder.encode(senha);
    }

    public boolean confere(
            String senha,
            String hash
    ) {

        return encoder.matches(
                senha,
                hash
        );
    }
}
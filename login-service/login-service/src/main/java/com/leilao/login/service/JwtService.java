package com.leilao.login.service;

import com.leilao.login.model.Usuario;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    public String gerarToken(
            Usuario usuario
    ) {

        SecretKey key =
                Keys.hmacShaKeyFor(
                        secret.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        return Jwts.builder()

                .subject(
                        usuario.getId()
                )

                .claim(
                        "role",
                        usuario.getRole()
                )

                .claim(
                        "nome",
                        usuario.getNome()
                )

                .issuedAt(
                        new Date()
                )

                .expiration(
                        new Date(
                                System.currentTimeMillis()
                                        + 2 * 60 * 60 * 1000
                        )
                )

                .signWith(key)

                .compact();
    }
}
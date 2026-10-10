package com.leilao.login.config;

import com.leilao.login.model.Usuario;

import com.leilao.login.repository.UsuarioRepository;

import com.leilao.login.service.PasswordService;

import org.springframework.boot.CommandLineRunner;

import org.springframework.stereotype.Component;

@Component
public class AdminInitializer
        implements CommandLineRunner {

    private final UsuarioRepository repository;

    private final PasswordService passwordService;

    public AdminInitializer(
            UsuarioRepository repository,
            PasswordService passwordService
    ) {

        this.repository =
                repository;

        this.passwordService =
                passwordService;
    }

    @Override
    public void run(
            String... args
    ) {

        String email =
                "admin@leilao.com";

        if (
                repository
                        .findByEmail(email)
                        .isPresent()
        ) {

            return;
        }

        Usuario admin =
                new Usuario();

        admin.setNome(
                "Administrador"
        );

        admin.setEmail(
                email
        );

        admin.setSenhaHash(
                passwordService
                        .criptografar(
                                "Admin123!"
                        )
        );

        admin.setRole(
                "ADM"
        );

        repository.save(
                admin
        );

        System.out.println(
                "Administrador inicial criado."
        );
    }
}
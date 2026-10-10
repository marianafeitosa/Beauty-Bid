package com.leilao.login.controller;

import com.leilao.login.dto.CadastroDTO;
import com.leilao.login.dto.LoginDTO;
import com.leilao.login.dto.UsuarioResponse;

import com.leilao.login.model.Usuario;

import com.leilao.login.service.JwtService;
import com.leilao.login.service.UsuarioService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import org.springframework.web.bind.annotation.*;

import java.time.Duration;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UsuarioService usuarioService;

    private final JwtService jwtService;

    public AuthController(
            UsuarioService usuarioService,
            JwtService jwtService
    ) {

        this.usuarioService =
                usuarioService;

        this.jwtService =
                jwtService;
    }

    @PostMapping("/register")
    public UsuarioResponse cadastrar(
            @Valid
            @RequestBody
            CadastroDTO dto
    ) {

        return usuarioService
                .cadastrarCliente(dto);
    }

    @PostMapping("/login")
    public UsuarioResponse login(
            @Valid
            @RequestBody
            LoginDTO dto,

            HttpServletResponse response
    ) {

        Usuario usuario =
                usuarioService
                        .autenticar(dto);

        String token =
                jwtService
                        .gerarToken(
                                usuario
                        );

        ResponseCookie cookie =
                ResponseCookie

                        .from(
                                "AUTH_TOKEN",
                                token
                        )

                        .httpOnly(true)

                        /*
                         * localhost usa HTTP.
                         * Em produção/HTTPS seria true.
                         */
                        .secure(false)

                        .sameSite("Lax")

                        .path("/")

                        .maxAge(
                                Duration.ofHours(2)
                        )

                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );

        return UsuarioResponse.from(
                usuario
        );
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletResponse response
    ) {

        ResponseCookie cookie =
                ResponseCookie

                        .from(
                                "AUTH_TOKEN",
                                ""
                        )

                        .httpOnly(true)

                        .secure(false)

                        .sameSite("Lax")

                        .path("/")

                        .maxAge(0)

                        .build();

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                cookie.toString()
        );
    }

    @GetMapping("/me")
    public UsuarioResponse me(
            @RequestHeader(
                    "X-User-Id"
            )
            String usuarioId
    ) {

        return usuarioService
                .buscar(
                        usuarioId
                );
    }
}
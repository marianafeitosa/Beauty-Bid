package com.leilao.login.dto;

public record AtualizarUsuarioDTO(
        String nome,
        String email,
        String senha,
        String role
) {
}
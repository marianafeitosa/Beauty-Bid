package com.leilao.login.dto;

import com.leilao.login.model.Usuario;

public record UsuarioResponse(
        String id,
        String nome,
        String email,
        String role
) {

    public static UsuarioResponse from(
            Usuario usuario
    ) {

        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }
}
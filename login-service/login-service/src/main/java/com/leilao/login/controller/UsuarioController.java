package com.leilao.login.controller;

import com.leilao.login.dto.AtualizarUsuarioDTO;
import com.leilao.login.dto.UsuarioResponse;

import com.leilao.login.service.UsuarioService;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/auth/usuarios")
public class UsuarioController {

    private final UsuarioService service;

    public UsuarioController(
            UsuarioService service
    ) {

        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponse> listar(
            @RequestHeader(
                    "X-User-Role"
            )
            String role
    ) {

        verificarAdm(role);

        return service.listar();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(
            @RequestHeader(
                    "X-User-Role"
            )
            String role,

            @PathVariable
            String id
    ) {

        verificarAdm(role);

        return service.buscar(id);
    }

    @PutMapping("/{id}")
    public UsuarioResponse atualizar(
            @RequestHeader(
                    "X-User-Role"
            )
            String role,

            @PathVariable
            String id,

            @RequestBody
            AtualizarUsuarioDTO dto
    ) {

        verificarAdm(role);

        return service.atualizar(
                id,
                dto
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(
            HttpStatus.NO_CONTENT
    )
    public void excluir(
            @RequestHeader(
                    "X-User-Role"
            )
            String role,

            @PathVariable
            String id
    ) {

        verificarAdm(role);

        service.excluir(id);
    }

    private void verificarAdm(
            String role
    ) {

        if (!"ADM".equals(role)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Apenas administradores podem realizar esta operação"
            );
        }
    }
}
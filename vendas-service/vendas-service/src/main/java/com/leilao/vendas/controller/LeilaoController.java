package com.leilao.vendas.controller;

import com.leilao.vendas.dto.LeilaoRequest;

import com.leilao.vendas.model.Leilao;

import com.leilao.vendas.service.LeilaoService;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/leiloes")
public class LeilaoController {

    private final LeilaoService service;

    public LeilaoController(
            LeilaoService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<Leilao> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Leilao buscar(
            @PathVariable String id
    ) {
        return service.buscar(id);
    }

    @PostMapping
    public Leilao criar(
            @RequestHeader(
                    "X-User-Role"
            ) String role,

            @RequestBody
            LeilaoRequest request
    ) {

        verificarAdm(role);

        return service.criar(
                request
        );
    }

    @PutMapping("/{id}")
    public Leilao atualizar(
            @RequestHeader(
                    "X-User-Role"
            ) String role,

            @PathVariable
            String id,

            @RequestBody
            LeilaoRequest request
    ) {

        verificarAdm(role);

        return service.atualizar(
                id,
                request
        );
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(
            HttpStatus.NO_CONTENT
    )
    public void excluir(
            @RequestHeader(
                    "X-User-Role"
            ) String role,

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
package com.leilao.vendas.controller;

import com.leilao.vendas.dto.ResultadoProcessamento;
import com.leilao.vendas.service.LanceService;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/internal/lances")
public class InternalLanceController {

    private final LanceService lanceService;

    @Value("${internal.secret}")
    private String internalSecret;

    public InternalLanceController(
            LanceService lanceService
    ) {
        this.lanceService = lanceService;
    }

    @PostMapping("/{id}/processar")
    public ResultadoProcessamento processar(
            @PathVariable String id,

            @RequestHeader(
                    "X-Internal-Token"
            ) String token
    ) {

        if (!internalSecret.equals(token)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Acesso interno não autorizado"
            );
        }

        return lanceService.processarLance(id);
    }
}
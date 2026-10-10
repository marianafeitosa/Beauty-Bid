package com.leilao.transacao.controller;

import com.leilao.transacao.model.HistoricoStatus;
import com.leilao.transacao.model.TransacaoLance;

import com.leilao.transacao.service.TransacaoService;

import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {

    private final TransacaoService service;

    public TransacaoController(
            TransacaoService service
    ) {
        this.service = service;
    }

    @GetMapping("/lance/{lanceId}")
    public TransacaoLance buscar(
            @PathVariable String lanceId,

            @RequestHeader("X-User-Id")
            String usuarioId,

            @RequestHeader("X-User-Role")
            String role
    ) {

        TransacaoLance transacao =
                service.buscarPorLance(
                        lanceId
                );

        verificarPermissao(
                transacao,
                usuarioId,
                role
        );

        return transacao;
    }

    @GetMapping(
            "/lance/{lanceId}/historico"
    )
    public List<HistoricoStatus> historico(
            @PathVariable String lanceId,

            @RequestHeader("X-User-Id")
            String usuarioId,

            @RequestHeader("X-User-Role")
            String role
    ) {

        TransacaoLance transacao =
                service.buscarPorLance(
                        lanceId
                );

        verificarPermissao(
                transacao,
                usuarioId,
                role
        );

        return service
                .buscarHistorico(
                        lanceId
                );
    }

    private void verificarPermissao(
            TransacaoLance transacao,
            String usuarioId,
            String role
    ) {

        if ("ADM".equals(role)) {
            return;
        }

        if (!transacao
                .getUsuarioId()
                .equals(usuarioId)) {

            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Você não pode visualizar esta transação"
            );
        }
    }
}
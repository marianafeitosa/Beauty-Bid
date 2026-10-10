package com.leilao.vendas.controller;

import com.leilao.vendas.dto.LanceRequest;
import com.leilao.vendas.model.Lance;
import com.leilao.vendas.service.LanceService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;



@RestController
@RequestMapping("/leiloes")
public class LanceController {

    private final LanceService lanceService;

    public LanceController(
            LanceService lanceService
    ) {
        this.lanceService = lanceService;
    }

    @PostMapping("/{leilaoId}/lances")
    public Lance darLance(
            @PathVariable String leilaoId,

            @RequestHeader(
                    "X-User-Id"
            ) String usuarioId,

            @RequestBody LanceRequest request
    ) {

        return lanceService.solicitarLance(
                leilaoId,
                usuarioId,
                request.valorCentavos()
        );
    }

    @GetMapping("/{leilaoId}/lances")
    public List<Lance> listar(
            @PathVariable String leilaoId
    ) {

        return lanceService
                .listarPorLeilao(
                        leilaoId
                );
    }
    @GetMapping("/lances/meus")
public List<Lance> meusLances(
        @RequestHeader("X-User-Id")
        String usuarioId
) {
    return lanceService
            .listarPorUsuario(usuarioId);
}
}
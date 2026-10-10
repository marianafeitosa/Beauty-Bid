package com.leilao.relatorio.controller;

import com.leilao.relatorio.model.EventoLog;

import com.leilao.relatorio.service.RelatorioService;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    private final RelatorioService service;

    public RelatorioController(
            RelatorioService service
    ) {
        this.service = service;
    }

    @GetMapping("/resumo")
    public Map<String, Object> resumo() {
        return service.resumo();
    }

    @GetMapping("/eventos")
    public List<EventoLog> eventos() {
        return service.eventos();
    }
}
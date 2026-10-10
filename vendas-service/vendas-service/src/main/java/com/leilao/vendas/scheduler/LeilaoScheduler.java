package com.leilao.vendas.scheduler;

import com.leilao.vendas.repository.LeilaoRepository;

import com.leilao.vendas.service.LeilaoService;

import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class LeilaoScheduler {

    private final LeilaoRepository repository;

    private final LeilaoService service;

    public LeilaoScheduler(
            LeilaoRepository repository,
            LeilaoService service
    ) {

        this.repository =
                repository;

        this.service =
                service;
    }

    @Scheduled(
            fixedDelay = 10000
    )
    public void encerrarVencidos() {

        repository
                .findByStatusAndFimEmBefore(
                        "ABERTO",
                        Instant.now()
                )

                .forEach(
                        leilao ->
                                service.encerrar(
                                        leilao.getId()
                                )
                );
    }
}
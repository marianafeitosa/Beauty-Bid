package com.leilao.transacao.client;

import com.leilao.transacao.dto.ResultadoProcessamentoDTO;

import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Component;

import org.springframework.web.client.RestClient;

@Component
public class VendasClient {

    private final RestClient http;

    private final String internalSecret;

    public VendasClient(
            @Value("${vendas.url}")
            String vendasUrl,

            @Value("${internal.secret}")
            String internalSecret
    ) {

        this.http =
                RestClient.builder()
                        .baseUrl(vendasUrl)
                        .build();

        this.internalSecret =
                internalSecret;
    }

    public ResultadoProcessamentoDTO processar(
            String lanceId
    ) {

        return http
                .post()

                .uri(
                        "/internal/lances/{id}/processar",
                        lanceId
                )

                .header(
                        "X-Internal-Token",
                        internalSecret
                )

                .retrieve()

                .body(
                        ResultadoProcessamentoDTO.class
                );
    }
}
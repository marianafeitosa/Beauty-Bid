package com.leilao.vendas.dto;

import java.time.Instant;

public record LeilaoRequest(
        String titulo,
        String marca,
        String descricao,
        String imagemUrl,
        long lanceInicialCentavos,
        Instant fimEm
) {
}
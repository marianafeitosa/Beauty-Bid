package com.leilao.vendas.dto;

public record ResultadoProcessamento(
        boolean aprovado,
        String motivo
) {
}
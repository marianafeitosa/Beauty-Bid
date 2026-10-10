package com.leilao.transacao.dto;

public record ResultadoProcessamentoDTO(
        boolean aprovado,
        String motivo
) {
}
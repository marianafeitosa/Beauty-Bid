package com.leilao.common;

import java.time.Instant;
import java.util.Map;

public record EventoDTO(
        String origem,
        String entidade,
        String acao,
        String entidadeId,
        Map<String, Object> dados,
        Instant timestamp
) {
}
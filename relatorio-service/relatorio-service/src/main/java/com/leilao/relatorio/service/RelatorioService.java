package com.leilao.relatorio.service;

import com.leilao.relatorio.model.EventoLog;

import com.leilao.relatorio.repository.EventoLogRepository;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RelatorioService {

    private final EventoLogRepository repository;

    public RelatorioService(
            EventoLogRepository repository
    ) {
        this.repository = repository;
    }

    public Map<String, Object> resumo() {

        Map<String, Object> resumo =
                new HashMap<>();

        resumo.put(
                "leiloesCriados",

                repository
                        .countByEntidadeAndAcao(
                                "leilao",
                                "criado"
                        )
        );

        resumo.put(
                "leiloesEncerrados",

                repository
                        .countByEntidadeAndAcao(
                                "leilao",
                                "encerrado"
                        )
        );

        resumo.put(
                "lancesRecebidos",

                repository
                        .countByEntidadeAndAcao(
                                "lance",
                                "solicitado"
                        )
        );

        resumo.put(
                "transacoesAprovadas",

                repository
                        .countByEntidadeAndAcao(
                                "transacao",
                                "aprovada"
                        )
        );

        resumo.put(
                "transacoesRecusadas",

                repository
                        .countByEntidadeAndAcao(
                                "transacao",
                                "recusada"
                        )
        );

        resumo.put(
                "usuariosCriados",

                repository
                        .countByEntidadeAndAcao(
                                "usuario",
                                "criado"
                        )
        );

        long valorTotalVendido =
                calcularTotalVendido();

        resumo.put(
                "valorTotalVendidoCentavos",
                valorTotalVendido
        );

        return resumo;
    }

    public List<EventoLog> eventos() {

        return repository
                .findAllByOrderByTimestampDesc();
    }

    private long calcularTotalVendido() {

        List<EventoLog> eventos =
                repository
                        .findByEntidadeAndAcao(
                                "leilao",
                                "encerrado"
                        );

        long total = 0;

        for (
                EventoLog evento :
                eventos
        ) {

            Object houveVenda =
                    evento
                            .getDados()
                            .get("houveVenda");

            if (
                    !Boolean.TRUE.equals(
                            houveVenda
                    )
            ) {
                continue;
            }

            Object valor =
                    evento
                            .getDados()
                            .get(
                                    "valorFinalCentavos"
                            );

            if (valor instanceof Number numero) {

                total +=
                        numero.longValue();
            }
        }

        return total;
    }
}
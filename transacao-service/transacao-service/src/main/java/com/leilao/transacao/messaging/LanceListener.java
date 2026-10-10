package com.leilao.transacao.messaging;

import com.leilao.common.EventPublisher;
import com.leilao.common.EventoDTO;
import com.leilao.common.Mensageria;

import com.leilao.transacao.client.VendasClient;

import com.leilao.transacao.dto.ResultadoProcessamentoDTO;

import com.leilao.transacao.model.TransacaoLance;

import com.leilao.transacao.service.TransacaoService;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class LanceListener {

    private final TransacaoService transacaoService;

    private final VendasClient vendasClient;

    private final EventPublisher publisher;

    public LanceListener(
            TransacaoService transacaoService,
            VendasClient vendasClient,
            EventPublisher publisher
    ) {

        this.transacaoService =
                transacaoService;

        this.vendasClient =
                vendasClient;

        this.publisher =
                publisher;
    }

    @RabbitListener(
            queues = Mensageria.FILA_TRANSACAO
    )
    public void receber(
            EventoDTO evento
    ) {

        TransacaoLance transacao =
                transacaoService
                        .criarPendente(
                                evento
                        );

        /*
         * Se já terminou anteriormente,
         * não processamos novamente.
         */
        if (
                "APROVADA".equals(
                        transacao.getStatus()
                )
                ||
                "RECUSADA".equals(
                        transacao.getStatus()
                )
        ) {
            return;
        }

        /*
         * Evento de PENDENTE.
         */
        if (
                "PENDENTE".equals(
                        transacao.getStatus()
                )
        ) {

            publisher.publicar(
                    "transacao",
                    "pendente",
                    transacao.getId(),
                    Map.of(
                            "lanceId",
                            transacao.getLanceId(),

                            "status",
                            "PENDENTE"
                    )
            );
        }

        /*
         * PENDENTE -> PROCESSANDO
         */
        transacao =
                transacaoService
                        .mudarStatus(
                                transacao.getId(),
                                "PROCESSANDO",
                                "Validando as regras do leilão"
                        );

        publisher.publicar(
                "transacao",
                "processando",
                transacao.getId(),
                Map.of(
                        "lanceId",
                        transacao.getLanceId(),

                        "status",
                        "PROCESSANDO"
                )
        );

        try {

            /*
             * Chamada DIRETA:
             *
             * transacao-service
             *       ↓
             * vendas-service
             *
             * NÃO passa no Gateway.
             */
            ResultadoProcessamentoDTO resultado =
                    vendasClient.processar(
                            transacao.getLanceId()
                    );

            if (resultado.aprovado()) {

                transacao =
                        transacaoService
                                .mudarStatus(
                                        transacao.getId(),
                                        "APROVADA",
                                        resultado.motivo()
                                );

                publisher.publicar(
                        "transacao",
                        "aprovada",
                        transacao.getId(),
                        Map.of(
                                "lanceId",
                                transacao.getLanceId(),

                                "valorCentavos",
                                transacao.getValorCentavos(),

                                "status",
                                "APROVADA"
                        )
                );

                publisher.publicar(
                        "lance",
                        "aprovado",
                        transacao.getLanceId(),
                        Map.of(
                                "leilaoId",
                                transacao.getLeilaoId(),

                                "usuarioId",
                                transacao.getUsuarioId(),

                                "valorCentavos",
                                transacao.getValorCentavos()
                        )
                );

            } else {

                transacao =
                        transacaoService
                                .mudarStatus(
                                        transacao.getId(),
                                        "RECUSADA",
                                        resultado.motivo()
                                );

                publisher.publicar(
                        "transacao",
                        "recusada",
                        transacao.getId(),
                        Map.of(
                                "lanceId",
                                transacao.getLanceId(),

                                "valorCentavos",
                                transacao.getValorCentavos(),

                                "status",
                                "RECUSADA"
                        )
                );

                publisher.publicar(
                        "lance",
                        "recusado",
                        transacao.getLanceId(),
                        Map.of(
                                "leilaoId",
                                transacao.getLeilaoId(),

                                "usuarioId",
                                transacao.getUsuarioId(),

                                "valorCentavos",
                                transacao.getValorCentavos()
                        )
                );
            }

        } catch (Exception e) {

            /*
             * Serviço de vendas fora do ar,
             * timeout, erro de rede etc.
             */
            transacaoService
                    .mudarStatus(
                            transacao.getId(),
                            "ERRO",
                            "Não foi possível processar o lance"
                    );

            publisher.publicar(
                    "transacao",
                    "erro",
                    transacao.getId(),
                    Map.of(
                            "lanceId",
                            transacao.getLanceId(),

                            "status",
                            "ERRO"
                    )
            );
        }
    }
}
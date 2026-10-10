package com.leilao.transacao.service;

import com.leilao.common.EventoDTO;
import com.leilao.transacao.model.HistoricoStatus;
import com.leilao.transacao.model.TransacaoLance;
import com.leilao.transacao.repository.HistoricoStatusRepository;
import com.leilao.transacao.repository.TransacaoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;

@Service
public class TransacaoService {

    private final TransacaoRepository transacaoRepository;
    private final HistoricoStatusRepository historicoRepository;

    public TransacaoService(
            TransacaoRepository transacaoRepository,
            HistoricoStatusRepository historicoRepository
    ) {
        this.transacaoRepository = transacaoRepository;
        this.historicoRepository = historicoRepository;
    }

    @Transactional
    public TransacaoLance criarPendente(
            EventoDTO evento
    ) {

        var existente =
                transacaoRepository.findByLanceId(
                        evento.entidadeId()
                );

        if (existente.isPresent()) {
            return existente.get();
        }

        TransacaoLance transacao =
                new TransacaoLance();

        transacao.setLanceId(
                evento.entidadeId()
        );

        transacao.setLeilaoId(
                String.valueOf(
                        evento.dados().get("leilaoId")
                )
        );

        transacao.setUsuarioId(
                String.valueOf(
                        evento.dados().get("usuarioId")
                )
        );

        Object valorBruto =
                evento.dados().get("valorCentavos");

        long valor;

        if (valorBruto instanceof Number numero) {
            valor = numero.longValue();
        } else {
            valor = Long.parseLong(
                    String.valueOf(valorBruto)
            );
        }

        transacao.setValorCentavos(valor);

        transacao.setStatus(
                "PENDENTE"
        );

        transacao.setMotivo(
                "Lance recebido pela fila"
        );

        transacao.setCriadoEm(
                Instant.now()
        );

        transacao.setAtualizadoEm(
                Instant.now()
        );

        transacao =
                transacaoRepository.save(
                        transacao
                );

        salvarHistorico(
                transacao,
                "PENDENTE",
                transacao.getMotivo()
        );

        return transacao;
    }

    @Transactional
    public TransacaoLance mudarStatus(
            String transacaoId,
            String status,
            String motivo
    ) {

        TransacaoLance transacao =
                transacaoRepository
                        .findById(transacaoId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Transação não encontrada"
                                )
                        );

        transacao.setStatus(status);
        transacao.setMotivo(motivo);
        transacao.setAtualizadoEm(
                Instant.now()
        );

        transacao =
                transacaoRepository.save(
                        transacao
                );

        salvarHistorico(
                transacao,
                status,
                motivo
        );

        return transacao;
    }

    public TransacaoLance buscarPorLance(
            String lanceId
    ) {

        return transacaoRepository
                .findByLanceId(lanceId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "A transação desse lance ainda não foi criada"
                        )
                );
    }

    public List<HistoricoStatus> buscarHistorico(
            String lanceId
    ) {

        TransacaoLance transacao =
                buscarPorLance(lanceId);

        return historicoRepository
                .findByTransacaoIdOrderByCriadoEmAsc(
                        transacao.getId()
                );
    }

    private void salvarHistorico(
            TransacaoLance transacao,
            String status,
            String motivo
    ) {

        HistoricoStatus historico =
                new HistoricoStatus();

        historico.setTransacaoId(
                transacao.getId()
        );

        historico.setStatus(
                status
        );

        historico.setMotivo(
                motivo
        );

        historico.setCriadoEm(
                Instant.now()
        );

        historicoRepository.save(
                historico
        );
    }
}
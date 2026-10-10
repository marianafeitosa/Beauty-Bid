package com.leilao.vendas.service;

import com.leilao.common.EventPublisher;
import com.leilao.vendas.dto.ResultadoProcessamento;
import com.leilao.vendas.model.Lance;
import com.leilao.vendas.model.Leilao;
import com.leilao.vendas.repository.LanceRepository;
import com.leilao.vendas.repository.LeilaoRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
public class LanceService {

    private final LanceRepository lanceRepository;
    private final LeilaoRepository leilaoRepository;
    private final EventPublisher publisher;
    private final MongoTemplate mongo;

    public LanceService(
            LanceRepository lanceRepository,
            LeilaoRepository leilaoRepository,
            EventPublisher publisher,
            MongoTemplate mongo
    ) {
        this.lanceRepository = lanceRepository;
        this.leilaoRepository = leilaoRepository;
        this.publisher = publisher;
        this.mongo = mongo;
    }

    /*
     * Chamado pelo FRONT através do Gateway.
     *
     * Aqui o lance NÃO é aprovado ainda.
     * Ele apenas entra na fila.
     */
    public Lance solicitarLance(
            String leilaoId,
            String usuarioId,
            long valor
    ) {

        leilaoRepository
                .findById(leilaoId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Leilão não encontrado"
                        )
                );

        if (valor <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "O valor do lance deve ser maior que zero"
            );
        }

        Lance lance = new Lance();

        lance.setLeilaoId(leilaoId);
        lance.setUsuarioId(usuarioId);
        lance.setValorCentavos(valor);
        lance.setStatus("NA_FILA");
        lance.setMotivoStatus(
                "Lance aguardando processamento"
        );
        lance.setCriadoEm(Instant.now());

        lance = lanceRepository.save(lance);

        publisher.publicar(
                "lance",
                "solicitado",
                lance.getId(),
                Map.of(
                        "leilaoId",
                        leilaoId,

                        "usuarioId",
                        usuarioId,

                        "valorCentavos",
                        valor
                )
        );

        return lance;
    }

    /*
     * Chamado SOMENTE pelo transacao-service.
     *
     * Aqui acontece a regra real do leilão.
     *
     * Caso o MongoDB detecte conflito entre duas
     * transações concorrentes, o Spring tentará
     * executar novamente a operação.
     */
    @Retryable(
            retryFor = DataIntegrityViolationException.class,
            maxAttempts = 5,
            backoff = @Backoff(
                    delay = 100,
                    multiplier = 2
            )
    )
    @Transactional
    public ResultadoProcessamento processarLance(
            String lanceId
    ) {

        Lance lance = lanceRepository
                .findById(lanceId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Lance não encontrado"
                        )
                );

        /*
         * Idempotência:
         * se essa mensagem for recebida duas vezes,
         * não processamos o mesmo lance novamente.
         */
        if ("ACEITO".equals(lance.getStatus())) {

            return new ResultadoProcessamento(
                    true,
                    lance.getMotivoStatus()
            );
        }

        if ("RECUSADO".equals(lance.getStatus())) {

            return new ResultadoProcessamento(
                    false,
                    lance.getMotivoStatus()
            );
        }

        /*
         * Atualiza o leilão SOMENTE se:
         *
         * - está ABERTO
         * - ainda não terminou
         * - novo lance é maior que o atual
         *
         * A verificação + alteração acontecem
         * atomicamente no MongoDB.
         */
        Query query = Query.query(
                Criteria.where("id")
                        .is(lance.getLeilaoId())

                        .and("status")
                        .is("ABERTO")

                        .and("fimEm")
                        .gt(Instant.now())

                        .and("valorAtualCentavos")
                        .lt(lance.getValorCentavos())
        );

        Update update = new Update()
                .set(
                        "valorAtualCentavos",
                        lance.getValorCentavos()
                )
                .set(
                        "vencedorId",
                        lance.getUsuarioId()
                );

        Leilao atualizado = mongo.findAndModify(
                query,
                update,
                FindAndModifyOptions
                        .options()
                        .returnNew(true),
                Leilao.class
        );

        /*
         * Se nenhum documento foi atualizado,
         * alguma regra não foi satisfeita.
         */
        if (atualizado == null) {

            lance.setStatus("RECUSADO");

            lance.setMotivoStatus(
                    "Lance inferior ao atual, leilão encerrado ou prazo expirado"
            );

            lanceRepository.save(lance);

            return new ResultadoProcessamento(
                    false,
                    lance.getMotivoStatus()
            );
        }

        lance.setStatus("ACEITO");

        lance.setMotivoStatus(
                "Lance aceito"
        );

        lanceRepository.save(lance);

        return new ResultadoProcessamento(
                true,
                "Lance aceito"
        );
    }

    public List<Lance> listarPorLeilao(
            String leilaoId
    ) {

        return lanceRepository
                .findByLeilaoIdOrderByCriadoEmDesc(
                        leilaoId
                );
    }

    public List<Lance> listarPorUsuario(
            String usuarioId
    ) {

        return lanceRepository
                .findByUsuarioIdOrderByCriadoEmDesc(
                        usuarioId
                );
    }
}
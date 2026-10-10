package com.leilao.transacao.repository;

import com.leilao.transacao.model.HistoricoStatus;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface HistoricoStatusRepository
        extends MongoRepository<
                HistoricoStatus,
                String
        > {

    List<HistoricoStatus>
    findByTransacaoIdOrderByCriadoEmAsc(
            String transacaoId
    );
}
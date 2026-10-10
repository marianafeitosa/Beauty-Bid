package com.leilao.relatorio.repository;

import com.leilao.relatorio.model.EventoLog;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface EventoLogRepository
        extends MongoRepository<
                EventoLog,
                String
        > {

    long countByEntidadeAndAcao(
            String entidade,
            String acao
    );

    List<EventoLog>
    findByEntidadeAndAcao(
            String entidade,
            String acao
    );

    List<EventoLog>
    findAllByOrderByTimestampDesc();
}
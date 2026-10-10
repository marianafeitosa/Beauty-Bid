package com.leilao.transacao.repository;

import com.leilao.transacao.model.TransacaoLance;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TransacaoRepository
        extends MongoRepository<
                TransacaoLance,
                String
        > {

    Optional<TransacaoLance>
    findByLanceId(
            String lanceId
    );
}
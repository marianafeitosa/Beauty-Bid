package com.leilao.vendas.repository;

import com.leilao.vendas.model.Leilao;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;

public interface LeilaoRepository
        extends MongoRepository<Leilao, String> {

    List<Leilao> findByStatusAndFimEmBefore(
            String status,
            Instant fim
    );
}
package com.leilao.vendas.repository;

import com.leilao.vendas.model.Lance;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LanceRepository
        extends MongoRepository<Lance, String> {

    List<Lance> findByLeilaoIdOrderByCriadoEmDesc(
            String leilaoId
    );

    List<Lance> findByUsuarioIdOrderByCriadoEmDesc(
            String usuarioId
    );
}
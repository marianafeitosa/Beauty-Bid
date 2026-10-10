package com.leilao.vendas.service;

import com.leilao.common.EventPublisher;

import com.leilao.vendas.dto.LeilaoRequest;

import com.leilao.vendas.model.Leilao;

import com.leilao.vendas.repository.LeilaoRepository;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import org.springframework.http.HttpStatus;

import org.springframework.stereotype.Service;

import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LeilaoService {

    private final LeilaoRepository repository;

    private final EventPublisher publisher;

    private final MongoTemplate mongo;

    public LeilaoService(
            LeilaoRepository repository,
            EventPublisher publisher,
            MongoTemplate mongo
    ) {

        this.repository =
                repository;

        this.publisher =
                publisher;

        this.mongo =
                mongo;
    }

    public Leilao criar(
            LeilaoRequest request
    ) {

        if (
                request.fimEm() == null
                ||
                !request
                        .fimEm()
                        .isAfter(
                                Instant.now()
                        )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A data final deve estar no futuro"
            );
        }

        if (
                request
                        .lanceInicialCentavos()
                < 0
        ) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Lance inicial inválido"
            );
        }

        Leilao leilao =
                new Leilao();

        leilao.setTitulo(
                request.titulo()
        );

        leilao.setMarca(
                request.marca()
        );

        leilao.setDescricao(
                request.descricao()
        );

        leilao.setImagemUrl(
                request.imagemUrl()
        );

        leilao.setLanceInicialCentavos(
                request
                        .lanceInicialCentavos()
        );

        leilao.setValorAtualCentavos(
                request
                        .lanceInicialCentavos()
        );

        leilao.setStatus(
                "ABERTO"
        );

        leilao.setInicioEm(
                Instant.now()
        );

        leilao.setFimEm(
                request.fimEm()
        );

        leilao =
                repository.save(
                        leilao
                );

        publisher.publicar(
                "leilao",
                "criado",
                leilao.getId(),
                Map.of(
                        "titulo",
                        leilao.getTitulo(),

                        "lanceInicialCentavos",
                        leilao
                                .getLanceInicialCentavos()
                )
        );

        return leilao;
    }

    public List<Leilao> listar() {
        return repository.findAll();
    }

    public Leilao buscar(
            String id
    ) {

        return repository
                .findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Leilão não encontrado"
                        )
                );
    }

    public Leilao atualizar(
            String id,
            LeilaoRequest request
    ) {

        Leilao leilao =
                buscar(id);

        if (
                "ENCERRADO".equals(
                        leilao.getStatus()
                )
        ) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Leilão encerrado não pode ser alterado"
            );
        }

        leilao.setTitulo(
                request.titulo()
        );

        leilao.setMarca(
                request.marca()
        );

        leilao.setDescricao(
                request.descricao()
        );

        leilao.setImagemUrl(
                request.imagemUrl()
        );

        leilao.setFimEm(
                request.fimEm()
        );

        /*
         * Só pode alterar lance inicial
         * se ainda não existe vencedor.
         */
        if (
                leilao
                        .getVencedorId()
                == null
        ) {

            leilao.setLanceInicialCentavos(
                    request
                            .lanceInicialCentavos()
            );

            leilao.setValorAtualCentavos(
                    request
                            .lanceInicialCentavos()
            );
        }

        leilao =
                repository.save(
                        leilao
                );

        publisher.publicar(
                "leilao",
                "atualizado",
                leilao.getId(),
                Map.of(
                        "titulo",
                        leilao.getTitulo()
                )
        );

        return leilao;
    }

    public void excluir(
            String id
    ) {

        Leilao leilao =
                buscar(id);

        repository.delete(
                leilao
        );

        publisher.publicar(
                "leilao",
                "excluido",
                id,
                Map.of(
                        "titulo",
                        leilao.getTitulo()
                )
        );
    }

    public void encerrar(
            String id
    ) {

        Leilao leilao =
                mongo.findAndModify(

                        Query.query(
                                Criteria
                                        .where("id")
                                        .is(id)

                                        .and("status")
                                        .is("ABERTO")
                        ),

                        new Update()
                                .set(
                                        "status",
                                        "ENCERRADO"
                                ),

                        FindAndModifyOptions
                                .options()
                                .returnNew(true),

                        Leilao.class
                );

        if (leilao == null) {
            return;
        }

        Map<String, Object> dados =
                new HashMap<>();

        dados.put(
                "valorFinalCentavos",
                leilao
                        .getValorAtualCentavos()
        );

        boolean houveVenda =
                leilao
                        .getVencedorId()
                != null;

        dados.put(
                "houveVenda",
                houveVenda
        );

        if (houveVenda) {

            dados.put(
                    "vencedorId",
                    leilao
                            .getVencedorId()
            );
        }

        publisher.publicar(
                "leilao",
                "encerrado",
                id,
                dados
        );
    }
}
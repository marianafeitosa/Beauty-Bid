package com.leilao.relatorio.model;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

import java.util.Map;

@Document("eventos")
public class EventoLog {

    @Id
    private String id;

    private String origem;

    private String entidade;

    private String acao;

    private String entidadeId;

    private Map<String, Object> dados;

    private Instant timestamp;

    public String getId() {
        return id;
    }

    public void setId(
            String id
    ) {
        this.id = id;
    }

    public String getOrigem() {
        return origem;
    }

    public void setOrigem(
            String origem
    ) {
        this.origem = origem;
    }

    public String getEntidade() {
        return entidade;
    }

    public void setEntidade(
            String entidade
    ) {
        this.entidade = entidade;
    }

    public String getAcao() {
        return acao;
    }

    public void setAcao(
            String acao
    ) {
        this.acao = acao;
    }

    public String getEntidadeId() {
        return entidadeId;
    }

    public void setEntidadeId(
            String entidadeId
    ) {
        this.entidadeId =
                entidadeId;
    }

    public Map<String, Object> getDados() {
        return dados;
    }

    public void setDados(
            Map<String, Object> dados
    ) {
        this.dados = dados;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(
            Instant timestamp
    ) {
        this.timestamp =
                timestamp;
    }
}
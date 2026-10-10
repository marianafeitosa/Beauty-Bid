package com.leilao.transacao.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("historico_status")
public class HistoricoStatus {

    @Id
    private String id;

    private String transacaoId;

    private String status;

    private String motivo;

    private Instant criadoEm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTransacaoId() {
        return transacaoId;
    }

    public void setTransacaoId(
            String transacaoId
    ) {
        this.transacaoId =
                transacaoId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status = status;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(
            String motivo
    ) {
        this.motivo = motivo;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(
            Instant criadoEm
    ) {
        this.criadoEm = criadoEm;
    }
}
package com.leilao.transacao.model;

import org.springframework.data.annotation.Id;

import org.springframework.data.mongodb.core.index.Indexed;

import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("transacoes")
public class TransacaoLance {

    @Id
    private String id;

    @Indexed(unique = true)
    private String lanceId;

    private String leilaoId;

    private String usuarioId;

    private long valorCentavos;

    private String status;

    private String motivo;

    private Instant criadoEm;

    private Instant atualizadoEm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLanceId() {
        return lanceId;
    }

    public void setLanceId(String lanceId) {
        this.lanceId = lanceId;
    }

    public String getLeilaoId() {
        return leilaoId;
    }

    public void setLeilaoId(String leilaoId) {
        this.leilaoId = leilaoId;
    }

    public String getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(String usuarioId) {
        this.usuarioId = usuarioId;
    }

    public long getValorCentavos() {
        return valorCentavos;
    }

    public void setValorCentavos(
            long valorCentavos
    ) {
        this.valorCentavos =
                valorCentavos;
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

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }

    public void setAtualizadoEm(
            Instant atualizadoEm
    ) {
        this.atualizadoEm =
                atualizadoEm;
    }
}
package com.leilao.vendas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("lances")
public class Lance {

    @Id
    private String id;

    private String leilaoId;

    private String usuarioId;

    private long valorCentavos;

    private String status;

    private String motivoStatus;

    private Instant criadoEm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public void setValorCentavos(long valorCentavos) {
        this.valorCentavos = valorCentavos;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMotivoStatus() {
        return motivoStatus;
    }

    public void setMotivoStatus(String motivoStatus) {
        this.motivoStatus = motivoStatus;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public void setCriadoEm(Instant criadoEm) {
        this.criadoEm = criadoEm;
    }
}
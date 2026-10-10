package com.leilao.vendas.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("leiloes")
public class Leilao {

    @Id
    private String id;

    private String titulo;

    private String marca;

    private String descricao;

    private String imagemUrl;

    private long lanceInicialCentavos;

    private long valorAtualCentavos;

    private String vencedorId;

    private String status;

    private Instant inicioEm;

    private Instant fimEm;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getImagemUrl() {
        return imagemUrl;
    }

    public void setImagemUrl(String imagemUrl) {
        this.imagemUrl = imagemUrl;
    }

    public long getLanceInicialCentavos() {
        return lanceInicialCentavos;
    }

    public void setLanceInicialCentavos(
            long lanceInicialCentavos
    ) {
        this.lanceInicialCentavos =
                lanceInicialCentavos;
    }

    public long getValorAtualCentavos() {
        return valorAtualCentavos;
    }

    public void setValorAtualCentavos(
            long valorAtualCentavos
    ) {
        this.valorAtualCentavos =
                valorAtualCentavos;
    }

    public String getVencedorId() {
        return vencedorId;
    }

    public void setVencedorId(
            String vencedorId
    ) {
        this.vencedorId =
                vencedorId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(
            String status
    ) {
        this.status = status;
    }

    public Instant getInicioEm() {
        return inicioEm;
    }

    public void setInicioEm(
            Instant inicioEm
    ) {
        this.inicioEm = inicioEm;
    }

    public Instant getFimEm() {
        return fimEm;
    }

    public void setFimEm(
            Instant fimEm
    ) {
        this.fimEm = fimEm;
    }
}
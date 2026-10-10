package com.leilao.common;

public final class Mensageria {

    public static final String EXCHANGE =
            "leilao.events";

    public static final String FILA_RELATORIO =
            "relatorio.queue";

    public static final String FILA_TRANSACAO =
            "transacao.lances.queue";

    private Mensageria() {
    }
}
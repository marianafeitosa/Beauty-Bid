package com.leilao.relatorio.messaging;

import com.leilao.common.EventoDTO;
import com.leilao.common.Mensageria;

import com.leilao.relatorio.model.EventoLog;

import com.leilao.relatorio.repository.EventoLogRepository;

import org.springframework.amqp.rabbit.annotation.RabbitListener;

import org.springframework.stereotype.Component;

@Component
public class EventoListener {

    private final EventoLogRepository repository;

    public EventoListener(
            EventoLogRepository repository
    ) {

        this.repository =
                repository;
    }

    @RabbitListener(
            queues = Mensageria.FILA_RELATORIO
    )
    public void receber(
            EventoDTO evento
    ) {

        EventoLog log =
                new EventoLog();

        log.setOrigem(
                evento.origem()
        );

        log.setEntidade(
                evento.entidade()
        );

        log.setAcao(
                evento.acao()
        );

        log.setEntidadeId(
                evento.entidadeId()
        );

        log.setDados(
                evento.dados()
        );

        log.setTimestamp(
                evento.timestamp()
        );

        repository.save(log);
    }
}
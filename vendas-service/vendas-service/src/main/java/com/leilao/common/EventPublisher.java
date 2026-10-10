package com.leilao.common;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
public class EventPublisher {

    private final RabbitTemplate rabbit;
    private final String origem;

    public EventPublisher(
            RabbitTemplate rabbit,
            @Value("${spring.application.name}") String origem
    ) {
        this.rabbit = rabbit;
        this.origem = origem;
    }

    public void publicar(
            String entidade,
            String acao,
            String id,
            Map<String, Object> dados
    ) {

        EventoDTO evento = new EventoDTO(
                origem,
                entidade,
                acao,
                id,
                dados,
                Instant.now()
        );

        String routingKey = entidade + "." + acao;

        rabbit.convertAndSend(
                Mensageria.EXCHANGE,
                routingKey,
                evento
        );
    }
}
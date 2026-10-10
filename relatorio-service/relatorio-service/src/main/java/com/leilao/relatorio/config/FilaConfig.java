package com.leilao.relatorio.config;

import com.leilao.common.Mensageria;

import org.springframework.amqp.core.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilaConfig {

    @Bean
    public Queue filaRelatorio() {

        return new Queue(
                Mensageria.FILA_RELATORIO,
                true
        );
    }

    @Bean
    public Binding bindingTudo(
            Queue filaRelatorio,
            TopicExchange leilaoExchange
    ) {

        return BindingBuilder
                .bind(filaRelatorio)
                .to(leilaoExchange)
                .with("#");
    }
}
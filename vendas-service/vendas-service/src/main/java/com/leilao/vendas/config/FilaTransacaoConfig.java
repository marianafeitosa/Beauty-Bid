package com.leilao.transacao.config;

import com.leilao.common.Mensageria;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilaTransacaoConfig {

    @Bean
    public Queue filaTransacao() {

        return new Queue(
                Mensageria.FILA_TRANSACAO,
                true
        );
    }

    @Bean
    public Binding bindingTransacao(
            Queue filaTransacao,
            TopicExchange leilaoExchange
    ) {

        return BindingBuilder
                .bind(filaTransacao)
                .to(leilaoExchange)
                .with("lance.solicitado");
    }
}
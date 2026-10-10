package com.leilao.common;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public TopicExchange leilaoExchange() {
        return new TopicExchange(Mensageria.EXCHANGE);
    }

    @Bean
    public Jackson2JsonMessageConverter jsonConverter() {

        Jackson2JsonMessageConverter converter =
                new Jackson2JsonMessageConverter();

        DefaultJackson2JavaTypeMapper mapper =
                new DefaultJackson2JavaTypeMapper();

        mapper.setTrustedPackages("*");

        converter.setJavaTypeMapper(mapper);

        return converter;
    }
}
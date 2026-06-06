package com.sanosysalvos.ms_reportes.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String QUEUE_NAME = "reportes.queue";
    public static final String EXCHANGE_NAME = "sanosysalvos.exchange";
    public static final String ROUTING_KEY_REPORTE_CREADO = "reporte.creado";

    @Bean
    public Queue reportesQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    @Bean
    public TopicExchange sanosysalvosExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding reportesBinding(Queue reportesQueue, TopicExchange sanosysalvosExchange) {
        return BindingBuilder.bind(reportesQueue).to(sanosysalvosExchange).with(ROUTING_KEY_REPORTE_CREADO);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter(ObjectMapper objectMapper) {
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(messageConverter);
        return rabbitTemplate;
    }
}

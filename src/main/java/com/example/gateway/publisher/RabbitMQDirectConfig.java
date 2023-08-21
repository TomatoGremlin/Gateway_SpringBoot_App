package com.example.gateway.publisher;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQDirectConfig {
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory factory = new CachingConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory;
    }
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        return new RabbitTemplate(connectionFactory);
    }

    @Bean
    Queue clientRequestsQueue() {
        return new Queue("clientRequestQueue", false);
    }

    @Bean
    Queue ratesQueue() {
        return new Queue("ratesQueue", false);
    }


    @Bean
    DirectExchange exchange() {
        return new DirectExchange("direct-exchange");
    }

    @Bean
    Binding clientRequests(Queue clientRequestsQueue, DirectExchange exchange) {
        return BindingBuilder.bind(clientRequestsQueue).to(exchange).with("clientRequest");
    }

    @Bean
    Binding rates(Queue ratesQueue, DirectExchange exchange) {
        return BindingBuilder.bind(ratesQueue).to(exchange).with("rates");
    }

}
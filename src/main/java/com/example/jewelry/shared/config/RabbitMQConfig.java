package com.example.jewelry.shared.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EMAIL_QUEUE = "order_email_queue";
    public static final String EXCHANGE = "silvere_exchange";
    public static final String EMAIL_ROUTING_KEY = "email_routing_key";

    public static final String PENDING_QUEUE = "order_pending_queue";
    public static final String CANCEL_DLQ = "order_cancel_dlq";
    public static final String PENDING_ROUTING_KEY = "pending_routing_key";
    public static final String DLX_ROUTING_KEY = "cancel_routing_key";

    // 1. Tạo Queue
    @Bean
    public Queue emailQueue() {
        return new Queue(EMAIL_QUEUE, true); // true = Queue sống sót qua lần restart server
    }

    // 2. Tạo Exchange (Trung tâm phân loại tin nhắn)
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }
    @Bean
    public Queue pendingQueue() {
        return QueueBuilder.durable(PENDING_QUEUE)
                .withArgument("x-dead-letter-exchange", EXCHANGE)
                .withArgument("x-dead-letter-routing-key", DLX_ROUTING_KEY)
                .withArgument("x-message-ttl", 900000 )   //milisecond
                .build();
    }

    @Bean
    public Queue cancelDlq() {
        return QueueBuilder.durable(CANCEL_DLQ).build();
    }

    @Bean
    public Binding pendingBinding(Queue pendingQueue, DirectExchange exchange) {
        return BindingBuilder.bind(pendingQueue).to(exchange).with(PENDING_ROUTING_KEY);
    }

    @Bean
    public Binding dlxBinding(Queue cancelDlq, DirectExchange exchange) {
        return BindingBuilder.bind(cancelDlq).to(exchange).with(DLX_ROUTING_KEY);
    }

    // 3. Nối Queue vào Exchange
    @Bean
    public Binding emailBinding(Queue emailQueue, DirectExchange exchange) {
        return BindingBuilder.bind(emailQueue).to(exchange).with(EMAIL_ROUTING_KEY);
    }

    // 4. Bắt buộc chuyển Message thành JSON
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
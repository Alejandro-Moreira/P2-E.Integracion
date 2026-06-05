package edu.udla.integracion.progreso2.config;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public DirectExchange billingExchange() {
        return new DirectExchange("billing.exchange", true, false);
    }

    @Bean
    public Queue billingQueue() {
        return new Queue("billing.queue", true);
    }

    @Bean
    public Binding billingBinding(Queue billingQueue, DirectExchange billingExchange) {
        return BindingBuilder.bind(billingQueue).to(billingExchange).with("billing.queue");
    }

    @Bean
    public AmqpAdmin amqpAdmin(
            ConnectionFactory connectionFactory,
            Queue billingQueue,
            DirectExchange billingExchange,
            Binding billingBinding,
            Queue notificationsQueue,
            Queue analyticsQueue,
            FanoutExchange appointmentsExchange,
            Binding notificationsBinding,
            Binding analyticsBinding) {
        RabbitAdmin admin = new RabbitAdmin(connectionFactory);
        admin.setAutoStartup(true);
        admin.declareQueue(billingQueue);
        admin.declareExchange(billingExchange);
        admin.declareBinding(billingBinding);
        admin.declareQueue(notificationsQueue);
        admin.declareQueue(analyticsQueue);
        admin.declareExchange(appointmentsExchange);
        admin.declareBinding(notificationsBinding);
        admin.declareBinding(analyticsBinding);
        return admin;
    }

    @Bean
    public FanoutExchange appointmentsExchange() {
        return new FanoutExchange("appointments.events", true, false);
    }

    @Bean
    public Queue notificationsQueue() {
        return new Queue("notifications.queue", true);
    }

    @Bean
    public Queue analyticsQueue() {
        return new Queue("analytics.queue", true);
    }

    @Bean
    public Binding notificationsBinding(Queue notificationsQueue, FanoutExchange appointmentsExchange) {
        return BindingBuilder.bind(notificationsQueue).to(appointmentsExchange);
    }

    @Bean
    public Binding analyticsBinding(Queue analyticsQueue, FanoutExchange appointmentsExchange) {
        return BindingBuilder.bind(analyticsQueue).to(appointmentsExchange);
    }
}

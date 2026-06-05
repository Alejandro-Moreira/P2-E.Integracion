package edu.udla.integracion.progreso2.config;

import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.support.SimpleRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

import edu.udla.integracion.progreso2.routes.CitaIntegrationRoute;
import edu.udla.integracion.progreso2.service.CitaValidationService;

@Configuration
public class CamelConfig {

    @Bean
    @DependsOn("amqpAdmin")
    public CamelContext camelContext(CitaIntegrationRoute route) throws Exception {
        SimpleRegistry registry = new SimpleRegistry();
        registry.bind("citaValidationService", new CitaValidationService());

        DefaultCamelContext context = new DefaultCamelContext(registry);
        context.addRoutes(route);
        context.start();
        return context;
    }

    @Bean
    public ProducerTemplate producerTemplate(CamelContext camelContext) {
        return camelContext.createProducerTemplate();
    }
}

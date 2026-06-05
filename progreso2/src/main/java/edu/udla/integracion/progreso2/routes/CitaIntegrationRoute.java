package edu.udla.integracion.progreso2.routes;

import java.time.LocalDateTime;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CitaIntegrationRoute extends RouteBuilder {

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(false)
                .process(exchange -> {
                    Throwable exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Throwable.class);
                    Object body = exchange.getIn().getBody();
                    String linea = String.format("%s | reason=%s | data=%s%n",
                            LocalDateTime.now(),
                            exception != null ? exception.getMessage() : "error desconocido",
                            body);
                    exchange.getMessage().setBody(linea);
                })
                .to("file:data/errors?fileName=citas-rechazadas.log&fileExist=Append");

        from("direct:procesarCita")
                .bean("citaValidationService", "validar")
                .multicast()
                    .to("direct:facturacion")
                    .to("direct:eventos")
                    .to("direct:csv")
                .end();

        from("direct:facturacion")
                .marshal().json()
                .to("rabbitmq:billing.exchange?exchangeType=direct&declare=false&exchangePattern=InOnly&hostname=localhost&portNumber=5672&username=guest&password=guest");

        from("direct:eventos")
                .marshal().json()
                .to("rabbitmq:appointments.events?exchangeType=fanout&declare=false&exchangePattern=InOnly&hostname=localhost&portNumber=5672&username=guest&password=guest");

        from("direct:csv")
                .setBody(simple(
                        "${body.idCita},${body.paciente},${body.correo}," +
                                "${body.especialidad},${body.fechaCita}," +
                                "${body.sede},${body.valor}\n"))
                .to("file:data/outbox?fileName=auditoria-citas.csv&fileExist=Append");
    }
}
package edu.udla.integracion.progreso2.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class CitaIntegrationRoute extends RouteBuilder {

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log("ERROR: ${exception.message}")
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
                .to("rabbitmq:billing.exchange?routingKey=billing.queue");

        from("direct:eventos")
                .marshal().json()
                .to("rabbitmq:billing.exchange"
                        + "?hostname=localhost"
                        + "&portNumber=5672"
                        + "&routingKey=billing.queue");

        from("direct:csv")
                .setBody(simple(
                        "${body.idCita},${body.paciente},${body.correo}," +
                                "${body.especialidad},${body.fechaCita}," +
                                "${body.sede},${body.valor}"))
                .to("file:data/outbox?fileName=auditoria-citas.csv&fileExist=Append");
    }
}
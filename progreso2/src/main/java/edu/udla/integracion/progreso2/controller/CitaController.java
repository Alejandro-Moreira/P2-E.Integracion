package edu.udla.integracion.progreso2.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import edu.udla.integracion.progreso2.model.CitaRequest;
import edu.udla.integracion.progreso2.service.CitaValidationService;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    private ProducerTemplate producerTemplate;

    @Autowired
    private CitaValidationService citaValidationService;

    @PostMapping
    public ResponseEntity<String> registrar(@RequestBody CitaRequest cita) {
        try {
            citaValidationService.validar(cita);
            producerTemplate.requestBody("direct:procesarCita", cita, String.class);
            return ResponseEntity.ok("Cita recibida");
        } catch (Exception ex) {
            String motivo = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
            registrarRechazo(cita, motivo);
            return ResponseEntity.badRequest().body("Solicitud rechazada: " + motivo);
        }
    }

    private void registrarRechazo(CitaRequest cita, String motivo) {
        try {
            Path carpeta = Path.of("data/errors");
            Files.createDirectories(carpeta);
            String linea = String.format("%s | motivo=%s | datos=%s%n",
                    LocalDateTime.now(), motivo, cita);
            Files.writeString(carpeta.resolve("citas-rechazadas.log"), linea,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            // No detener la ejecución; si no se puede escribir el log, la API sigue respondiendo.
        }
    }
}
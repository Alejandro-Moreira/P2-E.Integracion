package edu.udla.integracion.progreso2.controller;

import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import edu.udla.integracion.progreso2.model.CitaRequest;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    @Autowired
    ProducerTemplate producerTemplate;

    @PostMapping
    public String registrar(@RequestBody CitaRequest cita) {

        producerTemplate.sendBody("direct:procesarCita", cita);

        return "Cita recibida";
    }
}
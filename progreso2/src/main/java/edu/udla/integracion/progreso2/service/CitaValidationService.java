package edu.udla.integracion.progreso2.service;

import org.springframework.stereotype.Service;
import edu.udla.integracion.progreso2.model.CitaRequest;

@Service
public class CitaValidationService {

    public void validar(CitaRequest cita) {

        if(cita.getIdCita()==null || cita.getIdCita().isBlank())
            throw new RuntimeException("idCita obligatorio");

        if(cita.getPaciente()==null || cita.getPaciente().isBlank())
            throw new RuntimeException("paciente obligatorio");

        if(cita.getCorreo()==null || cita.getCorreo().isBlank())
            throw new RuntimeException("correo obligatorio");

        if(cita.getEspecialidad()==null || cita.getEspecialidad().isBlank())
            throw new RuntimeException("especialidad obligatoria");

        if(cita.getFechaCita()==null || cita.getFechaCita().isBlank())
            throw new RuntimeException("fecha obligatoria");

        if(cita.getSede()==null || cita.getSede().isBlank())
            throw new RuntimeException("sede obligatoria");

        if(cita.getValor()==null || cita.getValor() <= 0)
            throw new RuntimeException("valor debe ser mayor a cero");
    }
}
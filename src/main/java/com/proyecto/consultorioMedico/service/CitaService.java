package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.repository.CitaRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Alejandro
 */
@Service
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;

    @Transactional(readOnly = true)
    public List<Cita> getCitas() { 
        var lista = citaRepository.findAll();
        return lista;
    }

    @Transactional
    public void save(Cita cita) {
        citaRepository.save(cita);
    }

    @Transactional
    public boolean delete(Cita cita) {
        try {
            citaRepository.delete(cita);
            citaRepository.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(readOnly = true)
    public Cita getCita(Cita cita) {
        return citaRepository.findById(cita.getIdCita()).orElse(null);
    }

    @Transactional(readOnly = true)
    public Cita getCitaPorId(Integer idCita) {
        return citaRepository.findById(idCita).orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean validarConflictoHorario(Integer idMedico, LocalDate fecha, LocalTime hora) {
        List<Cita> citasEnHorario = citaRepository.findByMedicoFechaHora(idMedico, fecha, hora);
        return !citasEnHorario.isEmpty();
    }

    @Transactional(readOnly = true)
    public boolean validarConflictoHorarioEditar(Integer idMedico, LocalDate fecha, LocalTime hora, Integer idCitaActual) {
        List<Cita> citasEnHorario = citaRepository.findByMedicoFechaHoraExcluyendo(idMedico, fecha, hora, idCitaActual);
        return !citasEnHorario.isEmpty();
    }

    @Transactional(readOnly = true)
    public List<LocalTime> getHorasOcupadas(Integer idMedico, LocalDate fecha) {
        return citaRepository.findHorasOcupadas(idMedico, fecha);
    }

    @Transactional(readOnly = true)
    public List<Cita> getCitasPorPaciente(Integer idPaciente) {
        return citaRepository.findByPacienteId(idPaciente);
    }
}
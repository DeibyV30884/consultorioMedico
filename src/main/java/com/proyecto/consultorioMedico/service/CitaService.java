package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.repository.CitaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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

    private static final int DURACION_CITA_MINUTOS = 30;

    @Transactional(readOnly = true)
    public List<Cita> getCitas() {
        var lista = citaRepository.findAll();
        return lista;
    }

    @Transactional
    public void save(Cita cita) {

        if(cita.getEstado()==EstadoCita.Pendiente){
            if (cita.getFechaHora().isBefore(LocalDateTime.now())) {
                throw new IllegalArgumentException("No se pueden agendar citas en el pasado");
            }

            if (!validarDisponibilidad(cita)) {
                throw new IllegalArgumentException("El médico ya tiene una cita asignada en ese horario");
            }
        }    
        citaRepository.save(cita);
    }

    @Transactional
    public void guardarConsultaMedica(Cita cita) {
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
    public Optional<Cita> getCita(Integer idCita) {
        return citaRepository.findById(idCita);
    }

    @Transactional(readOnly = true)
    public Cita getCita(Cita cita) {
        return citaRepository.findById(cita.getIdCita()).orElse(null);
    }

    public List<Cita> buscarCitasHoy() {
        return citaRepository.buscarCitasHoy();
    }

    @Transactional(readOnly = true)
    public boolean validarDisponibilidad(Cita cita) {
        LocalDateTime inicio = cita.getFechaHora();
        LocalDateTime fin = inicio.plusMinutes(DURACION_CITA_MINUTOS);

        List<Cita> citasConflicto = citaRepository.findCitasEnRango(
                cita.getMedico().getIdMedico(),
                inicio.minusMinutes(DURACION_CITA_MINUTOS - 1),
                fin,
                cita.getIdCita()
        );

        return citasConflicto.isEmpty();
    }

    @Transactional(readOnly = true)
    public Cita getUltimaCitaCompletada(Integer idPaciente) {
        List<Cita> citas = citaRepository.findUltimaCitaCompletada(idPaciente);
        return citas.isEmpty() ? null : citas.get(0);
    }

    @Transactional(readOnly = true)
    public List<Cita> getCitasByPaciente(Integer idPaciente) {
        return citaRepository.findCitasByPaciente(idPaciente);
    }
}

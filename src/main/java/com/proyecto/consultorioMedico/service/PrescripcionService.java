package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Prescripcion;
import com.proyecto.consultorioMedico.repository.PrescripcionRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrescripcionService {
    
    @Autowired
    private PrescripcionRepository prescripcionRepository;
    
    @Transactional(readOnly = true)
    public List<Prescripcion> getPrescripciones() {
        return prescripcionRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Optional<Prescripcion> getPrescripcion(Integer idPrescripcion) {
        return prescripcionRepository.findById(idPrescripcion);
    }
    
    @Transactional
    public void save(Prescripcion prescripcion) {
        prescripcionRepository.save(prescripcion);
    }
    
    @Transactional
    public boolean delete(Prescripcion prescripcion) {
        try {
            prescripcionRepository.delete(prescripcion);
            prescripcionRepository.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Transactional(readOnly = true)
    public List<Prescripcion> getPrescripcionesPorCita(Integer idCita) {
        return prescripcionRepository.findByCitaId(idCita);
    }
    
    @Transactional(readOnly = true)
    public List<Prescripcion> getPrescripcionesPorPaciente(Integer idPaciente) {
        return prescripcionRepository.findByPacienteId(idPaciente);
    }
}
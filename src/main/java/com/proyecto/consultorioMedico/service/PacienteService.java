package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {
    
    @Autowired
    private PacienteRepository pacienteRepository;
    
    @Transactional
    public void save(Paciente paciente) {
        pacienteRepository.save(paciente);
    }
    
    @Transactional(readOnly = true)
    public Paciente getPaciente(Paciente paciente) {
        return pacienteRepository.findById(paciente.getIdPaciente()).orElse(null);
    }
} 
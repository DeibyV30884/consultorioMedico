package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.repository.PacienteRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Transactional(readOnly = true)
    public List<Paciente> getPacientes() {
        return pacienteRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Paciente getPaciente(Paciente paciente) {
        return pacienteRepository.findById(paciente.getIdPaciente()).orElse(null);
    }

    @Transactional
    public void save(Paciente paciente) {
        pacienteRepository.save(paciente);
    }

    @Transactional
    public boolean delete(Paciente paciente) {
        try {
            pacienteRepository.delete(paciente);
            pacienteRepository.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

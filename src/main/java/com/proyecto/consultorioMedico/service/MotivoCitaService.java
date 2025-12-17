package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.MotivoCita;
import com.proyecto.consultorioMedico.repository.MotivoCitaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MotivoCitaService {

    @Autowired
    private MotivoCitaRepository motivoCitaRepository;

    @Transactional(readOnly = true)
    public List<MotivoCita> getMotivoCitas() { 
        return motivoCitaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public MotivoCita getMotivoCita( MotivoCita motivoCita) {
        return motivoCitaRepository.findById(motivoCita.getIdMotivoCita()).orElse(null);
    }
}
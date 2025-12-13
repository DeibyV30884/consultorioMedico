package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Medico;
import com.proyecto.consultorioMedico.repository.MedicoRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MedicoService {

    @Autowired
    private MedicoRepository medicoRepository;

    @Transactional(readOnly = true)
    public List<Medico> getMedicos() {
        return medicoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Medico getMedico(Medico medico) {
        return medicoRepository.findById(medico.getIdMedico()).orElse(null);
    }

    @Transactional(readOnly = true)
    public Medico getMedicoPorId(Integer idMedico) {
        return medicoRepository.findById(idMedico).orElse(null);
    }

    @Transactional(readOnly = true)
    public Medico getMedicoPorIdUsuario(Integer idUsuario) {
        return medicoRepository.findByIdUsuario(idUsuario).orElse(null);
    }

    @Transactional
    public void save(Medico medico) {
        medicoRepository.save(medico);
    }

    @Transactional
    public boolean delete(Medico medico) {
        try {
            medicoRepository.delete(medico);
            medicoRepository.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
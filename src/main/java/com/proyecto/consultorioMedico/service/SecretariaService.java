/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.service;
import com.proyecto.consultorioMedico.domain.Secretaria;
import com.proyecto.consultorioMedico.repository.SecretariaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author deiby
 */

@Service
public class SecretariaService {
    
    @Autowired
    private SecretariaRepository secretariaRepository;
    
    @Transactional(readOnly = true)
    public List<Secretaria> getSecretarias() {
        return secretariaRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Secretaria getSecretaria(Secretaria secretaria) {
        return secretariaRepository.findById(secretaria.getIdSecretaria()).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public Secretaria getSecretariaPorId(Integer idSecretaria) {
        return secretariaRepository.findById(idSecretaria).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public Secretaria getSecretariaPorIdUsuario(Integer idUsuario) {
        return secretariaRepository.findByIdUsuario(idUsuario).orElse(null);
    }
    
    @Transactional
    public void save(Secretaria secretaria) {
        secretariaRepository.save(secretaria);
    }
    
    @Transactional
    public boolean delete(Secretaria secretaria) {
        try {
            secretariaRepository.delete(secretaria);
            secretariaRepository.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}

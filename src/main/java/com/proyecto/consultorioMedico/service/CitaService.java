/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.repository.CitaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Alejandro
 */
public class CitaService {
    
    @Autowired
    private CitaRepository citaRepository;

    @Transactional(readOnly = true)
    public List<Cita> getCitas(boolean activo) { 
        var lista = citaRepository.findAll(); // se quito la buqueda por activo de techShop
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
    
    // JPA Ampliada para las citas del dia
}

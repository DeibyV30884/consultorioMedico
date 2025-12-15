/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.repository.PacienteRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Camila
 */
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

    @Transactional(readOnly = true)
    public Paciente getPacientePorId(Integer idPaciente) {
        return pacienteRepository.findById(idPaciente).orElse(null);
    }

    @Transactional(readOnly = true)
    public Paciente getPacientePorIdUsuario(Integer idUsuario) {
        return pacienteRepository.findByIdUsuario(idUsuario).orElse(null);
    }

    @Transactional
    public void save(Paciente paciente) {
        pacienteRepository.save(paciente);
    }

    @Transactional 
    public boolean delete(Paciente paciente){ 
        try{
            pacienteRepository.delete(paciente);
            pacienteRepository.flush(); 
            return true;
        } catch (Exception e){ 
            return false;
        }
    }
    
    @Transactional(readOnly = true)
     public List<Paciente> buscarPaciente(String texto) {
        return pacienteRepository.buscarPaciente(texto);
    }
     
     @Transactional(readOnly = true)
    public List<Paciente> buscarPorNombreOApellido(String termino) {
        return pacienteRepository.buscarPorNombreOApellido(termino.toLowerCase());
    }
}
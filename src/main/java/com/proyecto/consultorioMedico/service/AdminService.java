/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Admin;
import com.proyecto.consultorioMedico.repository.AdminRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author deiby
 */
@Service
public class AdminService {
    
    @Autowired
    private AdminRepository administradorRepository;
    
    @Transactional(readOnly = true)
    public List<Admin> getAdministradores() {
        return administradorRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public Admin getAdministrador(Admin administrador) {
        return administradorRepository.findById(administrador.getIdAdministrador()).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public Admin getAdministradorPorId(Integer idAdministrador) {
        return administradorRepository.findById(idAdministrador).orElse(null);
    }
    
    @Transactional(readOnly = true)
    public Admin getAdministradorPorIdUsuario(Integer idUsuario) {
        return administradorRepository.findByIdUsuario(idUsuario).orElse(null);
    }
    
    @Transactional
    public void save(Admin administrador) {
        administradorRepository.save(administrador);
    }
    
    @Transactional
    public boolean delete(Admin administrador) {
        try {
            administradorRepository.delete(administrador);
            administradorRepository.flush();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
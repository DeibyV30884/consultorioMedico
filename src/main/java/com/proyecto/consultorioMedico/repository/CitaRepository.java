/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Cita;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author Alejandro
 */
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    
    
    // JPA Ampliada para las citas del dia
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Cita;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/**
 *
 * @author Alejandro
 */
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    
    
            @Query(
            value = "SELECT * FROM cita WHERE DATE(fecha_hora) = CURDATE()",
            nativeQuery = true
        )
        List<Cita> buscarCitasHoy();

}

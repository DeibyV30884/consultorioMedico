/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Prescripcion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author deiby
 */
public interface PrescripcionRepository extends JpaRepository<Prescripcion, Integer> {

    @Query("SELECT p FROM Prescripcion p WHERE p.cita.idCita = :idCita ORDER BY p.fechaCreacion DESC")
    List<Prescripcion> findByCitaId(@Param("idCita") Integer idCita);

    @Query("SELECT p FROM Prescripcion p WHERE p.cita.paciente.idPaciente = :idPaciente ORDER BY p.fechaCreacion DESC")
    List<Prescripcion> findByPacienteId(@Param("idPaciente") Integer idPaciente);
}

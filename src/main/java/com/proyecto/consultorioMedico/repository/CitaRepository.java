/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Cita;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author Alejandro
 */
public interface CitaRepository extends JpaRepository<Cita, Integer> {
    
    @Query("SELECT c FROM Cita c WHERE c.medico.idMedico = :idMedico " +
           "AND c.fecha = :fecha " +
           "AND c.hora = :hora " +
           "AND c.estado != 'Cancelada'")
    List<Cita> findByMedicoFechaHora(
        @Param("idMedico") Integer idMedico,
        @Param("fecha") LocalDate fecha,
        @Param("hora") LocalTime hora
    );
    
    @Query("SELECT c FROM Cita c WHERE c.medico.idMedico = :idMedico " +
           "AND c.fecha = :fecha " +
           "AND c.hora = :hora " +
           "AND c.estado != 'Cancelada' " +
           "AND c.idCita != :idCitaExcluir")
    List<Cita> findByMedicoFechaHoraExcluyendo(
        @Param("idMedico") Integer idMedico,
        @Param("fecha") LocalDate fecha,
        @Param("hora") LocalTime hora,
        @Param("idCitaExcluir") Integer idCitaExcluir
    );
    
    @Query("SELECT c.hora FROM Cita c WHERE c.medico.idMedico = :idMedico " +
           "AND c.fecha = :fecha " +
           "AND c.estado != 'Cancelada'")
    List<LocalTime> findHorasOcupadas(
        @Param("idMedico") Integer idMedico,
        @Param("fecha") LocalDate fecha
    );
    
    @Query("SELECT c FROM Cita c WHERE c.paciente.idPaciente = :idPaciente " +
           "ORDER BY c.fecha DESC, c.hora DESC")
    List<Cita> findByPacienteId(@Param("idPaciente") Integer idPaciente);
    
    @Query("SELECT c FROM Cita c WHERE c.fecha = CURRENT_DATE " +
           "AND (c.estado = 'Pendiente' OR c.estado = 'Confirmada') " +
           "ORDER BY c.hora ASC")
    List<Cita> findCitasHoy();
    
    @Query("SELECT c FROM Cita c WHERE c.fecha = CURRENT_DATE " +
           "ORDER BY c.hora ASC")
    List<Cita> findTodasCitasHoy();
    
    @Query("SELECT c FROM Cita c WHERE c.paciente.idPaciente = :idPaciente " +
           "AND c.estado = 'Completada' " +
           "ORDER BY c.fecha DESC, c.hora DESC")
    List<Cita> findUltimaCitaCompletada(@Param("idPaciente") Integer idPaciente);
    
}

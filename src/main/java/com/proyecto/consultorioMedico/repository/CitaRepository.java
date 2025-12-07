/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Cita;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    @Query("SELECT c FROM Cita c WHERE c.medico.idMedico = :idMedico "
            + "AND c.fechaHora BETWEEN :inicio AND :fin "
            + "AND c.estado != 'CANCELADA' "
            + "AND (:idCita IS NULL OR c.idCita != :idCita)")
    List<Cita> findCitasEnRango(
            @Param("idMedico") Integer idMedico,
            @Param("inicio") LocalDateTime inicio,
            @Param("fin") LocalDateTime fin,
            @Param("idCita") Integer idCita
    );
}

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Paciente;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    @Query(
            nativeQuery = true,
            value = "SELECT * FROM paciente "
            + "WHERE nombre LIKE CONCAT('%', :texto, '%') "
            + "   OR apellido_1 LIKE CONCAT('%', :texto, '%') "
            + "   OR apellido_2 LIKE CONCAT('%', :texto, '%') "
    )
    List<Paciente> buscarPaciente(@Param("texto") String texto);
    
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM paciente WHERE id_usuario = :idUsuario")
    Optional<Paciente> findByIdUsuario(@Param("idUsuario") Integer idUsuario);
}

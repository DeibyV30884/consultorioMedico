/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.repository;
import com.proyecto.consultorioMedico.domain.Secretaria;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 *
 * @author deiby
 */

public interface SecretariaRepository extends JpaRepository<Secretaria, Integer> {
    
    @Query(
        nativeQuery = true,
        value = "SELECT * FROM secretaria WHERE id_usuario = :idUsuario"
    )
    Optional<Secretaria> findByIdUsuario(@Param("idUsuario") Integer idUsuario);
}
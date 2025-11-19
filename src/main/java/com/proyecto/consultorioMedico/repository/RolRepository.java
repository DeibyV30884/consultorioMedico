package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RolRepository extends JpaRepository<Rol, Integer> {
    

    @Query(value = "SELECT rol FROM Rol rol WHERE rol.nombre = :nombre")
            
    Optional <Rol> buscarPorNombreJPQL(@Param("nombre") String nombre);
    
}
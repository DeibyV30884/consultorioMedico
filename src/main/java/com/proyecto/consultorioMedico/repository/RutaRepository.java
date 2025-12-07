package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Ruta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RutaRepository extends JpaRepository<Ruta, Integer> {
    
    List<Ruta> findAllByOrderByRequiereRolAsc();
}
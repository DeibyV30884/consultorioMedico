package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PacienteRepository extends JpaRepository<Paciente, Integer> {
    
    
}
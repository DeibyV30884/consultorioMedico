
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Medico;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MedicoRepository extends JpaRepository<Medico, Integer>{
    
}

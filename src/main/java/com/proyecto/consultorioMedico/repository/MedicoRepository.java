
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Medico;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface MedicoRepository extends JpaRepository<Medico, Integer>{
    
    @Query(
            nativeQuery = true,
            value = "SELECT * FROM medico WHERE id_usuario = :idUsuario")
    Optional<Medico> findByIdUsuario(@Param("idUsuario") Integer idUsuario);
}

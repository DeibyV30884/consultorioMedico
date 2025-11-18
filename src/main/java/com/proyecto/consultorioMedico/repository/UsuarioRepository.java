package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {
    
    Optional<Usuario> findByUsername(String username);
    
    Optional<Usuario> findByCorreo(String correo);
}
package com.proyecto.consultorioMedico.repository;

import com.proyecto.consultorioMedico.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> { 
      
    @Query(nativeQuery = true,
           value = "SELECT * FROM usuario WHERE username = :username")
            
    Optional<Usuario> findByUsernameNativo(@Param ("username") String username);
    
    
    
    @Query(nativeQuery = true,
           value = "SELECT * FROM usuario WHERE correo = :correo")
            
    Optional<Usuario> findByCorreoNativo(@Param("correo") String correo) ;
    
     @Query(nativeQuery = true,
           value = "SELECT * FROM usuario WHERE username = :username AND activo = true")
    Optional<Usuario> findByUsernameAndActivoTrue(@Param("username") String username);
}


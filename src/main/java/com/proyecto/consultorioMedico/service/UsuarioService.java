package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.repository.UsuarioRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Transactional
    public void save(Usuario usuario) {
        usuarioRepository.save(usuario); 
        
    }

    @Transactional(readOnly = true)
    public boolean existeUsername(String username) {
    return usuarioRepository.findByUsernameNativo(username).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
     return usuarioRepository.findByCorreoNativo(correo).isPresent();
    }  
    
    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorUsernameYPassword(String username, String password) {
        return usuarioRepository.findByUsernameAndPassword(username, password);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario>  getUsuarioPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> getUsuarioPorCorreo(String correo) {
        if (correo != null && !correo.isEmpty())  {
            return usuarioRepository.findByCorreoNativo(correo);
        }
        
        return  Optional.empty();
    }

    @Transactional(readOnly = true)
    public Usuario getUsuarioLogueado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        return usuarioRepository.findByUsernameNativo(username).orElse(null);
    }
}
package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
        return usuarioRepository.findByUsername(username).isPresent();
    }

    @Transactional(readOnly = true)
    public boolean existeCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo).isPresent();
    }

}

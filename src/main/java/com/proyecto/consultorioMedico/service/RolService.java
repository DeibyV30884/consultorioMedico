package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Rol;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.repository.RolRepository;
import com.proyecto.consultorioMedico.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class RolService {
    
    @Autowired
    private RolRepository rolRepository;
    
    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Transactional(readOnly = true)
    public Rol getRolByNombre(String nombre) {
        return rolRepository.buscarPorNombreJPQL(nombre).orElse(null);
    }
    
    @Transactional
    public void asignarRolAUsuario(Usuario usuario, Rol rol) { 
        if (usuario.getRoles() == null) {
            usuario.setRoles(new ArrayList<>());
        }
        usuario.getRoles().add(rol);
        
        usuarioRepository.save(usuario);
    }
}
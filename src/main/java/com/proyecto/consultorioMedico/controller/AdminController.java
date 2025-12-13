package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping ("/admin")
public class AdminController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @ModelAttribute("usuario")
    public Usuario agregarUsuarioLogueado() {
        return usuarioService.getUsuarioLogueado();
    }
    
    private boolean validarAcceso(Integer idUsuario) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        return usuarioLogueado != null && usuarioLogueado.getIdUsuario().equals(idUsuario);
    }
    
    @GetMapping("/reportes")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel de Administracion");
        return "admin/reportes"; 
    }
    
    @GetMapping("/usuarios")
    public String usuarios (Model model) {
        model.addAttribute("titulo", "Usuarios");
        return "admin/usuarios";
    }
    
    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable("id") Integer id, Model model) {
        if (!validarAcceso(id)) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", "perfil");
        return "admin/perfil";
    }
    
}
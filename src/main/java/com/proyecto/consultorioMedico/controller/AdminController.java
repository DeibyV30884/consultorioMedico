package com.proyecto.consultorioMedico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping ("/admin")
public class AdminController {
    
    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel de Administracion");
        return "admin/inicio"; 
    }
    
    @GetMapping("/usuarios")
    public String usuarios (Model model) {
        model.addAttribute("titulo", "Usuarios");
        return "admin/usuarios";
    }
    
    @GetMapping("/perfil")
    public String perfil (Model model) {
        model.addAttribute("titulo", "perfil" );
        return "admin/perfil";
    }
    
}
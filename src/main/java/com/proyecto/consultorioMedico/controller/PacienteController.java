package com.proyecto.consultorioMedico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/paciente")
public class PacienteController {
    
    @GetMapping("/inicio")
    public String inicioPaciente(Model model) {
        model.addAttribute("titulo", "Mi Panel");
        return "paciente/inicio";
    }
    
    @GetMapping("/perfil")
    public String perfil (Model model) {
        model.addAttribute("titulo", "perfil" );
        return "paciente/perfil";
    }
    
    @GetMapping("/tratamientos ")
    public String tratamientos(Model model) {
        model.addAttribute("titulo", "Mis tratamientos");
        return "paciente/tratamientos";
    }
    
}
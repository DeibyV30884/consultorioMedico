package com.proyecto.consultorioMedico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/medico")
public class MedicoController {
    
    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel Médico");
        return "medico/inicio";
    }
    
    @GetMapping("/perfil")
    public String perfil (Model model) {
        model.addAttribute("titulo", "perfil" );
        return "medico/perfil";
    }
    
    @GetMapping("/expedientes")
    public String expedientes (Model model) {
        model.addAttribute("titulo", "expedientes" );
        return "medico/expedientes";
    }
}
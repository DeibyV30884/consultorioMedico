package com.proyecto.consultorioMedico.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/secretaria")
public class SecretariaController {
    
    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel de Secretaría");
        return "secretaria/inicio";
    }
    
    @GetMapping("/perfil")
    public String perfil (Model model) {
        model.addAttribute("titulo", "perfil" );
        return "secretaria/perfil";  
    } 
    
    @GetMapping("/citas")
    public String citas (Model model) {
        model.addAttribute("titulo", "citas" );
        return "secretaria/citas";  
    } 
    
    @GetMapping("/pacientes")
    public String pacientes (Model model) {
        model.addAttribute("titulo", "pacientes" );
        return "secretaria/pacientes";  
    } 
}
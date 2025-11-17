package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.service.CitaService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/secretaria")
public class SecretariaController {

    @Autowired
    private CitaService citaService;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel de Secretaría");
        return "secretaria/inicio";
    }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        model.addAttribute("titulo", "Pacientes");
        return "secretaria/pacientes";
    }

    @GetMapping("/citas")
    public String citas(Model model) {
        List<Cita> lista = citaService.getCitas();
        model.addAttribute("citas", lista);
        model.addAttribute("totalCitas", lista.size());
        return "secretaria/citas";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("titulo", "perfil");
        return "secretaria/perfil";
    }
}

package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/paciente")
public class PacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    private boolean validarAcceso(Integer idPaciente) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Paciente pacienteLogueado = pacienteService.getPacientePorIdUsuario(usuarioLogueado.getIdUsuario());
        
        return pacienteLogueado != null && pacienteLogueado.getIdPaciente().equals(idPaciente);
    }
    
    @GetMapping("/inicio/{idPaciente}")
    public String inicioPaciente(@PathVariable Integer idPaciente, Model model) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        model.addAttribute("titulo", "Mi Panel");
        model.addAttribute("paciente", paciente);
        return "paciente/inicio";
    }
    
    @GetMapping("/perfil/{idPaciente}")
    public String perfil(@PathVariable Integer idPaciente, Model model) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        model.addAttribute("titulo", "Perfil");
        model.addAttribute("paciente", paciente);
        return "paciente/perfil";
    }
    
    @GetMapping("/tratamientos/{idPaciente}")
    public String tratamientos(@PathVariable Integer idPaciente, Model model) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        model.addAttribute("titulo", "Mis Tratamientos");
        model.addAttribute("paciente", paciente);
        return "paciente/tratamientos";
    }
    
    @GetMapping("/citas/{idPaciente}")
    public String citas(@PathVariable Integer idPaciente, Model model) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        model.addAttribute("titulo", "Citas");
        model.addAttribute("paciente", paciente);
        return "paciente/citas";
    }
    
    @PostMapping("/guardar/{idPaciente}")
    public String guardarPaciente(@PathVariable Integer idPaciente, Paciente paciente, Model model) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        paciente.setIdPaciente(idPaciente);
        pacienteService.save(paciente);
        return "redirect:/paciente/perfil/" + idPaciente;
    }
}
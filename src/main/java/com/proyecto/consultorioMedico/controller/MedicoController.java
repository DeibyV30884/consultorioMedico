package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Medico;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

@RequestMapping("/medico")
public class MedicoController {
    
    @Autowired
    private MedicoService medicoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    private boolean validarAcceso(Integer idMedico) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medicoLogueado = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        return medicoLogueado != null && medicoLogueado.getIdMedico().equals(idMedico);
    }
    
    @GetMapping("/inicio/{idMedico}")
    public String inicio(@PathVariable Integer idMedico, Model model) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", "Panel Médico");
        model.addAttribute("medico", medico);
        return "medico/inicio";
    }
    
    @GetMapping("/perfil/{idMedico}")
    public String perfil(@PathVariable Integer idMedico, Model model) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico =medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", "Perfil");
        model.addAttribute("medico", medico);
        return "medico/perfil";
    }
    
    @GetMapping("/expedientes/{idMedico}")
    public String expedientes(@PathVariable Integer idMedico, Model model) {
        if  (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", "Expedientes");
        model.addAttribute("medico", medico);
        return "medico/expedientes";
    }
    
    @PostMapping("/guardar/{idMedico}")
    public String guardarMedico(@PathVariable Integer idMedico, Medico medico, Model model) {
        if (!validarAcceso(idMedico)){
            return  "redirect:/";
        }
        
        medico.setIdMedico(idMedico);
        medicoService.save(medico);
        return "redirect:/medico/perfil/" + idMedico;
    }
    
}
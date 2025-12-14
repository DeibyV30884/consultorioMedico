package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;

@Controller
@RequestMapping("/paciente")
public class PacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private MessageSource messageSource;
    
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
        if (paciente == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", "Mi Perfil");
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
        model.addAttribute("titulo", "Mis Citas");
        model.addAttribute("paciente", paciente);
        return "paciente/citas";
    }
    
    @PostMapping("/guardar/{idPaciente}")
    public String guardarPaciente(
            @PathVariable Integer idPaciente, 
            Paciente paciente, 
            RedirectAttributes redirectAttributes) {
        
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        try {
            Paciente pacienteExistente = pacienteService.getPacientePorId(idPaciente);
            
            if (pacienteExistente == null) {
                redirectAttributes.addFlashAttribute("error", "Paciente no encontrado");
                return "redirect:/";
            }
            
            paciente.setIdPaciente(idPaciente);
            paciente.setIdUsuario(pacienteExistente.getIdUsuario());
            paciente.setFechaCreacion(pacienteExistente.getFechaCreacion());
            paciente.setFechaModificacion(LocalDateTime.now());
            
            pacienteService.save(paciente);
            
            redirectAttributes.addFlashAttribute("mensaje", "Perfil actualizado correctamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }
        
        return "redirect:/paciente/perfil/" + idPaciente;
    }
}
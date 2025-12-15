package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.service.CitaService;
import java.util.List;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import com.proyecto.consultorioMedico.domain.Usuario;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller

@RequestMapping("/secretaria")
public class SecretariaController {

    @Autowired
    private CitaService citaService;
  
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private MedicoService medicoService;
    
    @ModelAttribute("usuario")
    public Usuario agregarUsuarioLogueado() {
        return usuarioService.getUsuarioLogueado();
    }
    
    private boolean validarAcceso(Integer idUsuario) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        return usuarioLogueado != null && usuarioLogueado.getIdUsuario().equals(idUsuario);
    }
    
    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel de Secretaría");
        return "secretaria/inicio";
    }
    
    @GetMapping("/perfil/{id}")
    public String perfil(@PathVariable("id") Integer id, Model model) {
        if (!validarAcceso(id)) {
            return "redirect:/";
        }
        
            model.addAttribute("titulo", "perfil" );
            return "secretaria/perfil";  
        } 
    
    @GetMapping("/citas")
    public String citas(Model model) {
        List<Cita> lista = citaService.getCitas();
        model.addAttribute("citas", lista);
        model.addAttribute("cita", new Cita());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("totalCitas", lista.size());
        return "secretaria/citas";
    }
    
    
    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        var pacientes = pacienteService.getPacientes();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("titulo", "pacientes");
        return "secretaria/pacientes";
    }
    
    @GetMapping("/citasRegistro")
    public String citasRegistro(Model model) {
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        return "secretaria/citasRegistro";
    }
    
    @PostMapping("/citasRegistro/buscar")
    public String buscarPacienteRegistro(@RequestParam(value = "texto") String texto, Model model) {
        var pacientesEncontrados = pacienteService.buscarPorNombreOApellido(texto);
        
        model.addAttribute("pacientesEncontrados", pacientesEncontrados);
        model.addAttribute("texto", texto);
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        
        return "secretaria/citasRegistro";
    }
}
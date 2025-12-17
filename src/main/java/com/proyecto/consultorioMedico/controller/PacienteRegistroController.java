/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 *
 * @author Camila
 */
@Controller
@RequestMapping("/secretaria/pacientes")
public class PacienteRegistroController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private CitaService citaService;
    
    @ModelAttribute("usuario")
    public Usuario agregarUsuarioLogueado() {
        return usuarioService.getUsuarioLogueado();
    }

    @GetMapping("/listado") // https:localhost/paciente/listado
    public String inicio(Model model, Locale locale) {
        var pacientes = pacienteService.getPacientes();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("totalPacientes", pacientes.size());
        model.addAttribute("titulo", messageSource.getMessage("paciente.listado", null, locale));
        return "/secretaria/pacientes"; //las vistas que yo voy a crear en el html
    }

    @PostMapping("/modificar")
    public String modificar(Paciente paciente, Model model) {
        paciente = pacienteService.getPaciente(paciente);
        model.addAttribute("paciente", paciente);
        return "/secretaria/pacientes";
    }

    @PostMapping("/guardar")
    public String guardar(Paciente paciente,
            @RequestParam(required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        if (paciente.getFechaCreacion() == null) {
            paciente.setFechaCreacion(LocalDateTime.now());
        }
        paciente.setFechaModificacion(LocalDateTime.now());
        pacienteService.save(paciente);

        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado", null, locale));
        return "redirect:/secretaria/pacientes";
    }

    @PostMapping("/eliminar")
    public String eliminar(Paciente paciente, RedirectAttributes redirectAttributes, Locale locale) {
        // Obtener el paciente completo
        paciente = pacienteService.getPaciente(paciente);
        
        if (paciente == null) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("paciente.no.encontrado", null, locale));
            return "redirect:/secretaria/pacientes";
        }
        
        List<Cita> citasDelPaciente = citaService.getCitasPorPaciente(paciente.getIdPaciente());
        
        if (citasDelPaciente != null && !citasDelPaciente.isEmpty()) {
            String mensaje = messageSource.getMessage("paciente.error.eliminar.citas", 
                    new Object[]{citasDelPaciente.size()}, locale);
            redirectAttributes.addFlashAttribute("error", mensaje);
            return "redirect:/secretaria/pacientes";
        }
        
        // Si no hay citas, intentar eliminar el paciente
        if (pacienteService.delete(paciente)) {
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado", null, locale));
        } else {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("paciente.error.eliminar", null, locale));
        }     
        return "redirect:/secretaria/pacientes";
    }

    @PostMapping("/queryNombre")
    public String consultaPorNombre(
            @RequestParam(value = "texto") String texto, Model model, Locale locale) {
        var pacientes = pacienteService.buscarPaciente(texto);
        model.addAttribute("texto", texto);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("titulo", messageSource.getMessage("paciente.listado", null, locale));
        return "/secretaria/pacientes"; //las vistas que yo voy a crear en el html
    }
}
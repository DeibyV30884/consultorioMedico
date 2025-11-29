/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.service.PacienteService;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @GetMapping("/listado") // https:localhost/paciente/listado
    public String inicio(Model model) {
        var pacientes = pacienteService.getPacientes();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("totalPacientes", pacientes.size());
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
            RedirectAttributes redirectAttributes) {
        if (paciente.getFechaCreacion() == null) {
            paciente.setFechaCreacion(LocalDateTime.now());
        }
        paciente.setFechaModificacion(LocalDateTime.now());
        pacienteService.save(paciente);

        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado",
                        null,
                        Locale.getDefault()));
        return "redirect:/secretaria/pacientes";
    }

    @PostMapping("/eliminar")
    public String eliminar(Paciente paciente, RedirectAttributes redirectAttributes) {
        paciente = pacienteService.getPaciente(paciente);
        if (paciente == null) {  // La paciente no existe...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("paciente.error01",
                            null,
                            Locale.getDefault()));
        } else if (false) { // Esto se actualiza proximas semanas...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("paciente.error02",
                            null,
                            Locale.getDefault()));
        } else if (pacienteService.delete(paciente)) {
            // Si se borró...
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado",
                            null,
                            Locale.getDefault()));
        } else {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("paciente.error03",
                            null,
                            Locale.getDefault()));
        }
        return "redirect:/secretaria/pacientes";
    }

    @PostMapping("/queryNombre")
    public String consultaPorNombre(
            @RequestParam(value = "texto") String texto, Model model) {
        var pacientes = pacienteService.buscarPaciente(texto);
        model.addAttribute("texto", texto);
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("paciente", new Paciente());
        return "/secretaria/pacientes"; //las vistas que yo voy a crear en el html
    }
}

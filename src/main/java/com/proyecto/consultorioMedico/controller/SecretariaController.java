package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.service.CitaService;
import java.util.List;
import com.proyecto.consultorioMedico.service.PacienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.proyecto.consultorioMedico.domain.Paciente;
import org.springframework.beans.factory.annotation.Autowired;

@Controller

@RequestMapping("/secretaria")
public class SecretariaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private PacienteService pacienteService;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel de Secretaría");
        List<Cita> citas = citaService.buscarCitasHoy();

        int total = citas.size();
        int completas = 0;
        int pendientes = 0;

        for (Cita c : citas) {
            if (c.getEstado() == EstadoCita.Completada) {
                completas++;
            }
            if (c.getEstado() == EstadoCita.Pendiente) {
                pendientes++;
            }
        }

        model.addAttribute("total", total);
        model.addAttribute("citasproximas", citas);
        model.addAttribute("completas", completas);
        model.addAttribute("pendientes", pendientes);

        return "secretaria/inicio";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("titulo", "perfil");
        return "secretaria/perfil";
    }

    @GetMapping("/citas")
    public String citas(Model model) {
        List<Cita> lista = citaService.getCitas();
        model.addAttribute("citas", lista);
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
}

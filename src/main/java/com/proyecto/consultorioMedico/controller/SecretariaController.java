package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.service.CitaService;
import java.util.List;
import com.proyecto.consultorioMedico.service.PacienteService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.TipoConsultaService;
import org.springframework.beans.factory.annotation.Autowired;

@Controller

@RequestMapping("/secretaria")
public class SecretariaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private TipoConsultaService tipoConsultaService;

    @Autowired
    private PacienteService pacienteService;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel de Secretaría");
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
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("tiposConsulta", tipoConsultaService.getTipoConsultas());
        model.addAttribute("totalCitas", lista.size());
        return "secretaria/citas";
    }

    @GetMapping("/pacientes")
    public String pacientes(Model model) {
        var pacientes = pacienteService.getPacientes();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("titulo", "pacientes");
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("tiposConsulta", tipoConsultaService.getTipoConsultas());
        return "secretaria/pacientes";
    }
}

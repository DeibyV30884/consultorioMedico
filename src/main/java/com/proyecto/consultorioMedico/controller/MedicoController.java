package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.MotivoCitaService;
import com.proyecto.consultorioMedico.service.PacienteService;
import java.util.List;
import java.util.Optional;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller

@RequestMapping("/medico")
public class MedicoController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private MotivoCitaService motivoCitaService;

    @Autowired
    private PacienteService pacienteService;

    @GetMapping("/inicio")
    public String inicio(Model model) {
        model.addAttribute("titulo", "Panel Médico");

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

        return "medico/inicio";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        model.addAttribute("titulo", "perfil");
        return "medico/perfil";
    }

    @GetMapping("/expedientes")
    public String expedientes(Model model) {
        model.addAttribute("titulo", "expedientes");
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("titulo", "pacientes");
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("motivosCita", motivoCitaService.getMotivoCitas());
        return "medico/expedientes";
    }

    @GetMapping("/atender-cita/{idCita}")
    public String atenderCita(@PathVariable Integer idCita, Model model) {
        Optional<Cita> citaOpt = citaService.getCita(idCita);
        if (citaOpt.isEmpty()) {
            return "redirect:/medico/inicio";
        }
        Cita citaActual = citaOpt.get();
        Paciente paciente = citaActual.getPaciente();
        Cita ultimaCita = citaService.getUltimaCitaCompletada(paciente.getIdPaciente());
        model.addAttribute("cita", citaActual);
        model.addAttribute("paciente", paciente);
        model.addAttribute("ultimaCita", ultimaCita);
        

        return "medico/atenderCita";
    }

    @PostMapping("/guardar-consulta")
public String guardarConsulta(Cita cita,Paciente paciente, RedirectAttributes redirectAttributes) {
    try {
        
        Optional<Cita> citaActualOpt = citaService.getCita(cita.getIdCita());
        
        if (citaActualOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
            return "redirect:/medico/inicio";
        }
        
        Cita citaActual = citaActualOpt.get();
        
        citaActual.setObservaciones(cita.getObservaciones());
        citaActual.setTratamiento(cita.getTratamiento());
        citaActual.setEstado(cita.getEstado());
                
        citaService.save(citaActual);
        
        redirectAttributes.addFlashAttribute("todoOk", "Consulta guardada exitosamente");
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        e.printStackTrace();
    }
    return "redirect:/medico/inicio";
}

    @PostMapping("/ver-expediente")
    public String verExpediente(@RequestParam Integer idPaciente, Model model, RedirectAttributes redirectAttributes) {
        Paciente pacienteTemp = new Paciente();
        pacienteTemp.setIdPaciente(idPaciente);
        Paciente paciente = pacienteService.getPaciente(pacienteTemp);

        if (paciente == null) {
            redirectAttributes.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/medico/expedientes";
        }

        model.addAttribute("paciente", paciente);
        return "medico/modificarExpediente";
    }

    @PostMapping("/guardarExpediente")
    public String guardarExpediente(Paciente paciente, RedirectAttributes redirectAttributes) {
        try {
            Paciente pacienteActual = pacienteService.getPaciente(paciente);
                
                pacienteActual.setAntecedentesHeredoFamiliares(paciente.getAntecedentesHeredoFamiliares());
                pacienteActual.setAntecedentesPersonales(paciente.getAntecedentesPersonales());
                pacienteActual.setAntecedentesQuirurgicos(paciente.getAntecedentesQuirurgicos());
                pacienteActual.setAntecedentesGinecoObstetricos(paciente.getAntecedentesGinecoObstetricos());

                pacienteService.save(pacienteActual);
                redirectAttributes.addFlashAttribute("todoOk", "Expediente actualizado exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar expediente");
        }

        return "redirect:/medico/expedientes";
    }
}
    
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

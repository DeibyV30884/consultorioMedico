package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
import java.util.List;
import java.util.Optional;
import com.proyecto.consultorioMedico.domain.Medico;
import com.proyecto.consultorioMedico.domain.Usuario;
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
    private PacienteService pacienteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    private boolean validarAcceso(Integer idMedico) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medicoLogueado = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        return medicoLogueado != null && medicoLogueado.getIdMedico().equals(idMedico);
    }

    @GetMapping("/inicio")
    public String inicio(Model model) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", "Panel Médico");
        model.addAttribute("medico", medico);

        List<Cita> citasProximas = citaService.buscarCitasHoy();
        List<Cita> todasCitasHoy = citaService.buscarTodasCitasHoy();

        int total = todasCitasHoy.size();
        int completas = 0;
        int pendientes = 0;

        for (Cita c : todasCitasHoy) {
            if (EstadoCita.COMPLETADA.equals(c.getEstado())) {
                completas++;
            }
            if (EstadoCita.PENDIENTE.equals(c.getEstado())) {
                pendientes++;
            }
        }

        model.addAttribute("total", total);
        model.addAttribute("citasproximas", citasProximas);
        model.addAttribute("completas", completas);
        model.addAttribute("pendientes", pendientes);

        return "medico/inicio";
    }

    @GetMapping("/inicio/{idMedico}")
    public String inicioConId(@PathVariable Integer idMedico, Model model) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", "Panel Médico");
        model.addAttribute("medico", medico);
        
        List<Cita> citasProximas = citaService.buscarCitasHoy();
        List<Cita> todasCitasHoy = citaService.buscarTodasCitasHoy();

        int total = todasCitasHoy.size();
        int completas = 0;
        int pendientes = 0;

        for (Cita c : todasCitasHoy) {
            if (EstadoCita.COMPLETADA.equals(c.getEstado())) {
                completas++;
            }
            if (EstadoCita.PENDIENTE.equals(c.getEstado())) {
                pendientes++;
            }
        }

        model.addAttribute("total", total);
        model.addAttribute("citasproximas", citasProximas);
        model.addAttribute("completas", completas);
        model.addAttribute("pendientes", pendientes);
        
        return "medico/inicio";
    }

    @GetMapping("/perfil")
    public String perfil(Model model) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", "Perfil");
        model.addAttribute("medico", medico);
        return "medico/perfil";
    }
    
    @GetMapping("/perfil/{idMedico}")
    public String perfilConId(@PathVariable Integer idMedico, Model model) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", "Perfil");
        model.addAttribute("medico", medico);
        return "medico/perfil";
    }

    @GetMapping("/expedientes")
    public String expedientes(Model model) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", "Expedientes");
        model.addAttribute("medico", medico);
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("medicos", medicoService.getMedicos());
        return "medico/expedientes";
    }
    
    @GetMapping("/expedientes/{idMedico}")
    public String expedientesConId(@PathVariable Integer idMedico, Model model) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", "Expedientes");
        model.addAttribute("medico", medico);
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("medicos", medicoService.getMedicos());
        return "medico/expedientes";
    }
    
    @PostMapping("/expedientes/buscar")
    public String buscarPacienteExpediente(@RequestParam(value = "texto") String texto, Model model) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        var pacientesEncontrados = pacienteService.buscarPorNombreOApellido(texto);
        
        model.addAttribute("medico", medico);
        model.addAttribute("pacientes", pacientesEncontrados);
        model.addAttribute("texto", texto);
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("titulo", "Expedientes");
        
        return "medico/expedientes";
    }

    @GetMapping("/atender-cita/{idCita}")
    public String atenderCita(@PathVariable Integer idCita, Model model) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        Optional<Cita> citaOpt = citaService.getCita(idCita);
        if (citaOpt.isEmpty()) {
            return "redirect:/medico/inicio";
        }
        Cita citaActual = citaOpt.get();
        Paciente paciente = citaActual.getPaciente();
        Cita ultimaCita = citaService.getUltimaCitaCompletada(paciente.getIdPaciente());
        
        model.addAttribute("medico", medico);
        model.addAttribute("cita", citaActual);
        model.addAttribute("paciente", paciente);
        model.addAttribute("ultimaCita", ultimaCita);
        
        return "medico/atenderCita";
    }

    @PostMapping("/guardar-consulta")
    public String guardarConsulta(Cita cita, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
            Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
            
            Optional<Cita> citaActualOpt = citaService.getCita(cita.getIdCita());
            
            if (citaActualOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                return medico != null ? "redirect:/medico/inicio/" + medico.getIdMedico() : "redirect:/medico/inicio";
            }
            
            Cita citaActual = citaActualOpt.get();
            
            // Validar que el estado sea válido
            if (!EstadoCita.esValido(cita.getEstado())) {
                redirectAttributes.addFlashAttribute("error",
                    "Estado de cita inválido. Debe ser: Pendiente, Confirmada, Completada o Cancelada.");
                return medico != null ? "redirect:/medico/inicio/" + medico.getIdMedico() : "redirect:/medico/inicio";
            }
            
            citaActual.setObservaciones(cita.getObservaciones());
            citaActual.setTipoConsulta(cita.getTipoConsulta());
            citaActual.setTratamiento(cita.getTratamiento());
            citaActual.setEstado(cita.getEstado());
                    
            citaService.save(citaActual);
            
            redirectAttributes.addFlashAttribute("todoOk", "Consulta guardada exitosamente");
            
            
            return medico != null ? "redirect:/medico/inicio/" + medico.getIdMedico() : "redirect:/medico/inicio";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
            e.printStackTrace();
            
            return "redirect:/medico/inicio";
        }
    }

    @PostMapping("/ver-expediente")
    public String verExpediente(@RequestParam Integer idPaciente, Model model, RedirectAttributes redirectAttributes) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        Paciente pacienteTemp = new Paciente();
        pacienteTemp.setIdPaciente(idPaciente);
        Paciente paciente = pacienteService.getPaciente(pacienteTemp);

        if (paciente == null) {
            redirectAttributes.addFlashAttribute("error", "Paciente no encontrado");
            return "redirect:/medico/expedientes/" + medico.getIdMedico();
        }

        model.addAttribute("medico", medico);
        model.addAttribute("paciente", paciente);
        return "medico/modificarExpediente";
    }

    @PostMapping("/guardarExpediente")
    public String guardarExpediente(Paciente paciente, RedirectAttributes redirectAttributes) {
        try {
            Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
            Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
            
            Paciente pacienteActual = pacienteService.getPaciente(paciente);
                
                pacienteActual.setAntecedentesHeredoFamiliares(paciente.getAntecedentesHeredoFamiliares());
                pacienteActual.setAntecedentesPersonales(paciente.getAntecedentesPersonales());
                pacienteActual.setAntecedentesQuirurgicos(paciente.getAntecedentesQuirurgicos());
                pacienteActual.setAntecedentesGinecoObstetricos(paciente.getAntecedentesGinecoObstetricos());

                pacienteService.save(pacienteActual);
                redirectAttributes.addFlashAttribute("todoOk", "Expediente actualizado exitosamente");
            
            return medico != null ? "redirect:/medico/expedientes/" + medico.getIdMedico() : "redirect:/medico/expedientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar expediente");
            return "redirect:/medico/expedientes";
        }
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
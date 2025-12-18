package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Prescripcion;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.MotivoCitaService;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.PrescripcionService;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import com.proyecto.consultorioMedico.domain.Medico;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
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
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PrescripcionService prescripcionService;
    
    @Autowired
    private MessageSource messageSource;
    
    private boolean validarAcceso(Integer idMedico) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medicoLogueado = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        return medicoLogueado != null && medicoLogueado.getIdMedico().equals(idMedico);
    }

    @GetMapping("/inicio")
    public String inicio(Model model, Locale locale) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", messageSource.getMessage("medico.panel", null, locale));
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
    public String inicioConId(@PathVariable Integer idMedico, Model model, Locale locale) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", messageSource.getMessage("medico.panel", null, locale));
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
    public String perfil(Model model, Locale locale) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", messageSource.getMessage("sidebar.perfil", null, locale));
        model.addAttribute("medico", medico);
        model.addAttribute("usuario", usuarioLogueado);
        return "medico/perfil";
    }
    
    @GetMapping("/perfil/{idMedico}")
    public String perfilConId(@PathVariable Integer idMedico, Model model, Locale locale) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        Usuario usuario = usuarioService.getUsuarioPorId(medico.getIdUsuario()).orElse(null);
        
        if (usuario == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", messageSource.getMessage("sidebar.perfil", null, locale));
        model.addAttribute("medico", medico);
        model.addAttribute("usuario", usuario);
        return "medico/perfil";
    }

    @GetMapping("/expedientes")
    public String expedientes(Model model, Locale locale) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", messageSource.getMessage("sidebar.expedientes", null, locale));
        model.addAttribute("medico", medico);
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("motivosCita", motivoCitaService.getMotivoCitas());
        return "medico/expedientes";
    }
    
    @GetMapping("/expedientes/{idMedico}")
    public String expedientesConId(@PathVariable Integer idMedico, Model model, Locale locale) {
        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }
        
        Medico medico = medicoService.getMedicoPorId(idMedico);
        model.addAttribute("titulo", messageSource.getMessage("sidebar.expedientes", null, locale));
        model.addAttribute("medico", medico);
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("motivosCita", motivoCitaService.getMotivoCitas());
        return "medico/expedientes";
    }
    
    @PostMapping("/expedientes/buscar")
    public String buscarPacienteExpediente(
            @RequestParam(value = "texto") String texto, 
            Model model,
            Locale locale) {
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
        model.addAttribute("titulo", messageSource.getMessage("sidebar.expedientes", null, locale));
        
        return "medico/expedientes";
    }

    @GetMapping("/atender-cita/{idCita}")
    public String atenderCita(@PathVariable Integer idCita, Model model, Locale locale) {
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
        
        List<Prescripcion> prescripciones = prescripcionService.getPrescripcionesPorCita(idCita);
        
        model.addAttribute("titulo", messageSource.getMessage("accion.atender", null, locale));
        model.addAttribute("medico", medico);
        model.addAttribute("cita", citaActual);
        model.addAttribute("paciente", paciente);
        model.addAttribute("ultimaCita", ultimaCita);
        model.addAttribute("motivosCita", motivoCitaService.getMotivoCitas());
        model.addAttribute("prescripciones", prescripciones);
        model.addAttribute("nuevaPrescripcion", new Prescripcion());
        
        return "medico/atenderCita";
    }

    @PostMapping("/guardar-consulta")
    public String guardarConsulta(
            Cita cita, 
            Paciente paciente, 
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
            Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
            
            Optional<Cita> citaActualOpt = citaService.getCita(cita.getIdCita());
            
            if (citaActualOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("cita.error01", null, locale));
                return medico != null ? "redirect:/medico/inicio/" + medico.getIdMedico() : "redirect:/medico/inicio";
            }
            
            Cita citaActual = citaActualOpt.get();
            
            if (!EstadoCita.esValido(cita.getEstado())) {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error.estado.invalido", null, locale));
                return medico != null ? "redirect:/medico/inicio/" + medico.getIdMedico() : "redirect:/medico/inicio";
            }
            
            citaActual.setObservaciones(cita.getObservaciones());
            citaActual.setTratamiento(cita.getTratamiento());
            citaActual.setEstado(cita.getEstado());
            citaActual.setMotivoCita(cita.getMotivoCita());
                    
            citaService.save(citaActual);
            
            redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("medico.consulta.guardada", null, locale));
            return medico != null ? "redirect:/medico/inicio/" + medico.getIdMedico() : "redirect:/medico/inicio";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("error", null, locale) + ": " + e.getMessage());
            e.printStackTrace();
            return "redirect:/medico/inicio";
        }
    }
    
    @PostMapping("/agregar-prescripcion")
    public String agregarPrescripcion(
            @RequestParam Integer idCita,
            @RequestParam String medicamento,
            @RequestParam String dosis,
            @RequestParam Integer duracionDias,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            Optional<Cita> citaOpt = citaService.getCita(idCita);
            
            if (citaOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("cita.error01", null, locale));
                return "redirect:/medico/inicio";
            }
            
            Prescripcion prescripcion = new Prescripcion();
            prescripcion.setCita(citaOpt.get());
            prescripcion.setMedicamento(medicamento);
            prescripcion.setDosis(dosis);
            prescripcion.setDuracionDias(duracionDias);
            
            prescripcionService.save(prescripcion);
            
            redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("prescripcion.agregada", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("prescripcion.error.agregar", null, locale) + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        return "redirect:/medico/atender-cita/" + idCita;
    }
    
    @PostMapping("/eliminar-prescripcion/{idPrescripcion}")
    public String eliminarPrescripcion(
            @PathVariable Integer idPrescripcion,
            @RequestParam Integer idCita,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            Optional<Prescripcion> prescripcionOpt = prescripcionService.getPrescripcion(idPrescripcion);
            
            if (prescripcionOpt.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("prescripcion.no.encontrada", null, locale));
                return "redirect:/medico/atender-cita/" + idCita;
            }
            
            prescripcionService.delete(prescripcionOpt.get());
            redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("prescripcion.eliminada", null, locale));
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("prescripcion.error.eliminar", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/medico/atender-cita/" + idCita;
    }

    @PostMapping("/ver-expediente")
    public String verExpediente(
            @RequestParam Integer idPaciente, 
            Model model, 
            RedirectAttributes redirectAttributes,
            Locale locale) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (medico == null) {
            return "redirect:/";
        }
        
        Paciente pacienteTemp = new Paciente();
        pacienteTemp.setIdPaciente(idPaciente);
        Paciente paciente = pacienteService.getPaciente(pacienteTemp);

        if (paciente == null) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("paciente.no.encontrado", null, locale));
            return "redirect:/medico/expedientes/" + medico.getIdMedico();
        }

        
        Paciente pacienteCita = pacienteService.getPacientePorId(idPaciente);
        List<Cita> citas = citaService.getCitasPorPaciente(idPaciente);
        
        model.addAttribute("titulo", messageSource.getMessage("accion.verExpediene", null, locale));
        model.addAttribute("medico", medico);
        model.addAttribute("paciente", paciente);
         model.addAttribute("citas", citas);
        return "medico/modificarExpediente";
    }

    @PostMapping("/guardarExpediente")
    public String guardarExpediente(
            Paciente paciente, 
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
            Medico medico = medicoService.getMedicoPorIdUsuario(usuarioLogueado.getIdUsuario());
            
            Paciente pacienteActual = pacienteService.getPaciente(paciente);
                
                pacienteActual.setAntecedentesHeredoFamiliares(paciente.getAntecedentesHeredoFamiliares());
                pacienteActual.setAntecedentesPersonales(paciente.getAntecedentesPersonales());
                pacienteActual.setAntecedentesQuirurgicos(paciente.getAntecedentesQuirurgicos());
                pacienteActual.setAntecedentesGinecoObstetricos(paciente.getAntecedentesGinecoObstetricos());

                pacienteService.save(pacienteActual);
            redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("expediente.actualizado", null, locale));
            
            return medico != null ? "redirect:/medico/expedientes/" + medico.getIdMedico() : "redirect:/medico/expedientes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("expediente.error.actualizar", null, locale));
            return "redirect:/medico/expedientes";
        }
    }
    
    @PostMapping("/guardar/{idMedico}")
    public String guardarMedico(
            @PathVariable Integer idMedico, 
            Medico medico,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        if (!validarAcceso(idMedico)) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("e403.texto", null, locale));
            return "redirect:/";
        }
        
        try {
            Medico medicoExistente = medicoService.getMedicoPorId(idMedico);
            
            if (medicoExistente == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("medico.no.encontrado", null, locale));
                return "redirect:/";
            }

            Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
            
            if (correo != null && !correo.trim().isEmpty()) {
                if (!correo.equals(usuarioLogueado.getCorreo())) {
                    if (usuarioService.existeCorreo(correo)) {
                        redirectAttributes.addFlashAttribute("error", 
                            messageSource.getMessage("controller.usuarios.registro.correo.en.sistema", null, locale));
                        return "redirect:/medico/perfil/" + idMedico;
                    }
                }
            }
            
            medico.setIdMedico(idMedico);
            medico.setIdUsuario(medicoExistente.getIdUsuario());
            
            medicoService.save(medico);
            
            Usuario usuario = usuarioService.getUsuarioPorId(medicoExistente.getIdUsuario()).orElse(null);
            
            if (usuario != null) {
                usuario.setNombre(medico.getNombre());
                usuario.setApellido1(medico.getApellido1());
                usuario.setApellido2(medico.getApellido2());
                usuario.setCorreo(correo);
                usuario.setTelefono(telefono);
                usuarioService.save(usuario);
            }
            
            redirectAttributes.addFlashAttribute("mensaje", 
                messageSource.getMessage("perfil.actualizado.correctamente", null, locale));
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("error.actualizar.perfil", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/medico/perfil/" + idMedico;
    }

    @PostMapping("/desactivar/{idMedico}")
    public String desactivarPerfil(
            @PathVariable Integer idMedico,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (!validarAcceso(idMedico)) {
            return "redirect:/";
        }

        try {
            Medico medico = medicoService.getMedicoPorId(idMedico);

            if (medico == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("medico.no.encontrado", null, locale));
                return "redirect:/";
            }

            Usuario usuario = usuarioService.getUsuarioPorId(medico.getIdUsuario()).orElse(null);

            if (usuario != null) {
                usuario.setActivo(false);
                usuarioService.save(usuario);
            }

            SecurityContextHolder.clearContext();

            redirectAttributes.addFlashAttribute("mensaje",
                messageSource.getMessage("medico.cuenta.desactivada", null, locale));
            return "redirect:/login?cuentaDesactivada=true";

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("medico.error.desactivar", null, locale) + ": " + e.getMessage());
            return "redirect:/login";
        }
    }
}
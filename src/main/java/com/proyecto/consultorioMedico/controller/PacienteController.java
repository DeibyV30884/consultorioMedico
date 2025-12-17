package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Prescripcion;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.PrescripcionService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;
import org.springframework.security.core.context.SecurityContextHolder;

@Controller
@RequestMapping("/paciente")
public class PacienteController {
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private CitaService citaService;
    
    @Autowired
    private MedicoService medicoService;
    
    @Autowired
    private PrescripcionService prescripcionService;
    
    private boolean validarAcceso(Integer idPaciente) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Paciente pacienteLogueado = pacienteService.getPacientePorIdUsuario(usuarioLogueado.getIdUsuario());
        
        return pacienteLogueado != null && pacienteLogueado.getIdPaciente().equals(idPaciente);
    }
    
    @GetMapping("/inicio/{idPaciente}")
    public String inicioPaciente(@PathVariable Integer idPaciente, Model model, Locale locale) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        
        Cita ultimaCita = citaService.getUltimaCitaCompletada(idPaciente);
        List<Prescripcion> prescripciones = prescripcionService.getPrescripcionesPorPaciente(idPaciente);
        List<Cita> proximasCitas = citaService.getCitasPorPaciente(idPaciente);
        proximasCitas.removeIf(c -> 
            !EstadoCita.PENDIENTE.equals(c.getEstado()) && 
            !EstadoCita.CONFIRMADA.equals(c.getEstado())
        );
        
        model.addAttribute("titulo", messageSource.getMessage("sidebar.inicio", null, locale));
        model.addAttribute("paciente", paciente);
        model.addAttribute("ultimaCita", ultimaCita);
        model.addAttribute("prescripciones", prescripciones);
        model.addAttribute("proximasCitas", proximasCitas);
        
        return "paciente/inicio";
    }
    
    @GetMapping("/perfil/{idPaciente}")
    public String perfil(@PathVariable Integer idPaciente, Model model, Locale locale) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        if (paciente == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", messageSource.getMessage("paciente.perfil", null, locale));
        model.addAttribute("paciente", paciente);
        return "paciente/perfil";
    }
    
    @GetMapping("/tratamientos/{idPaciente}")
    public String tratamientos(@PathVariable Integer idPaciente, Model model, Locale locale) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        model.addAttribute("titulo", messageSource.getMessage("sidebar.tratamientos", null, locale));
        model.addAttribute("paciente", paciente);
        return "paciente/tratamientos";
    }
    
    @GetMapping("/citas/{idPaciente}")
    public String citas(@PathVariable Integer idPaciente, Model model, Locale locale) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        List<Cita> citas = citaService.getCitasPorPaciente(idPaciente);
        
        model.addAttribute("titulo", messageSource.getMessage("cita.mis.citas", null, locale));
        model.addAttribute("paciente", paciente);
        model.addAttribute("citas", citas);
        return "paciente/citas";
    }
    
    @GetMapping("/citas/{idPaciente}/nueva")
    public String nuevaCita(@PathVariable Integer idPaciente, Model model, Locale locale) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        
        model.addAttribute("titulo", messageSource.getMessage("cita.agendar.nueva", null, locale));
        model.addAttribute("paciente", paciente);
        model.addAttribute("medicos", medicoService.getMedicos());
        
        return "paciente/nuevaCita";
    }
    
    @PostMapping("/guardar/{idPaciente}")
    public String guardarPaciente(
            @PathVariable Integer idPaciente, 
            Paciente paciente, 
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        try {
            Paciente pacienteExistente = pacienteService.getPacientePorId(idPaciente);
            
            if (pacienteExistente == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("paciente.no.encontrado", null, locale));
                return "redirect:/";
            }
            
            paciente.setIdPaciente(idPaciente);
            paciente.setIdUsuario(pacienteExistente.getIdUsuario());
            paciente.setFechaCreacion(pacienteExistente.getFechaCreacion());
            paciente.setFechaModificacion(LocalDateTime.now());
            
            pacienteService.save(paciente);
            
            if (pacienteExistente.getIdUsuario() != null) {
                Usuario usuario = usuarioService.getUsuarioPorId(pacienteExistente.getIdUsuario())
                        .orElse(null);
                
                if (usuario != null) {
                    usuario.setNombre(paciente.getNombre());
                    usuario.setApellido1(paciente.getApellido1());
                    usuario.setApellido2(paciente.getApellido2());
                    usuario.setCorreo(paciente.getCorreoElectronico());
                    usuario.setTelefono(paciente.getTelefono());
                    
                    usuarioService.save(usuario);
                }
            }
            
            redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("perfil.actualizado.correctamente", null, locale));
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("error.actualizar.perfil", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/paciente/perfil/" + idPaciente;
    }
    
    @PostMapping("/citas/{idPaciente}/crear")
    public String crearCita(
            @PathVariable Integer idPaciente,
            @RequestParam("medico.idMedico") Integer idMedico,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam String tipoConsulta,
            @RequestParam(required = false) String  motivoConsulta,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            LocalTime horaCita = LocalTime.parse(hora);
            
            if (fechaCita.isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error.fecha.pasada", null, locale));
                return "redirect:/paciente/citas/" + idPaciente + "/nueva";
            }
            
            boolean hayConflicto = citaService.validarConflictoHorario(idMedico, fechaCita, horaCita);
            
            if (hayConflicto) {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error.conflicto", null, locale));
                return  "redirect:/paciente/citas/" + idPaciente + "/nueva";
            } 
            
            Cita cita = new Cita();
            cita.setPaciente(pacienteService.getPacientePorId(idPaciente));
            cita.setMedico(medicoService.getMedicoPorId(idMedico));
            cita.setFecha(fechaCita);
            cita.setHora(horaCita);
            cita.setTipoConsulta(tipoConsulta);
            cita.setEstado(EstadoCita.PENDIENTE);
            cita.setTratamiento(motivoConsulta);
            
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("cita.agendada.exitosamente", null, locale));
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("cita.error.crear", null, locale) + ": " + e.getMessage());
            return "redirect:/paciente/citas/" + idPaciente + "/nueva";
        }
        
        return "redirect:/paciente/citas/" + idPaciente;
    }
    
    @PostMapping("/citas/{idPaciente}/cancelar")
    public String cancelarCita(
            @PathVariable Integer idPaciente,
            @RequestParam Integer idCita,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        try  {
            Cita cita = citaService.getCitaPorId(idCita);
            
            if (cita == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("cita.error01", null, locale));
                return "redirect:/paciente/citas/" + idPaciente;
            }
            
            if (!cita.getPaciente().getIdPaciente().equals(idPaciente)) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("cita.error.permiso.cancelar", null, locale));
                return "redirect:/paciente/citas/" + idPaciente;
            }
            
            if (!EstadoCita.PENDIENTE.equals(cita.getEstado()) && 
                !EstadoCita.CONFIRMADA.equals(cita.getEstado())) {
                redirectAttributes.addFlashAttribute ("error", 
                    messageSource.getMessage("cita.error.cancelar.estado", null, locale));
                return "redirect:/paciente/citas/" + idPaciente;
            }
            
            cita.setEstado(EstadoCita.CANCELADA);
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("cita.cancelada.exitosamente", null, locale));
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("cita.error.cargar", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/paciente/citas/" + idPaciente;
    }
    
    @PostMapping("/desactivar/{idPaciente}")
    public String desactivarPerfil(
            @PathVariable Integer idPaciente,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }

        try {
            Paciente paciente = pacienteService.getPacientePorId(idPaciente);

            if (paciente == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("paciente.no.encontrado", null, locale));
                return "redirect:/";
            }

            Usuario usuario = usuarioService.getUsuarioPorId(paciente.getIdUsuario()).orElse(null);

            if (usuario != null) {
                usuario.setActivo(false);
                usuarioService.save(usuario);
            }

            SecurityContextHolder.clearContext();

            redirectAttributes.addFlashAttribute("mensaje",
                messageSource.getMessage("paciente.cuenta.desactivada", null, locale));
            return "redirect:/login?cuentaDesactivada=true";

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("paciente.error.desactivar", null, locale) + ": " + e.getMessage());
            return "redirect:/login";
        }
    }
}
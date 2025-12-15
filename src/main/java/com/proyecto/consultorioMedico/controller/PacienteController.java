package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
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
        List<Cita> citas = citaService.getCitasPorPaciente(idPaciente);
        
        model.addAttribute("titulo", "Mis Citas");
        model.addAttribute("paciente", paciente);
        model.addAttribute("citas", citas);
        return "paciente/citas";
    }
    
    @GetMapping("/citas/{idPaciente}/nueva")
    public String nuevaCita(@PathVariable Integer idPaciente, Model model) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        Paciente paciente = pacienteService.getPacientePorId(idPaciente);
        
        model.addAttribute("titulo", "Nueva Cita");
        model.addAttribute("paciente", paciente);
        model.addAttribute("medicos", medicoService.getMedicos());
        
        return "paciente/nuevaCita";
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
    
    @PostMapping("/citas/{idPaciente}/crear")
    public String crearCita(
            @PathVariable Integer idPaciente,
            @RequestParam("medico.idMedico") Integer idMedico,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam String tipoConsulta,
            @RequestParam(required = false) String  motivoConsulta,
            RedirectAttributes redirectAttributes) {
        
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        try {
            LocalDate fechaCita = LocalDate.parse(fecha);
            LocalTime horaCita = LocalTime.parse(hora);
            
            if (fechaCita.isBefore(LocalDate.now())) {
                redirectAttributes.addFlashAttribute ("error",
                     "No puede agendar citas en fechas pasadas.");
                return "redirect:/paciente/citas/" + idPaciente + "/nueva";
            }
            
            boolean hayConflicto = citaService.validarConflictoHorario(idMedico, fechaCita, horaCita);
            
            if (hayConflicto) {
                redirectAttributes.addFlashAttribute("error",
                    "El horario seleccionado ya no está disponible. Por favor, seleccione otra hora.");
                return  "redirect:/paciente/citas/" + idPaciente + "/nueva";
            } 
            Cita cita = new Cita();
            cita.setPaciente(pacienteService.getPacientePorId(idPaciente));
            cita.setMedico(medicoService.getMedicoPorId(idMedico));
            cita.setFecha(fechaCita);
            cita.setHora(horaCita);
            cita.setTipoConsulta(tipoConsulta);
            cita.setEstado("Pendiente");
            cita.setTratamiento(motivoConsulta);
            
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("todoOk","Cita agendada exitosamente. Estado: Pendiente de confirmacion.");
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al agendar la cita: " + e.getMessage());
            return "redirect:/paciente/citas/" + idPaciente + "/nueva";
        }
        
        return "redirect:/paciente/citas/" + idPaciente;
    }
    
    @PostMapping("/citas/{idPaciente}/cancelar")
    public String cancelarCita(
            @PathVariable Integer idPaciente,
            @RequestParam Integer idCita,
            RedirectAttributes redirectAttributes) {
        if (!validarAcceso(idPaciente)) {
            return "redirect:/";
        }
        
        try  {
            Cita cita = citaService.getCitaPorId(idCita);
            
            if (cita == null) {
                 redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
                return "redirect:/paciente/citas/" + idPaciente;
            }
            
            if (!cita.getPaciente().getIdPaciente().equals(idPaciente)) {
                redirectAttributes.addFlashAttribute("error", "No tiene permiso para cancelar esta cita");
                return "redirect:/paciente/citas/" + idPaciente;
            }
            
            if (!cita.getEstado().equals("Pendiente") && !cita.getEstado().equals("Confirmada")) {
                redirectAttributes.addFlashAttribute ("error", 
                    "Solo se pueden cancelar citas en estado Pendiente o Confirmada");
                return "redirect:/paciente/citas/" + idPaciente;
            }
            
            cita.setEstado("Cancelada");
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute ("todoOk", "Cita cancelada exitosamente");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al cancelar la cita: " + e.getMessage());
        }
        
        return "redirect:/paciente/citas/" + idPaciente;
    }
}
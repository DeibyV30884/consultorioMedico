package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.MotivoCitaService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import java.util.Locale;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Alejandro
 */
@Controller
@RequestMapping("/cita")
public class CitaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private MotivoCitaService motivoCitaService;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @ModelAttribute("usuario")
    public Usuario agregarUsuarioLogueado() {
        return usuarioService.getUsuarioLogueado();
    }

    @GetMapping("/listado")
    public String inicio(Model model) {
        var citas = citaService.getCitas();
        model.addAttribute("citas", citas);
        model.addAttribute("cita", new Cita());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        return "redirect:/secretaria/citas"; 
    }
    
    @GetMapping("/agregar")
    public String agregar(Model model, Locale locale) {
        model.addAttribute("titulo", messageSource.getMessage("cita.agregar", null, locale));
        model.addAttribute("cita", new Cita());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        return "secretaria/agrega";
    }
    
    @PostMapping("/crear")
    public String crear(
            @RequestParam("paciente.idPaciente") Integer idPaciente,
            @RequestParam("medico.idMedico") Integer idMedico,
            @RequestParam String fecha,
            @RequestParam String hora,
            @RequestParam String tipoConsulta,
            @RequestParam String estado,
            @RequestParam(required = false) String tratamiento,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        try  {
            LocalDate fechaCita = LocalDate.parse(fecha);
            LocalTime horaCita = LocalTime.parse(hora);
            
            if (!EstadoCita.esValido(estado)) {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error.estado.invalido", null, locale));
                return "redirect:/secretaria/citasRegistro";
            }
            
            boolean hayConflicto = citaService.validarConflictoHorario(idMedico, fechaCita, horaCita);
            
            if (hayConflicto) {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error.conflicto", null, locale));
                return "redirect:/secretaria/citasRegistro";
            }
            
            
            Cita cita = new Cita();
            cita.setPaciente (pacienteService.getPacientePorId(idPaciente));
            cita.setMedico(medicoService.getMedicoPorId(idMedico));
            cita.setFecha(fechaCita);
            cita.setHora(horaCita);
            cita.setTipoConsulta(tipoConsulta);
            cita.setEstado(estado);
            cita.setTratamiento(tratamiento);
            
            citaService.save(cita);
            
            redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.guardado", null, locale));
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("cita.error.crear", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/secretaria/citas";
        
    }
    
    @PostMapping("/modificar")
    public String modificar(
            @RequestParam("idCita") Integer idCita, Model model, RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            Cita cita = citaService.getCitaPorId(idCita);
            
            if (cita == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("cita.error01", null, locale));
                return "redirect:/secretaria/citas";
            }
            
            model.addAttribute("titulo", messageSource.getMessage("cita.editar", null, locale));
            model.addAttribute("cita", cita);
            model.addAttribute("medicos", medicoService.getMedicos());
            model.addAttribute("pacientes", pacienteService.getPacientes());
            
            return "secretaria/modifica";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("cita.error.cargar", null, locale) + ": " + e.getMessage());
            return "redirect:/secretaria/citas";
        }
    }
    
    @GetMapping("/buscarPaciente")
    @ResponseBody
    public List<Map<String, Object>> buscarPaciente(@RequestParam String termino) {
        List<Paciente> pacientes = pacienteService.buscarPorNombreOApellido(termino);
        List<Map<String, Object>> resultado = new ArrayList<>();
        
        for (int i = 0; i < pacientes.size(); i++) {
            Paciente paciente = pacientes.get(i);
            Map<String, Object> pacienteMap = new HashMap<>();
            Integer idPaciente = paciente.getIdPaciente();
            String nombre = paciente.getNombre();
            String apellido1 = paciente.getApellido1();
            String apellido2 = paciente.getApellido2();
            String nombreCompleto = nombre + " " + apellido1;
            
            if (apellido2 != null) {
                nombreCompleto = nombreCompleto + " " + apellido2;
            }
            
            pacienteMap.put("id", idPaciente);
            pacienteMap.put("nombre", nombreCompleto);
            resultado.add(pacienteMap);
        }
        
        return resultado;
    }
    
    @PostMapping("/guardar")
    public String guardar(Cita citaFormulario, RedirectAttributes redirectAttributes, Locale locale) {
        try {
            Cita citaReal = citaService.getCita(citaFormulario);
            
            if (citaReal == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("cita.error01", null, locale));
                return "redirect:/secretaria/citas";
            }
            
            if (!EstadoCita.esValido(citaFormulario.getEstado())) {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error.estado.invalido", null, locale));
                return "redirect:/secretaria/citas";
            }
            
            boolean cambioHorario = !citaReal.getFecha().equals(citaFormulario.getFecha()) ||
                                   !citaReal.getHora().equals(citaFormulario.getHora());
            
            if (cambioHorario) {
                boolean hayConflicto =  citaService.validarConflictoHorarioEditar(
                    citaReal.getMedico().getIdMedico(),
                    citaFormulario.getFecha(),
                    citaFormulario.getHora(),
                    citaReal.getIdCita()
                );
                
                if (hayConflicto) {
                    redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("cita.error.conflicto", null, locale));
                    return "redirect:/secretaria/citas";
                }
            }
            
            citaReal.setFecha(citaFormulario.getFecha());
            citaReal.setHora(citaFormulario.getHora());
            citaReal.setEstado(citaFormulario.getEstado());
            citaReal.setTratamiento(citaFormulario.getTratamiento());
            
            citaService.save(citaReal);
            
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.actualizado", null, locale));
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("cita.error.actualizar", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/secretaria/citas";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(
            @RequestParam("idCita") Integer idCita, 
            RedirectAttributes redirectAttributes,
            Locale locale) {
        try {
            Cita cita = citaService.getCitaPorId(idCita);
            
            if (cita == null) {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error01", null, locale));
            } else if (citaService.delete(cita)) {
                redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado", null, locale));
            } else {
                redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error03", null, locale));
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("cita.error.eliminar", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/secretaria/citas";
    }
}
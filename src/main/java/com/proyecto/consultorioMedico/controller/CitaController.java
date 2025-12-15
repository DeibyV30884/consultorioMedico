package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
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
    private CitaService citaService;//-> CRUD


    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private MedicoService medicoService;
    
    @Autowired
    private PacienteService pacienteService;
    
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
    public String agregar(Model model) {
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
            RedirectAttributes redirectAttributes) {
        
        try  {
            LocalDate fechaCita = LocalDate.parse(fecha);
            LocalTime horaCita = LocalTime.parse(hora);
            
            boolean hayConflicto = citaService.validarConflictoHorario (idMedico, fechaCita, horaCita);
            
            if (hayConflicto) {
                redirectAttributes.addFlashAttribute("error",
                    "El médico ya tiene una cita agendada en ese horario. Por favor, seleccione otra hora.");
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
                messageSource.getMessage("mensaje.guardado", null, Locale.getDefault()));
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al crear la cita: " + e.getMessage());
        }
        
        return "redirect:/secretaria/citas";
        
    }
    
    @PostMapping("/modificar")
    public String modificar(Cita cita, Model model) {
        cita = citaService.getCita(cita);
        model.addAttribute("cita", cita);
        return "secretaria/modifica";
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
    public String guardar(Cita citaFormulario, RedirectAttributes redirectAttributes) {
        try {
            Cita citaReal = citaService.getCita(citaFormulario);
            
            if (citaReal == null) {
                redirectAttributes.addFlashAttribute("error", "Cita no encontrada");
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
                         "El medico ya tiene una cita agendada en ese horario. Por favor, seleccione otra hora.");
                    return "redirect:/secretaria/citas";
                }
            }
            
            citaReal.setFecha(citaFormulario.getFecha());
            citaReal.setHora(citaFormulario.getHora());
            citaReal.setEstado(citaFormulario.getEstado());
            citaReal.setTratamiento(citaFormulario.getTratamiento());
            
            citaService.save(citaReal);
            
    redirectAttributes.addFlashAttribute("todoOk",
            messageSource.getMessage("mensaje.actualizado",
                    null,
                    Locale.getDefault()));
                
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al actualizar la cita: " + e.getMessage());
        }
        
        return "redirect:/secretaria/citas";
    }
    
    @PostMapping("/eliminar")
    public String eliminar(Cita cita, RedirectAttributes redirectAttributes) {
        cita = citaService.getCita(cita);
        if (cita == null) {  // La cita no existe...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error01",
                            null,
                            Locale.getDefault()));
        } else if (citaService.delete(cita)) {
            // Si se borró...
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.eliminado",
                            null,
                            Locale.getDefault()));
        } else {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error03",
                            null,
                            Locale.getDefault()));
        }
        return "redirect:/secretaria/citas";
    }
}
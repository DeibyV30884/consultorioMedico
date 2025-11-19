package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
import java.util.Locale;
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

    @GetMapping("/listado") 
    public String inicio(Model model) {
        var citas = citaService.getCitas();
        model.addAttribute("citas", citas);
        return "redirect:/secretaria/citas"; 
    }

    @PostMapping("/modificar")
    public String modificar(Cita cita, Model model) {
        cita = citaService.getCita(cita);
        model.addAttribute("cita", cita);
        return "secretaria/modifica";
    }

    @PostMapping("/guardar")
public String guardar(Cita citaFormulario, RedirectAttributes redirectAttributes) {

    // Cargar la cita real desde la BD
    Cita citaReal = citaService.getCita(citaFormulario);

    // Actualizar SOLO los campos que se pueden editar
    citaReal.setFechaHora(citaFormulario.getFechaHora());
    citaReal.setEstado(citaFormulario.getEstado());
    citaReal.setTratamiento(citaFormulario.getTratamiento());

    citaService.save(citaReal);

    redirectAttributes.addFlashAttribute("todoOk",
            messageSource.getMessage("mensaje.actualizado",
                    null,
                    Locale.getDefault()));

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
        } else if (false) { // Esto se actualiza proximas semanas...
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error02",
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

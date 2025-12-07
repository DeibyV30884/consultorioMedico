package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.TipoConsultaService;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
    private PacienteService pacienteService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private TipoConsultaService tipoConsultaService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/listado")
    public String inicio(Model model) {
        var citas = citaService.getCitas();
        model.addAttribute("citas", citas);
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("tiposConsulta", tipoConsultaService.getTipoConsultas());
        return "/secretaria/citas";
    }

    @GetMapping("/modificar/{idCita}")
    public String citaModificar(@PathVariable("idCita") Integer idCita, Model model, RedirectAttributes redirectAttributes) {
        Optional<Cita> citaOpt = citaService.getCita(idCita);

        if (citaOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("cita.error01", null, Locale.getDefault()));
            return "redirect:/secretaria/citas";
        }

        model.addAttribute("cita", citaOpt.get());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("tiposConsulta", tipoConsultaService.getTipoConsultas());

        return "/secretaria/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(Cita cita, RedirectAttributes redirectAttributes) {

        try {
            citaService.save(cita);
            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("mensaje.actualizado", null, Locale.getDefault()));
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/secretaria/pacientes";
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

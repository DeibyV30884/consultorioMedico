/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.service.CitaService;
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

//    @Autowired
//    private FirebaseStorageService firebaseStorageService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/listado") // https:localhost/cita/listado
    public String inicio(Model model) {
        var citas = citaService.getCitas(false);
        model.addAttribute("citas", citas);
        model.addAttribute("totalCitas", citas.size());
        return "/cita/listado"; //las vistas que yo voy a crear en el html
    }

    @PostMapping("/modificar")// https:localhost/cita/modificar
    public String modificar(Cita cita, Model model) {
        cita = citaService.getCita(cita);
        model.addAttribute("cita", cita);
        return "/cita/modifica";
    }

    @PostMapping("/guardar")
    public String guardar(Cita cita,
            @RequestParam MultipartFile imagenFile,
            RedirectAttributes redirectAttributes) {
//        if (!imagenFile.isEmpty()) { // Si no está vacío... pasaron una imagen...
//            citaService.save(cita);
//            String rutaImagen = firebaseStorageService
//                    .cargaImagen(
//                            imagenFile,
//                            "cita",
//                            cita.getIdCita());
//            cita.setRutaImagen(rutaImagen);
//        }
        citaService.save(cita);
        redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.actualizado",
                        null,
                        Locale.getDefault()));
        return "redirect:/cita/listado";
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
        return "redirect:/cita/listado";
    }
}

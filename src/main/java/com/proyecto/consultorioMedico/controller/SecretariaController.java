package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.EstadoCita;
import com.proyecto.consultorioMedico.service.CitaService;
import java.util.List;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Secretaria;
import com.proyecto.consultorioMedico.service.MedicoService;
import org.springframework.beans.factory.annotation.Autowired;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.SecretariaService;
import java.time.LocalDateTime;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;

@Controller
@RequestMapping("/secretaria")
public class SecretariaController {

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private SecretariaService secretariaService;
    
    @Autowired
    private MessageSource messageSource;
    
    @ModelAttribute("usuario")
    public Usuario agregarUsuarioLogueado() {
        return usuarioService.getUsuarioLogueado();
    }
    
    private boolean validarAccesoSecretaria(Integer idSecretaria) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Secretaria secretariaLogueada = secretariaService.getSecretariaPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        return secretariaLogueada != null && secretariaLogueada.getIdSecretaria().equals(idSecretaria);
    }
    
    @GetMapping("/inicio")
    public String inicio(Model model, Locale locale) {
        model.addAttribute("titulo", messageSource.getMessage("sidebar.inicio", null, locale));
        List<Cita> citas = citaService.buscarCitasHoy();

        int total = citas.size();
        int completas = 0;
        int pendientes = 0;

        for (Cita c : citas) {
            if (EstadoCita.COMPLETADA.equals(c.getEstado())) {
                completas++;
            }
            if (EstadoCita.PENDIENTE.equals(c.getEstado())) {
                pendientes++;
            }
        }

        model.addAttribute("total", total);
        model.addAttribute("citasproximas", citas);
        model.addAttribute("completas", completas);
        model.addAttribute("pendientes", pendientes);

        return "secretaria/inicio";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Locale locale) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Secretaria secretaria = secretariaService.getSecretariaPorIdUsuario(usuarioLogueado.getIdUsuario());
        
        if (secretaria == null) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", messageSource.getMessage("secretaria.perfil", null, locale));
        model.addAttribute("secretaria", secretaria);
        model.addAttribute("usuario", usuarioLogueado);
        return "secretaria/perfil";
    }

    
    @GetMapping("/perfil/{id}")
    public String perfilConId(@PathVariable("id") Integer id, Model model, Locale locale) {
        Usuario usuario = usuarioService.getUsuarioPorId(id).orElse(null);
        if (usuario == null) {
            return "redirect:/";
        }
        
        Secretaria secretaria = secretariaService.getSecretariaPorIdUsuario(id);
        if (secretaria == null) {
            return "redirect:/";
        }
        
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        if (!usuarioLogueado.getIdUsuario().equals(id)) {
            return "redirect:/";
        }
        
        model.addAttribute("titulo", messageSource.getMessage("secretaria.perfil", null, locale));
        model.addAttribute("secretaria", secretaria);
        model.addAttribute("usuario", usuario);
        return "secretaria/perfil";
    }
    
    @GetMapping("/citas")
    public String citas(Model model, Locale locale) {
        List<Cita> lista = citaService.getCitas();
        model.addAttribute("citas", lista);
        model.addAttribute("cita", new Cita());
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("totalCitas", lista.size());
        model.addAttribute("titulo", messageSource.getMessage("sidebar.citas", null, locale));
        return "secretaria/citas";
    }
    
    @GetMapping("/pacientes")
    public String pacientes(Model model, Locale locale) {
        var pacientes = pacienteService.getPacientes();
        model.addAttribute("pacientes", pacientes);
        model.addAttribute("paciente", new Paciente());
        model.addAttribute("titulo", messageSource.getMessage("sidebar.pacientes", null, locale));
        model.addAttribute("medicos", medicoService.getMedicos());
        return "secretaria/pacientes";
    }
    
    @GetMapping("/citasRegistro")
    public String citasRegistro(Model model, Locale locale) {
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("titulo", messageSource.getMessage("cita.registrar.nueva", null, locale));
        return "secretaria/citasRegistro";
    }
    
    @PostMapping("/citasRegistro/buscar")
    public String buscarPacienteRegistro(@RequestParam(value = "texto") String texto, 
            Model model, Locale locale) {
        var pacientesEncontrados = pacienteService.buscarPorNombreOApellido(texto);
        
        model.addAttribute("pacientesEncontrados", pacientesEncontrados);
        model.addAttribute("texto", texto);
        model.addAttribute("medicos", medicoService.getMedicos());
        model.addAttribute("pacientes", pacienteService.getPacientes());
        model.addAttribute("titulo", messageSource.getMessage("cita.registrar.nueva", null, locale));
        
        return "secretaria/citasRegistro";
    }
    
    @PostMapping("/guardar/{idSecretaria}")
    public String guardarSecretaria(
            @PathVariable Integer idSecretaria, 
            Secretaria secretaria,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        if (!validarAccesoSecretaria(idSecretaria)) {
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("secretaria.error.permiso", null, locale));
            return "redirect:/";
        }
             
        try {
            Secretaria secretariaExistente = secretariaService.getSecretariaPorId(idSecretaria);
            
            if (secretariaExistente == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("secretaria.no.encontrada", null, locale));
                return "redirect:/";
            }
            
            Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
            
            if (correo != null && !correo.trim().isEmpty()) {
                if (!correo.equals(usuarioLogueado.getCorreo())) {
                    if (usuarioService.existeCorreo(correo)) {
                        redirectAttributes.addFlashAttribute("error", 
                            messageSource.getMessage("secretaria.error.correo.uso", null, locale));
                        return "redirect:/secretaria/perfil/" + usuarioLogueado.getIdUsuario();
                    }
                }
            }
            
            secretaria.setIdSecretaria(idSecretaria);
            secretaria.setIdUsuario(secretariaExistente.getIdUsuario());
            secretaria.setFechaCreacion(secretariaExistente.getFechaCreacion());
            secretaria.setFechaModificacion(LocalDateTime.now());
            
            secretariaService.save(secretaria);
            Usuario usuario = usuarioService.getUsuarioPorId(secretariaExistente.getIdUsuario()).orElse(null);
            
            if (usuario != null) {
                usuario.setNombre(secretaria.getNombre());
                usuario.setApellido1(secretaria.getApellido1());
                usuario.setApellido2(secretaria.getApellido2());
                usuario.setCorreo(correo);
                usuario.setTelefono(telefono);
                usuarioService.save(usuario);
            }
            
            redirectAttributes.addFlashAttribute("todoOk", 
                messageSource.getMessage("perfil.actualizado.correctamente", null, locale));
            
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", 
                messageSource.getMessage("error.actualizar.perfil", null, locale) + ": " + e.getMessage());
        }
        
        return "redirect:/secretaria/perfil/" + usuarioService.getUsuarioLogueado().getIdUsuario();
    }

    @PostMapping("/desactivar/{idSecretaria}")
    public String desactivarPerfil(
            @PathVariable Integer idSecretaria,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            Secretaria secretaria = secretariaService.getSecretariaPorId(idSecretaria);

            if (secretaria == null) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("secretaria.no.encontrada", null, locale));
                return "redirect:/";
            }
            
            if (!validarAccesoSecretaria(idSecretaria)) {
                redirectAttributes.addFlashAttribute("error", 
                    messageSource.getMessage("secretaria.error.permiso", null, locale));
                return "redirect:/";
            }

            Usuario usuario = usuarioService.getUsuarioPorId(secretaria.getIdUsuario()).orElse(null);

            if (usuario != null) {
                usuario.setActivo(false);
                usuarioService.save(usuario);
            }

            SecurityContextHolder.clearContext();

            redirectAttributes.addFlashAttribute("mensaje",
                messageSource.getMessage("secretaria.cuenta.desactivada", null, locale));
            return "redirect:/login?cuentaDesactivada=true";

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("error",
                messageSource.getMessage("secretaria.error.desactivar", null, locale) + ": " + e.getMessage());
            return "redirect:/login";
        }
    }
}
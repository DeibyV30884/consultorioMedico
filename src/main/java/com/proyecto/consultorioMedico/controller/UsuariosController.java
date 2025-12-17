package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Rol;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.RegistroService;
import com.proyecto.consultorioMedico.service.RolService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Locale;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/registro")
public class UsuariosController {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RolService rolService;
    
    @Autowired
    private PacienteService pacienteService; 

    @Autowired
    private RegistroService registroService;

    @Autowired
    private MessageSource messageSource;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @GetMapping("/nuevo")
    public String mostrarFormularioRegistro(Model model, Locale locale) {
        model.addAttribute("titulo", messageSource.getMessage("registro.titulo", null, locale));
        return "registro/nuevo";
    }

    @PostMapping("/crearUsuario")
    public String crearUsuario(
            Usuario usuario, 
            @RequestParam("fechaNacimiento") String fechaNacimiento,//se esta agregando por aparte porque la fecha de nacimiento de de paciente y no de usuario
            Model model, Locale locale) throws MessagingException {
        
        if (usuarioService.existeUsername(usuario.getUsername())) {
            model.addAttribute("error", 
                messageSource.getMessage("controller.usuarios.registro.usuario.en.sistema", null, locale));
            return "registro/nuevo";
        }
        
        if (usuarioService.existeCorreo(usuario.getCorreo())) {
            model.addAttribute("error", 
                messageSource.getMessage("controller.usuarios.registro.correo.en.sistema", null, locale));
            return "registro/nuevo";
        }
    
        boolean exito = registroService.crearUsuario(usuario, locale);
        
        if (exito) {
            return "redirect:/registro/salida?tipo=registro&correo=" + usuario.getCorreo();
        } else {
            return "redirect:/registro/salida?tipo=errorRegistro&username=" + usuario.getUsername() + "&correo=" + usuario.getCorreo();
        }
    }
    
    @GetMapping("/activacion/{usuario}/{id}")
    public String mostrarActivacion(
            Model model, 
            Locale locale,
            @PathVariable(value = "usuario") String username, 
            @PathVariable(value = "id") String clave) {
        
        model = registroService.activar(model, username, clave);
        
        if (model.containsAttribute("usuario") && model.getAttribute("usuario") != null) {
            model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, locale));
            return "registro/activa";
        } else {
            model.addAttribute("titulo", messageSource.getMessage("registro.activar.error", null, locale));
            model.addAttribute("mensaje", messageSource.getMessage("registro.activar.error", null, locale));
            return "registro/salida";
        }
    }
    
    @PostMapping("/activar")
public String activar(
        @RequestParam("idUsuario") Integer idUsuario,
        @RequestParam("password") String password,
        @RequestParam(value = "fechaNacimiento", required = false) String fechaNacimiento,
        RedirectAttributes redirectAttributes,
        Locale locale) {
    
    try {
        Usuario usuario = usuarioService.getUsuarioPorId(idUsuario)
                .orElseThrow(() -> new IllegalArgumentException(
                    messageSource.getMessage("usuario.no.encontrado", null, locale)));
        
        usuario.setPassword(passwordEncoder.encode(password));
        usuario.setActivo(true);
        usuarioService.save(usuario);
        
        boolean tieneRoles = !usuario.getRoles().isEmpty();
        
        if (!tieneRoles) {
            Rol rolCliente = rolService.getRolByNombre("CLIENTE"); 
            if (rolCliente != null) {
                rolService.asignarRolAUsuario(usuario, rolCliente);
            }
            
            Paciente paciente = new Paciente(); 
            paciente.setIdUsuario(usuario.getIdUsuario());
            paciente.setNombre(usuario.getNombre());
            paciente.setApellido1(usuario.getApellido1());
            paciente.setApellido2(usuario.getApellido2());
            paciente.setCorreoElectronico(usuario.getCorreo());
            paciente.setTelefono(usuario.getTelefono());
            
            if (fechaNacimiento != null && !fechaNacimiento.isEmpty()) {
                paciente.setFechaNacimiento(LocalDate.parse(fechaNacimiento));
            }
            
            pacienteService.save(paciente);
        }
        
        redirectAttributes.addFlashAttribute("mensaje", 
            messageSource.getMessage("login.mensaje.registro.completo", null, locale));
        return "redirect:/login?registroExitoso=true";
        
    } catch (Exception e) {
        redirectAttributes.addFlashAttribute("error", 
            messageSource.getMessage("usuario.error.activar", null, locale));
        return "redirect:/registro/salida?tipo=errorActivacion";
    }
}
    
    @GetMapping("/recordar")
    public String mostrarFormularioRecordar(Model model, Locale locale) {
        model.addAttribute("titulo", messageSource.getMessage("registro.recordar.us", null, locale));
        return "registro/recordar";
    }
    
    @PostMapping("/recordarUsuario")
    public String procesarRecordar(
            @RequestParam("correo") String correo,
            Locale locale) throws MessagingException {
        
        boolean exito = registroService.recordarUsuario(correo, locale);
        
        if (exito) {
            return "redirect:/registro/salida?tipo=exito&correo=" + correo;
        } else {
            return "redirect:/registro/salida?tipo=error";
        }
    }
    
    @GetMapping("/salida")
    public String mostrarSalida(
            Model model, 
            Locale locale,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String username) {
        
        if ("exito".equals(tipo) && correo != null) {
            model.addAttribute("titulo", 
                messageSource.getMessage("registro.recordar.titulo.exito", null, locale));
            String mensaje = String.format(
                messageSource.getMessage("registro.mensaje.recordar.ok", null, locale), 
                correo);
            model.addAttribute("mensaje", mensaje);
        } else if ("error".equals(tipo)) {
            model.addAttribute("titulo", 
                messageSource.getMessage("registro.recordar.titulo.exito", null, locale));
            model.addAttribute("mensaje", 
                messageSource.getMessage("registro.mensaje.correo.no.encontrado", null, locale));
        } else if ("registro".equals(tipo) && correo != null) {
            model.addAttribute("titulo", 
                messageSource.getMessage("registro.activar", null, locale));
            String mensaje = String.format(
                messageSource.getMessage("registro.mensaje.activacion.ok", null, locale), 
                correo);
            model.addAttribute("mensaje", mensaje);
        } else if ("errorRegistro".equals(tipo) && username != null && correo != null) {
            model.addAttribute("titulo", 
                messageSource.getMessage("registro.activar", null, locale));
            String mensaje = String.format(
                messageSource.getMessage("registro.mensaje.usuario.o.correo", null, locale), 
                username, correo);
            model.addAttribute("mensaje", mensaje);
        } else if ("errorActivacion".equals(tipo)) {
            model.addAttribute("titulo", 
                messageSource.getMessage("registro.activar.error", null, locale));
            model.addAttribute("mensaje", 
                messageSource.getMessage("usuario.error.activar", null, locale));
        } else {
            model.addAttribute("titulo", 
                messageSource.getMessage("registro.salida.titulo.default", null, locale));
            model.addAttribute("mensaje", 
                messageSource.getMessage("registro.salida.mensaje.default", null, locale));
        }
        
        return "registro/salida";
    }
}
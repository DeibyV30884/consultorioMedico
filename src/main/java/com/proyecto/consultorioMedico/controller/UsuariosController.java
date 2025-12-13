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
    public String mostrarFormularioRegistro(Model model) {
        return "registro/nuevo"; }

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
        
        model.addAttribute("fechaNacimiento", fechaNacimiento);
        
        model = registroService.crearUsuario(model, usuario);
        return "registro/salida";
    }
    
    @GetMapping("/activacion/{usuario}/{id}")
    public String mostrarActivacion(
            Model model, 
            @PathVariable(value = "usuario") String username, 
            @PathVariable(value = "id") String clave) {
        
        model = registroService.activar(model, username, clave);
        
    if (model.containsAttribute("usuario") && model.getAttribute("usuario") != null) {
            return "registro/activa";
        } else {
            return "registro/salida";
        }
    }
    
    @PostMapping("/activar")
    public String activar(
            @RequestParam("idUsuario") Integer idUsuario,
            @RequestParam("password") String password,
            @RequestParam(value = "fechaNacimiento", required = false) String fechaNacimiento,
            Locale locale) {
        
        try {
            Usuario usuario = usuarioService.getUsuarioPorId(idUsuario)
                    .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setActivo(true);
            usuarioService.save(usuario);
            
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
            
            return "redirect:/login?registroExitoso=true";
            
        } catch (Exception e) {
            return "registro/salida";
        }
    }
    @GetMapping("/recordar")
    public String mostrarFormularioRecordar (Model model){
        return "registro/recordar";
    }
    
    @PostMapping("/recordarUsuario")
    public String procesarRecordar(
            Model model,  
            @RequestParam("correo") String correo) throws MessagingException {
        model =  registroService.recordarUsuario(model, correo);
        return  "registro/salida";
    }
}
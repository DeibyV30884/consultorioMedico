package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Rol;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.RolService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.LocalDate;
import java.util.Locale;

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
    private MessageSource messageSource;
    
    @GetMapping
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("usuario", new Usuario() );
        return "registro";
    }
    
    @PostMapping("/guardar")
    public String guardar(
            Usuario usuario,
            @RequestParam("fechaNacimiento") String fechaNacimiento,//se esta agregando por aparte porque la fecha de nacimiento de de paciente y no de usuario
            @RequestParam("confirmarPassword") String confirmarPassword,// y esto solo se va a usar para validar la contraseña pero no guarda nada a la base de datos
            Model model,
            RedirectAttributes redirectAttributes,
            Locale locale) {
        
        if (!usuario.getPassword().equals(confirmarPassword)) {
            model.addAttribute("error", 
                messageSource.getMessage("controller.usuarios.registro.contraseña.error", 
                    null, locale));
            model.addAttribute("usuario", usuario);
            return "registro";
        }
        
        if (usuarioService.existeUsername(usuario.getUsername())) {
            model.addAttribute("error", 
                messageSource.getMessage("controller.usuarios.registro.usuario.en.sistema", 
                    null, locale));
            model.addAttribute("usuario", usuario);
            return "registro";
        }
        
        if (usuarioService.existeCorreo(usuario.getCorreo())) {
            model.addAttribute("error", 
                messageSource.getMessage("controller.usuarios.registro.correo.en.sistema", 
                    null, locale));
            model.addAttribute("usuario", usuario);
            return "registro";
        }
        
        try {

            usuario.setPassword("{noop}" + usuario.getPassword()); // de momento se guarda la contraseña así porque aun no se encripta la clave, sino springboot no nos deja guardar el usuario
            usuario.setActivo(true);
            
            usuarioService.save(usuario);
            
            Rol rolCliente = rolService.getRolByNombre("CLIENTE"); 
            if (rolCliente == null) {
                model.addAttribute("error", 
                    messageSource.getMessage("controller.usuarios.registro.rol.no.existe", 
                        null, locale));
                return "registro";
            }
            
            rolService.asignarRolAUsuario(usuario, rolCliente);
            
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
            
            redirectAttributes.addFlashAttribute("todoOk",
                messageSource.getMessage("mensaje.registro.funciono", 
                    null, locale));
            
            return "redirect:/login";
            
        } catch (Exception e) {
            model.addAttribute("error", 
                messageSource.getMessage("controller.usuarios.registro.registrar.error",  null, locale) + e.getMessage());
            model.addAttribute("usuario", usuario);
            return "registro";
        }
    }
}
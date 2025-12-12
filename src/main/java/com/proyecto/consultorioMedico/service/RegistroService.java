package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Usuario;
import jakarta.mail.MessagingException;
import java.util.Locale;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class RegistroService {
    
    @Autowired
    private CorreoService correoService;
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private MessageSource messageSource;
    
    @Value("${servidor.http}")
    private String servidor;

    public Model activar(Model model, String username, String clave) {
        Optional<Usuario> usuario = usuarioService.getUsuarioPorUsernameYPassword(username, clave);
        if (usuario.isPresent()) {
            model.addAttribute("usuario", usuario.get());
            
        } else {
            model.addAttribute("titulo", 
                messageSource.getMessage("registro.activar", null, Locale.getDefault()));
            model.addAttribute ("mensaje", 
                messageSource.getMessage("registro.activar.error", null, Locale.getDefault()));
        }
        return model;
    }
    
    /**
     * se crea el usuario inactivo y envia se envia el correo de con el enlace de activacion
     */
    public Model crearUsuario(Model model, Usuario usuario) throws MessagingException {
        String mensaje;
        try {
            
            String clave = demeClave();
            usuario.setPassword(clave);
            usuario.setActivo(false);
            usuarioService.save(usuario);
            enviaCorreoActivar(usuario, clave);
            mensaje = String.format(
                messageSource.getMessage("registro.mensaje.activacion.ok", null, Locale.getDefault()), usuario.getCorreo());
        } catch (MessagingException | NoSuchMessageException e) {
            mensaje = String.format(
                messageSource.getMessage("registro.mensaje.usuario.o.correo", null, Locale.getDefault()), usuario.getUsername(), usuario.getCorreo());
        }
        model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, Locale.getDefault()));
        model.addAttribute("mensaje", mensaje);
        return model;
    }
    
    public Model recordarUsuario(Model model, String correo) throws MessagingException {
    String mensaje;
    Optional<Usuario> usuarioOpt = usuarioService.getUsuarioPorCorreo(correo);
    
    if (usuarioOpt.isPresent()) {
        Usuario usuario = usuarioOpt.get();
        String clave = demeClave();
        usuario.setPassword(clave);
        usuario.setActivo(false);
        usuarioService.save(usuario);
        enviaCorreoRecordar(usuario, clave);
        mensaje = String.format(messageSource.getMessage("registro.mensaje.recordar.ok", null, Locale.getDefault()), usuario.getCorreo());
    } else {
        mensaje = messageSource.getMessage ("registro.mensaje.correo.no.encontrado", null, Locale.getDefault());
    }
    model.addAttribute("titulo", messageSource.getMessage("registro.activar", null, Locale.getDefault()));
    model.addAttribute("mensaje", mensaje);
    return model;
}
    
    private String demeClave() {
        String tira =  "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        String clave = "";
        for (int i = 0; i < 40; i++) {
            clave += tira.charAt((int) (Math.random() * tira.length()));
        }
        return clave;
    }
    
    private void enviaCorreoActivar(Usuario usuario, String clave) throws MessagingException {
        String mensaje = messageSource.getMessage("registro.correo.activar", null, Locale.getDefault());
        mensaje = String.format(mensaje, usuario.getNombre(), usuario.getApellido1() + (usuario.getApellido2() != null ? " " + usuario.getApellido2():  ""),servidor,  usuario.getUsername(), clave);
        String asunto = messageSource.getMessage("registro.mensaje.activacion", null, Locale.getDefault());
        correoService.enviarCorreoHtml(usuario.getCorreo(), asunto, mensaje);
    }
    
    private void enviaCorreoRecordar(Usuario usuario, String clave) throws MessagingException {
        String mensaje = messageSource.getMessage("registro.correo.recordar", null, Locale.getDefault());
        mensaje = String.format(mensaje, usuario.getNombre(), usuario.getApellido1() + (usuario.getApellido2() != null ? " " + usuario.getApellido2() : ""),servidor, usuario.getUsername(),  clave);
        String asunto = messageSource.getMessage("registro.mensaje.recordar", null, Locale.getDefault());
        correoService.enviarCorreoHtml(usuario.getCorreo(), asunto, mensaje);
    }
}
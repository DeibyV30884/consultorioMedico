package com.proyecto.consultorioMedico.controller;

import com.proyecto.consultorioMedico.domain.Admin;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.AdminService;
import com.proyecto.consultorioMedico.service.AdminUsuariosService;
import com.proyecto.consultorioMedico.service.AdminUsuariosService.UsuarioConRol;
import com.proyecto.consultorioMedico.service.UsuarioService;
import com.proyecto.consultorioMedico.service.CitaService;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.Medico;
import com.proyecto.consultorioMedico.domain.Paciente;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.Locale;
import java.util.Map;

@Controller

@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private AdminUsuariosService adminUsuariosService;

    @Autowired
    private CitaService citaService;

    @Autowired
    private MedicoService medicoService;

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private MessageSource messageSource;

    @ModelAttribute("usuario")
    public Usuario agregarUsuarioLogueado() {
        return usuarioService.getUsuarioLogueado();
    }

    private boolean validarAcceso(Integer idAdministrador) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Admin adminLogueado = adminService.getAdministradorPorIdUsuario(usuarioLogueado.getIdUsuario());

        return adminLogueado != null && adminLogueado.getIdAdministrador().equals(idAdministrador);
    }

    @GetMapping("/reportes")
    public String reportes(Model model, Locale locale) {
        model.addAttribute("titulo", messageSource.getMessage("sidebar.reportes", null, locale));

        Map<String, Long> resumen = usuarioService.obtenerResumenUsuarios();
        
        model.addAttribute("totalAdministradores", resumen.get("totalAdministradores"));
        model.addAttribute("totalMedicos", resumen.get("totalMedicos"));
        model.addAttribute("totalSecretarias", resumen.get("totalSecretarias"));
        model.addAttribute("totalPacientes", resumen.get("totalPacientes"));
        

        return "admin/reportes";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model, Locale locale) {
        List<UsuarioConRol> usuarios = adminUsuariosService.getAllUsuariosConRol();

        model.addAttribute("titulo", messageSource.getMessage("sidebar.usuarios", null, locale));
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("totalUsuarios", usuarios.size());
        return "admin/usuarios";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevoUsuario(Model model, Locale locale) {
        model.addAttribute("titulo", messageSource.getMessage("admin.usuarios.nuevo", null, locale));
        return "admin/nuevoUsuario";
    }

    @PostMapping("/usuarios/crear")
    public String crearUsuario(
            @RequestParam String nombre,
            @RequestParam String apellido1,
            @RequestParam String apellido2,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            @RequestParam String rol,
            @RequestParam(required = false) String username, // Nuevo parámetro
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            if (usuarioService.existeCorreo(correo)) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("controller.usuarios.registro.correo.en.sistema", null, locale));
                return "redirect:/admin/usuarios/nuevo";
            }

            if (username != null && !username.trim().isEmpty() && usuarioService.existeUsername(username.trim())) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("controller.usuarios.registro.usuario.en.sistema", null, locale));
                return "redirect:/admin/usuarios/nuevo";
            }

            adminUsuariosService.crearUsuario(nombre, apellido1, apellido2, correo, telefono, rol, username);

            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("admin.usuario.creado", null, locale));

        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("admin.usuario.error.crear", null, locale) + ": " + e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/actualizar/{idUsuario}")
    public String actualizarUsuario(
            @PathVariable Integer idUsuario,
            @RequestParam String nombre,
            @RequestParam String apellido1,
            @RequestParam String apellido2,
            @RequestParam String correo,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            Usuario usuarioExistente = usuarioService.getUsuarioPorId(idUsuario).orElse(null);

            if (usuarioExistente == null) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("usuario.no.encontrado", null, locale));
                return "redirect:/admin/usuarios";
            }

            if (!correo.equals(usuarioExistente.getCorreo()) && usuarioService.existeCorreo(correo)) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("controller.usuarios.registro.correo.en.sistema", null, locale));
                return "redirect:/admin/usuarios/editar/" + idUsuario;
            }

            adminUsuariosService.actualizarUsuario(idUsuario, nombre, apellido1, apellido2, correo, telefono);

            redirectAttributes.addFlashAttribute("todoOk",
                    messageSource.getMessage("admin.usuario.actualizado", null, locale));

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("admin.usuario.error.actualizar", null, locale) + ": " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/eliminar")
    public String eliminarUsuario(
            @RequestParam Integer idUsuario,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            Usuario usuario = usuarioService.getUsuarioPorId(idUsuario).orElse(null);

            if (usuario == null) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("usuario.no.encontrado", null, locale));
                return "redirect:/admin/usuarios";
            }

            List<String> mensajesError = new ArrayList<>();

            Medico medico = medicoService.getMedicoPorIdUsuario(idUsuario);
            if (medico != null) {
                List<Cita> citas = citaService.getCitasPorMedico(medico.getIdMedico());
                if (citas != null && !citas.isEmpty()) {
                    mensajesError.add(messageSource.getMessage("admin.medico.tiene.citas", null, locale));
                }
            }

            Paciente paciente = pacienteService.getPacientePorIdUsuario(idUsuario);
            if (paciente != null) {
                List<Cita> citas = citaService.getCitasPorPaciente(paciente.getIdPaciente());
                if (citas != null && !citas.isEmpty()) {
                    mensajesError.add(messageSource.getMessage("admin.paciente.tiene.citas", null, locale));
                }
            }

            if (!mensajesError.isEmpty()) {
                String mensaje = messageSource.getMessage("admin.usuario.no.eliminar", null, locale) + ": "
                        + String.join(", ", mensajesError);
                redirectAttributes.addFlashAttribute("error", mensaje);
                return "redirect:/admin/usuarios";
            }

            if (adminUsuariosService.eliminarUsuario(idUsuario)) {
                redirectAttributes.addFlashAttribute("todoOk",
                        messageSource.getMessage("admin.usuario.eliminado", null, locale));
            } else {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("admin.usuario.error.eliminar", null, locale));
            }

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("admin.usuario.error.eliminar", null, locale) + ": " + e.getMessage());
        }

        return "redirect:/admin/usuarios";
    }

    @GetMapping("/perfil")
    public String perfil(Model model, Locale locale) {
        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        Admin administrador = adminService.getAdministradorPorIdUsuario(usuarioLogueado.getIdUsuario());

        if (administrador == null) {
            return "redirect:/";
        }

        model.addAttribute("titulo", messageSource.getMessage("sidebar.perfil", null, locale));
        model.addAttribute("administrador", administrador);
        model.addAttribute("usuario", usuarioLogueado);
        return "admin/perfil";
    }

    @GetMapping("/perfil/{id}")
    public String perfilConId(@PathVariable("id") Integer id, Model model, Locale locale) {
        Usuario usuario = usuarioService.getUsuarioPorId(id).orElse(null);
        if (usuario == null) {
            return "redirect:/";
        }

        Admin administrador = adminService.getAdministradorPorIdUsuario(id);
        if (administrador == null) {
            return "redirect:/";
        }

        Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();
        if (!usuarioLogueado.getIdUsuario().equals(id)) {
            return "redirect:/";
        }

        model.addAttribute("titulo", messageSource.getMessage("sidebar.perfil", null, locale));
        model.addAttribute("administrador", administrador);
        model.addAttribute("usuario", usuario);
        return "admin/perfil";
    }

    @PostMapping("/guardar/{idAdministrador}")
    public String guardarAdministrador(
            @PathVariable Integer idAdministrador,
            Admin administrador,
            @RequestParam(required = false) String correo,
            @RequestParam(required = false) String telefono,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        if (!validarAcceso(idAdministrador)) {
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("e403.texto", null, locale));
            return "redirect:/";
        }

        try {
            Admin administradorExistente = adminService.getAdministradorPorId(idAdministrador);

            if (administradorExistente == null) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("admin.no.encontrado", null, locale));
                return "redirect:/";
            }

            Usuario usuarioLogueado = usuarioService.getUsuarioLogueado();

            if (correo != null && !correo.trim().isEmpty()) {
                if (!correo.equals(usuarioLogueado.getCorreo())) {
                    if (usuarioService.existeCorreo(correo)) {
                        redirectAttributes.addFlashAttribute("error",
                                messageSource.getMessage("controller.usuarios.registro.correo.en.sistema", null, locale));
                        return "redirect:/admin/perfil/" + usuarioLogueado.getIdUsuario();
                    }
                }
            }

            administrador.setIdAdministrador(idAdministrador);
            administrador.setIdUsuario(administradorExistente.getIdUsuario());
            administrador.setFechaCreacion(administradorExistente.getFechaCreacion());
            administrador.setFechaModificacion(LocalDateTime.now());

            adminService.save(administrador);

            Usuario usuario = usuarioService.getUsuarioPorId(administradorExistente.getIdUsuario()).orElse(null);

            if (usuario != null) {
                usuario.setNombre(administrador.getNombre());
                usuario.setApellido1(administrador.getApellido1());
                usuario.setApellido2(administrador.getApellido2());
                usuario.setCorreo(correo);
                usuario.setTelefono(telefono);
                usuarioService.save(usuario);
            }

            redirectAttributes.addFlashAttribute("mensaje",
                    messageSource.getMessage("perfil.actualizado.correctamente", null, locale));

        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("error.actualizar.perfil", null, locale) + ": " + e.getMessage());
        }

        return "redirect:/admin/perfil/" + usuarioService.getUsuarioLogueado().getIdUsuario();
    }

    @PostMapping("/desactivar/{idAdministrador}")
    public String desactivarPerfil(
            @PathVariable Integer idAdministrador,
            RedirectAttributes redirectAttributes,
            Locale locale) {

        try {
            Admin administrador = adminService.getAdministradorPorId(idAdministrador);

            if (administrador == null) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("admin.no.encontrado", null, locale));
                return "redirect:/";
            }

            if (!validarAcceso(idAdministrador)) {
                redirectAttributes.addFlashAttribute("error",
                        messageSource.getMessage("e403.texto", null, locale));
                return "redirect:/";
            }

            Usuario usuario = usuarioService.getUsuarioPorId(administrador.getIdUsuario()).orElse(null);

            if (usuario != null) {
                usuario.setActivo(false);
                usuarioService.save(usuario);
            }

            SecurityContextHolder.clearContext();

            redirectAttributes.addFlashAttribute("mensaje",
                    messageSource.getMessage("admin.cuenta.desactivada", null, locale));
            return "redirect:/login?cuentaDesactivada=true";

        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            redirectAttributes.addFlashAttribute("error",
                    messageSource.getMessage("admin.error.desactivar", null, locale) + ": " + e.getMessage());
            return "redirect:/login";
        }
    }
}

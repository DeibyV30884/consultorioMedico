package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Admin;
import com.proyecto.consultorioMedico.domain.Cita;
import com.proyecto.consultorioMedico.domain.Medico;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Rol;
import com.proyecto.consultorioMedico.domain.Secretaria;
import com.proyecto.consultorioMedico.domain.Usuario;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUsuariosService {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private RolService rolService;
    
    @Autowired
    private MedicoService medicoService;
    
    @Autowired
    private SecretariaService secretariaService;
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private AdminService adminService;
    
    @Autowired
    private CitaService citaService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public static class UsuarioConRol {
        public Usuario usuario;
        public Object entidad; 
        public Rol rol;
        
        public UsuarioConRol(Usuario usuario, Object entidad, Rol rol) {
            this.usuario = usuario;
            this.entidad = entidad;
            this.rol = rol;
        }
    }
    
    @Transactional(readOnly = true)
    public List<UsuarioConRol> getAllUsuariosConRol() {
        List<UsuarioConRol> resultado = new ArrayList<>();
        
        List<Medico> medicos = medicoService.getMedicos();
        for (Medico medico : medicos) {
            Usuario usuario = usuarioService.getUsuarioPorId(medico.getIdUsuario()).orElse(null);
            if (usuario != null && usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
                Rol rol = usuario.getRoles().get(0);
                resultado.add(new UsuarioConRol(usuario, medico, rol));
            }
        }
        
        List<Secretaria> secretarias = secretariaService.getSecretarias();
        for (Secretaria secretaria : secretarias) {
            Usuario usuario = usuarioService.getUsuarioPorId(secretaria.getIdUsuario()).orElse(null);
            if (usuario != null && usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
                Rol rol = usuario.getRoles().get(0);
                resultado.add(new UsuarioConRol(usuario, secretaria, rol));
            }
        }
        
        List<Paciente> pacientes = pacienteService.getPacientes();
        for (Paciente paciente : pacientes) {
            if (paciente.getIdUsuario() != null) {
                Usuario usuario = usuarioService.getUsuarioPorId(paciente.getIdUsuario()).orElse(null);
                if (usuario != null && usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
                    Rol rol = usuario.getRoles().get(0);
                    resultado.add(new UsuarioConRol(usuario, paciente, rol));
                }
            }
        }
        
        List<Admin> admins = adminService.getAdministradores();
        for (Admin admin : admins) {
            Usuario usuario = usuarioService.getUsuarioPorId(admin.getIdUsuario()).orElse(null);
            if (usuario != null && usuario.getRoles() != null && !usuario.getRoles().isEmpty()) {
                Rol rol = usuario.getRoles().get(0);
                resultado.add(new UsuarioConRol(usuario, admin, rol));
            }
        }
        
        return resultado;
    }
    
    @Transactional
public Usuario crearUsuario(String nombre, String apellido1, String apellido2, 
                            String correo, String telefono, String rolNombre, String username) {
    if (username == null || username.trim().isEmpty()) {
        username = generarUsernameUnico(correo);
    } else {
        username = username.trim().toLowerCase();
        if (usuarioService.existeUsername(username)) {
            throw new IllegalArgumentException("El nombre de usuario ya existe en el sistema");
        }
    }
    
    Usuario usuario = new Usuario();
    usuario.setNombre(nombre);
    usuario.setApellido1(apellido1);
    usuario.setApellido2(apellido2);
    usuario.setCorreo(correo);
    usuario.setTelefono(telefono);
    usuario.setUsername(username);
    usuario.setPassword(passwordEncoder.encode("CLave2312@#"));
    usuario.setActivo(true);
    
    usuarioService.save(usuario);
    
    Rol rol = rolService.getRolByNombre(rolNombre);
    if (rol != null) {
        rolService.asignarRolAUsuario(usuario, rol);
    }
    
    if (rolNombre.equals("MEDICO")) {
        Medico medico = new Medico();
        medico.setIdUsuario(usuario.getIdUsuario());
        medico.setNombre(nombre);
        medico.setApellido1(apellido1);
        medico.setApellido2(apellido2);
        medico.setEspecialidad("Por definir");
        medicoService.save(medico);
    } else if (rolNombre.equals("SECRETARIA")) {
        Secretaria secretaria = new Secretaria();
        secretaria.setIdUsuario(usuario.getIdUsuario());
        secretaria.setNombre(nombre);
        secretaria.setApellido1(apellido1);
        secretaria.setApellido2(apellido2);
        secretaria.setFechaCreacion(LocalDateTime.now());
        secretaria.setFechaModificacion(LocalDateTime.now());
        secretariaService.save(secretaria);
    } else if (rolNombre.equals("CLIENTE")) {
        Paciente paciente = new Paciente();
        paciente.setIdUsuario(usuario.getIdUsuario());
        paciente.setNombre(nombre);
        paciente.setApellido1(apellido1);
        paciente.setApellido2(apellido2);
        paciente.setCorreoElectronico(correo);
        paciente.setTelefono(telefono);
        paciente.setFechaCreacion(LocalDateTime.now());
        paciente.setFechaModificacion(LocalDateTime.now());
        pacienteService.save(paciente);
    } else if (rolNombre.equals("ADMIN")) {
        Admin admin = new Admin();
        admin.setIdUsuario(usuario.getIdUsuario());
        admin.setNombre(nombre);
        admin.setApellido1(apellido1);
        admin.setApellido2(apellido2);
        admin.setFechaCreacion(LocalDateTime.now());
        admin.setFechaModificacion(LocalDateTime.now());
        adminService.save(admin);
    }
    
    return usuario;
}
    
    @Transactional
    public void actualizarUsuario(Integer idUsuario, String nombre, String apellido1, 
                                  String apellido2, String correo, String telefono) {
        Usuario usuario = usuarioService.getUsuarioPorId(idUsuario).orElse(null);
        if (usuario == null) {
            return;
        }
        
        usuario.setNombre(nombre);
        usuario.setApellido1(apellido1);
        usuario.setApellido2(apellido2);
        usuario.setCorreo(correo);
        usuario.setTelefono(telefono);
        usuarioService.save(usuario);
        
        Medico medico = medicoService.getMedicoPorIdUsuario(usuario.getIdUsuario());
        if (medico != null) {
            medico.setNombre(nombre);
            medico.setApellido1(apellido1);
            medico.setApellido2(apellido2);
            medicoService.save(medico);
            return;
        }
        
        Secretaria secretaria = secretariaService.getSecretariaPorIdUsuario(usuario.getIdUsuario());
        if (secretaria != null) {
            secretaria.setNombre(nombre);
            secretaria.setApellido1(apellido1);
            secretaria.setApellido2(apellido2);
            secretaria.setFechaModificacion(LocalDateTime.now());
            secretariaService.save(secretaria);
            return;
        }
        
        Paciente paciente = pacienteService.getPacientePorIdUsuario(usuario.getIdUsuario());
        if (paciente != null) {
            paciente.setNombre(nombre);
            paciente.setApellido1(apellido1);
            paciente.setApellido2(apellido2);
            paciente.setCorreoElectronico(correo);
            paciente.setTelefono(telefono);
            paciente.setFechaModificacion(LocalDateTime.now());
            pacienteService.save(paciente);
            return;
        }
        
        Admin admin = adminService.getAdministradorPorIdUsuario(usuario.getIdUsuario());
        if (admin != null) {
            admin.setNombre(nombre);
            admin.setApellido1(apellido1);
            admin.setApellido2(apellido2);
            admin.setFechaModificacion(LocalDateTime.now());
            adminService.save(admin);
        }
    }
    
    @Transactional
    public boolean eliminarUsuario(Integer idUsuario) {
        Paciente paciente = pacienteService.getPacientePorIdUsuario(idUsuario);
        if (paciente != null) {
            List<Cita> citas = citaService.getCitasPorPaciente(paciente.getIdPaciente());
            if (citas != null && !citas.isEmpty()) {
                return false;
            }
        }
        
        Medico medico = medicoService.getMedicoPorIdUsuario(idUsuario);
        if (medico != null) {
            List<Cita> citas = citaService.getCitasPorMedico(medico.getIdMedico());
            if (citas != null && !citas.isEmpty()) {
                return false;
            }
        }
        
        Usuario usuario = usuarioService.getUsuarioPorId(idUsuario).orElse(null);
        if (usuario == null) {
            return false;
        }
        
        if (medico != null) {
            medicoService.delete(medico);
        }
        
        Secretaria secretaria = secretariaService.getSecretariaPorIdUsuario(usuario.getIdUsuario());
        if (secretaria != null) {
            secretariaService.delete(secretaria);
        }
        
        if (paciente != null) {
            pacienteService.delete(paciente);
        }
                Admin admin = adminService.getAdministradorPorIdUsuario(usuario.getIdUsuario());
        if (admin != null) {
            adminService.delete(admin);
        }
        
        return usuarioService.delete(usuario);
    }
    
    private String generarUsernameUnico(String correo) {
        String baseUsername = correo.split("@")[0].toLowerCase();
        String username = baseUsername;
        int contador = 1;
        
        while (usuarioService.existeUsername(username)) {
            username = baseUsername + contador;
            contador++;
        }
        
        return username;
    }
    
    @Transactional(readOnly = true)
    public String obtenerRolPrincipal(Usuario usuario) {
        if (medicoService.getMedicoPorIdUsuario(usuario.getIdUsuario()) != null) {
            return "MEDICO";
        }
        if (secretariaService.getSecretariaPorIdUsuario(usuario.getIdUsuario()) != null) {
            return "SECRETARIA";
        }
        if (pacienteService.getPacientePorIdUsuario(usuario.getIdUsuario()) != null) {
            return "CLIENTE";
        }
        if (adminService.getAdministradorPorIdUsuario(usuario.getIdUsuario()) != null) {
            return "ADMIN";
        }
        return "DESCONOCIDO";
    }
}
package com.proyecto.consultorioMedico.security;
import com.proyecto.consultorioMedico.domain.Medico;
import com.proyecto.consultorioMedico.domain.Paciente;
import com.proyecto.consultorioMedico.domain.Usuario;
import com.proyecto.consultorioMedico.service.MedicoService;
import com.proyecto.consultorioMedico.service.PacienteService;
import com.proyecto.consultorioMedico.service.UsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List; 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component 
public class InicioSegunRol implements AuthenticationSuccessHandler {
    
    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private PacienteService pacienteService;
    
    @Autowired
    private MedicoService medicoService;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication ) throws IOException, ServletException {
        
        List<GrantedAuthority> authorities = new ArrayList<> (authentication.getAuthorities());
        String redirectUrl = null;
        
        String username = authentication.getName();
        Usuario usuario = usuarioService.getUsuarioLogueado();
        
        for (int i = 0; i < authorities.size(); i++ ) {
            String role = authorities.get(i).getAuthority();
            
            if (role.equals("ROLE_ADMINISTRADOR")) { 
                redirectUrl = "/admin/reportes";
                break;
            } else if (role.equals("ROLE_MEDICO")) { 
                Medico medico = medicoService.getMedicoPorIdUsuario(usuario.getIdUsuario());
                if (medico != null) {
                    redirectUrl = "/medico/inicio/" + medico.getIdMedico();
                } else {
                    System.out.println("Error, el suario no es valido" + username);
                    redirectUrl = "/";
                }
                break;
                
            } else if (role.equals("ROLE_SECRETARIA")) { 
                redirectUrl = "/secretaria/inicio";
                break;
                
            } else if (role.equals("ROLE_CLIENTE")) { 
                Paciente paciente = pacienteService.getPacientePorIdUsuario(usuario.getIdUsuario());
                if (paciente != null) {
                    redirectUrl = "/paciente/tratamientos/" + paciente.getIdPaciente();
                } else {
                    System.out.println("Error, el suario no es valido" + username);
                    redirectUrl = "/";
                }
                break;
            }
        }
        
        if (redirectUrl == null) {
            redirectUrl = "/login";
        }
        
        String contextPath = request.getContextPath();
        String fullUrl = contextPath + redirectUrl;
        
        response.sendRedirect(fullUrl);
    } 
}
package com.proyecto.consultorioMedico.security;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List; 
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component 
public class InicioSegunRol implements AuthenticationSuccessHandler {
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        Authentication authentication ) throws IOException, ServletException {
        
        List<GrantedAuthority> authorities = new ArrayList<> (authentication.getAuthorities());
        String redirectUrl = null;
        
        for (int i = 0; i < authorities.size(); i++ ) {
            String role = authorities.get(i).getAuthority();
            
            if (role.equals("ROLE_ADMINISTRADOR")) { 
                redirectUrl = "/admin/reportes";
                break;
            } else if (role.equals("ROLE_MEDICO")) { 
                redirectUrl = "/medico/inicio";
                break;
            } else if (role.equals("ROLE_SECRETARIA")) { 
                redirectUrl = "/secretaria/inicio";
                break;
            } else if (role.equals("ROLE_CLIENTE")) { 
                redirectUrl = "/paciente/inicio";
                break;
            }
        }
        
        if (redirectUrl == null) {
            redirectUrl = "/index";
        }
        
        String contextPath = request.getContextPath();
        String fullUrl = contextPath + redirectUrl;
        
        response.sendRedirect(fullUrl);
    } 
}
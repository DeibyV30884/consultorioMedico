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
        
        for (int i = 0; i < authorities.size(); i++ ) {
            String role = authorities.get(i).getAuthority();
            
            if (role.equals("ROLE_ADMINISTRADOR")) { 
                response.sendRedirect("/admin/inicio");
                return;
            } else if (role.equals("ROLE_MEDICO")) { 
                response.sendRedirect("/medico/inicio");
                return;
            } else if (role.equals("ROLE_SECRETARIA")) { 
                response.sendRedirect("/secretaria/inicio");
                return;
            } else if (role.equals("ROLE_CLIENTE")) { 
                response.sendRedirect("/paciente/inicio");
                return;
            }
        }
        
        response.sendRedirect("/");
    } 
}
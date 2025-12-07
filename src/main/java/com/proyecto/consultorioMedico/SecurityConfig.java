package com.proyecto.consultorioMedico;

import com.proyecto.consultorioMedico.domain.Ruta;
import com.proyecto.consultorioMedico.security.InicioSegunRol;
import com.proyecto.consultorioMedico.service.RutaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    
    @Autowired
    private InicioSegunRol inicioSegunRol;
    
    @Bean
public SecurityFilterChain securityFilterChain(HttpSecurity http, @Lazy RutaService rutaService) throws Exception {
    var rutas = rutaService.getRutas();
    
    http.authorizeHttpRequests(requests -> {
        for (Ruta ruta : rutas) {
            if (!ruta.getRequiereRol()) {
                requests.requestMatchers(ruta.getRuta()).permitAll();
            }
        }
        
        for (Ruta ruta : rutas) {
            if (ruta.getRequiereRol() && ruta.getRol() != null) {
                requests.requestMatchers(ruta.getRuta()).hasRole(ruta.getRol().getNombre());
            }
        }
        requests.anyRequest().authenticated();
    })
    .formLogin(form -> form
            .loginPage("/login")
            .loginProcessingUrl("/login")
            .usernameParameter("username")
            .passwordParameter("password")
            .successHandler(inicioSegunRol)
            .failureUrl("/login?error=true")
            .permitAll()
    )
    .logout(logout -> logout
            .logoutUrl("/logout")
            .logoutSuccessUrl("/login?logout")
            .invalidateHttpSession(true)
            .deleteCookies("JSESSIONID")
            .permitAll()
    )
    .exceptionHandling(exceptions -> exceptions
            .accessDeniedPage("/error/403")
    )
    .sessionManagement(session -> session
            .maximumSessions(1)
            .maxSessionsPreventsLogin(false)
    );
    
    return http.build();
}
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder build, 
            @Lazy PasswordEncoder passwordEncoder, 
            @Lazy UserDetailsService userDetailsService) throws Exception {
        build.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }
}
package com.proyecto.consultorioMedico;

import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.thymeleaf.spring6.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;

@Configuration
public class ProjectConfig implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("index");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/registro").setViewName("registro");
        registry.addViewController("/error/**").setViewName("error");
    }

    @Bean
    public SpringResourceTemplateResolver templateResolver_0() {
        SpringResourceTemplateResolver resolver = new SpringResourceTemplateResolver();
        resolver.setPrefix("classpath:/templates");
        resolver.setSuffix(".html");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setOrder(0);
        resolver.setCheckExistence(true);
        return resolver;
    }

    @Bean
    public LocaleResolver localeResolver() {
        var slr = new SessionLocaleResolver();
        slr.setDefaultLocale(Locale.getDefault());
        slr.setLocaleAttributeName("session.current.locale");
        slr.setTimeZoneAttributeName("session.current.timezone");
        return slr;
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        var lci = new LocaleChangeInterceptor();
        lci.setParamName("lang");
        return lci;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registro) {
        registro.addInterceptor(localeChangeInterceptor());
    }

    @Bean("messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((request) -> request
                        // Rutas publicas
                        .requestMatchers("/", "/index", "/login", "/registro", 
                                "/error/**", "/webjars/**", "/js/**", "/css/**", "/images/**")
                        .permitAll()
                        
                        // Rutas de los Administrador
                        .requestMatchers("/admin/**", "/usuario/**", "/rol/**", 
                                "/ruta/**", "/constante/**")
                        .hasRole("ADMINISTRADOR")
                        
                        // Rutas de los Medico
                        .requestMatchers("/medico/**", "/paciente/ver/**", 
                                "/cita/ver/**", "/registro-clinico/**", "/prescripcion/**")
                        .hasRole("MEDICO")
                        
                        // Rutas de los Secretaria
                        .requestMatchers("/secretaria/**", "/paciente/**", "/cita/**")
                        .hasRole("SECRETARIA")
                        
                        // Rutas de los Pacientes
                        .requestMatchers("/paciente/inicio", "/paciente/tratamientos", 
                                "/paciente/perfil", "/cita/mis-citas")
                        .hasRole("CLIENTE")
                        
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );
        return http.build();
    }

    @Bean
    public UserDetailsService users() {
        //Administrador
        UserDetails admin = User.builder()
                .username("admin")
                .password("{noop}1234")
                .roles("ADMINISTRADOR")
                .build();
        
        //Médico
        UserDetails medico = User.builder()
                .username("dr.perez")
                .password("{noop}1234")
                .roles("MEDICO")
                .build();
        
        //Secretaria
        UserDetails secretaria = User.builder()
                .username("secretaria")
                .password("{noop}1234")
                .roles("SECRETARIA")
                .build();
        
        //Paciente
        UserDetails paciente = User.builder()
                .username("paciente1")
                .password("{noop}1111")
                .roles("CLIENTE")
                .build();
        
        return new InMemoryUserDetailsManager(admin, medico, secretaria, paciente);
    }
}
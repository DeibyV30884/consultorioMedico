package com.proyecto.consultorioMedico.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class CorreoService {
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void enviarCorreoHtml(String para,
            String asunto,
            String contenido) throws MessagingException {
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        helper.setTo(para);
        helper.setSubject(asunto);
        helper.setText(contenido, true);
        mailSender.send(message);
    }
}
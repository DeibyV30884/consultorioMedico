package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "paciente")
public class Paciente implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name = "id_paciente")
    private Integer idPaciente;
    
    @Column(name = "id_usuario")
    private Integer idUsuario;
    
    @Column(nullable = false, length = 50)
    private String nombre; 
    
    @Column(name = "apellido_1", nullable = false, length = 30)
    private String apellido1;
    
    @Column(name = "apellido_2", length = 30) 
    private String apellido2;
    
    @Column(name = "correo_electronico", length = 75)
    private String correoElectronico;
    
}
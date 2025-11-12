/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;// para fechaHora
import java.util.List;
import lombok.Data;

/**
 *
 * @author Alejandro
 */

@Data
@Entity
@Table(name="cita")
public class Cita implements Serializable{
   
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name ="id_cita")
    private Integer idCita;
    
    @Column(name = "fecha_hora")
    private LocalDateTime  fechaHora;
    
    //estado
    @Enumerated(EnumType.STRING)
    @Column(name = "estado")
    private EstadoCita estado;
    
    @Column (columnDefinition = "TEXT")
    private String tratamiento;
    
    @Column(unique = true, nullable = false, length = 50 )
    private String tipoConsulta; 
    
    
//    @ManyToOne
//    @JoinColumn(name = "id_paciente")
//    private Paciente paciente;
}


    
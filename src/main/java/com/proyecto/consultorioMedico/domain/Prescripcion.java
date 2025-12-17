package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@Entity
@Table(name = "prescripcion")
public class Prescripcion implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_prescripcion")
    private Integer idPrescripcion;
    
    @ManyToOne
    @JoinColumn(name = "id_cita", nullable = false)
    private Cita cita;
    
    @Column(name = "medicamento", columnDefinition = "TEXT")
    private String medicamento;
    
    @Column(name = "dosis", length = 100)
    private String dosis;
    
    @Column(name = "duracion_dias")
    private Integer duracionDias;
    
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
}
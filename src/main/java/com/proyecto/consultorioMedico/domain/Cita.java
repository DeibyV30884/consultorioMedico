package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import lombok.Data;

/**
 *
 * @author Alejandro
 */
@Data
@Entity
@Table(name = "cita")
public class Cita implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cita")
    private Integer idCita;
    
    @ManyToOne
    @JoinColumn(name = "id_paciente", nullable = false)
    private Paciente paciente;
    
    @ManyToOne
    @JoinColumn(name = "id_medico", nullable = false)
    private Medico medico;
    
    @ManyToOne
    @JoinColumn(name = "id_motivo_cita", nullable = true)
    private MotivoCita motivoCita;
    
    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;
    
    @Column(name = "hora", nullable = false)
    private LocalTime hora;
    
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;
    
    @Column(name = "tratamiento", columnDefinition = "TEXT")
    private String tratamiento;
    
    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;
    
    @Column(name = "tipo_consulta", length = 50)
    private String tipoConsulta;
    
    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion")
    private LocalDateTime fechaModificacion;
    
    public LocalDateTime getFechaHora() {
        if (fecha != null && hora != null) {
            return LocalDateTime.of(fecha, hora);
        }
        return null;
    }
    
    public void setFechaHora(LocalDateTime fechaHora) {
        if (fechaHora != null) {
            this.fecha = fechaHora.toLocalDate();
            this.hora = fechaHora.toLocalTime();
        }
    }
}
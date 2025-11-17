
package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.time.LocalDate;
import lombok.Data;


@Data
@Entity
@Table(name="paciente")
public class Paciente implements Serializable{
   
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_paciente")
    private Integer idPaciente;

    @Column(name = "id_usuario")
    private Integer idUsuario;  // Si quieres, luego lo convertimos a relación ManyToOne

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "apellido_1", length = 30, nullable = false)
    private String apellido1;

    @Column(name = "apellido_2", length = 30)
    private String apellido2;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(name = "correo_electronico", length = 75)
    private String correoElectronico;

    @Column(name = "ocupacion", length = 50)
    private String ocupacion;

    @Column(name = "estado_civil", length = 20)
    private String estadoCivil;

    @Column(name = "telefono", length = 25)
    private String telefono;

    @Column(name = "antecedentes_heredo_familiares", columnDefinition = "TEXT")
    private String antecedentesHeredoFamiliares;

    @Column(name = "antecedentes_personales", columnDefinition = "TEXT")
    private String antecedentesPersonales;

    @Column(name = "antecedentes_quirurgicos", columnDefinition = "TEXT")
    private String antecedentesQuirurgicos;

    @Column(name = "antecedentes_gineco_obstetricos", columnDefinition = "TEXT")
    private String antecedentesGinecoObstetricos;

    @Column(name = "fecha_creacion", updatable = false)
    private java.sql.Timestamp fechaCreacion;

    @Column(name = "fecha_modificacion")
    private java.sql.Timestamp fechaModificacion;
    
        
}


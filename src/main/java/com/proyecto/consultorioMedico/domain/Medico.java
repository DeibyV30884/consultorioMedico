
package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.Data;


@Data
@Entity
@Table(name="medico")
public class Medico implements Serializable{
   
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column (name ="id_medico")
    private Integer idMedico;
            
    @Column(name = "id_usuario", nullable = false)
    private Integer idUsuario;  // O relación con Usuario si lo deseas usar

    @Column(name = "nombre", length = 50, nullable = false)
    private String nombre;

    @Column(name = "apellido_1", length = 30, nullable = false)
    private String apellido1;

    @Column(name = "apellido_2", length = 30)
    private String apellido2;

    @Column(name = "especialidad", length = 50)
    private String especialidad;
    
            
}

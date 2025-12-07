package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "rol")
public class Rol implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rol") 
    private Integer idRol;
    
    @Column(unique = true, length = 20)
    private String nombre;
    
    @Column(length = 100) 
    private String descripcion;
    
    @Column(name = "fecha_creacion", insertable = false, updatable = false)
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
    
    @ManyToMany(mappedBy = "roles")
    private List<Usuario> usuarios;
}
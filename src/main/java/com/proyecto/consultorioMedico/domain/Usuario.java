package com.proyecto.consultorioMedico.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;
    
    @Column(unique = true, nullable = false, length = 30)
    private String username;
    
    @Column(nullable = false, length = 512)
    private String  password;
    
    @Column(nullable = false, length = 20)
    private String nombre;
    
    @Column(name = "apellido_1", nullable = false, length = 30)
    private String apellido1; 
    
    @Column(name = "apellido_2", length = 30)
    private String apellido2;
    
    @Column(unique = true, length = 75)
    private String correo;
    
    @Column(length = 25) 
    private String telefono;
    
    @Column(name = "ruta_imagen", length = 1024)
    private String rutaImagen;
     
    @Column(name = "activo")
    private Boolean activo; 
    
    @Column(name = "fecha_creacion", insertable = false, updatable = false) 
    private LocalDateTime fechaCreacion;
    
    @Column(name = "fecha_modificacion", insertable = false, updatable = false)
    private LocalDateTime fechaModificacion;
    
   //many to many previendo que un usuario pueda tener 2 roles, como un medico que tambien sea administrador 
    @ManyToMany
    @JoinTable(
        name = "usuario_rol", 
        joinColumns = @JoinColumn( name = "id_usuario"),
        inverseJoinColumns = @JoinColumn( name = "id_rol")
    )
    
    private List<Rol> roles;
}
package com.proyecto.consultorioMedico.domain;

public class EstadoCita {
    
    public static final String PENDIENTE = "Pendiente";
    public static final String CONFIRMADA = "Confirmada";
    public static final String COMPLETADA = "Completada";
    public static final String CANCELADA = "Cancelada";
    
    private EstadoCita() {
        throw new IllegalStateException("Clase no instanciable");
    }
    
    public static boolean esValido(String estado) {
        return PENDIENTE.equals(estado) || 
               CONFIRMADA.equals(estado) || 
               COMPLETADA.equals(estado) || 
               CANCELADA.equals(estado);
    }
}
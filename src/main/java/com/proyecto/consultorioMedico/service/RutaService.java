package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.Ruta;
import com.proyecto.consultorioMedico.repository.RutaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RutaService {
    
    @Autowired
    private RutaRepository rutaRepository;
    
    @Transactional(readOnly = true)
    public List<Ruta> getRutas() {
        return rutaRepository.findAllByOrderByRequiereRolAsc();
    }
}
package com.proyecto.consultorioMedico.service;

import com.proyecto.consultorioMedico.domain.TipoConsulta;
import com.proyecto.consultorioMedico.repository.TipoConsultaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TipoConsultaService {

    @Autowired
    private TipoConsultaRepository tipoConsultaRepository;

    @Transactional(readOnly = true)
    public List<TipoConsulta> getTipoConsultas() { 
        return tipoConsultaRepository.findAll();
    }

    @Transactional(readOnly = true)
    public TipoConsulta getTipoConsulta( TipoConsulta tipoConsulta) {
        return tipoConsultaRepository.findById(tipoConsulta.getIdTipoConsulta()).orElse(null);
    }
}


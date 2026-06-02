package com.sanosysalvos.ms_geolocalizacion.service;

import com.sanosysalvos.ms_geolocalizacion.dto.UbicacionDTO;
import com.sanosysalvos.ms_geolocalizacion.model.Ubicacion;
import com.sanosysalvos.ms_geolocalizacion.repository.UbicacionRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime; // <-- IMPORTANTE AÑADIR ESTE IMPORT
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UbicacionServiceImpl implements UbicacionService {

    @Autowired
    private UbicacionRepository ubicacionRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public UbicacionDTO registrar(UbicacionDTO dto) {
        Ubicacion ubicacion = modelMapper.map(dto, Ubicacion.class);
        
        ubicacion.setFechaRegistro(LocalDateTime.now());
        
        Ubicacion guardada = ubicacionRepository.save(ubicacion);
        return modelMapper.map(guardada, UbicacionDTO.class);
    }

    @Override
    public List<UbicacionDTO> listarPorMascota(Long idMascota) {
        return ubicacionRepository.findByIdMascota(idMascota)
                .stream()
                .map(u -> modelMapper.map(u, UbicacionDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<UbicacionDTO> listarParaMapa() {
        return ubicacionRepository.findAllByOrderByFechaRegistroDesc()
                .stream()
                .map(u -> modelMapper.map(u, UbicacionDTO.class))
                .collect(Collectors.toList());
    }
}
package com.sanosysalvos.ms_mascotas.service;

import com.sanosysalvos.ms_mascotas.dto.MascotaDTO;
import com.sanosysalvos.ms_mascotas.exception.ResourceNotFoundException;
import com.sanosysalvos.ms_mascotas.model.EstadoMascota;
import com.sanosysalvos.ms_mascotas.model.Mascota;
import com.sanosysalvos.ms_mascotas.repository.MascotaRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MascotaService {

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private ModelMapper modelMapper;

    public MascotaDTO crearMascota(MascotaDTO dto) {
        Mascota mascota = modelMapper.map(dto, Mascota.class);
        Mascota guardado = mascotaRepository.save(mascota);
        return modelMapper.map(guardado, MascotaDTO.class);
    }

    public List<MascotaDTO> obtenerTodos() {
        List<Mascota> mascotas = mascotaRepository.findAll();
        return mascotas.stream()
                .map(mascota -> modelMapper.map(mascota, MascotaDTO.class))
                .collect(Collectors.toList());
    }

    public MascotaDTO obtenerPorId(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con id: " + id));
        return modelMapper.map(mascota, MascotaDTO.class);
    }

    public List<MascotaDTO> obtenerPorUsuario(Long idUsuario) {
        List<Mascota> mascotas = mascotaRepository.findByIdUsuario(idUsuario);
        return mascotas.stream()
                .map(mascota -> modelMapper.map(mascota, MascotaDTO.class))
                .collect(Collectors.toList());
    }

    public List<MascotaDTO> obtenerPorEstado(EstadoMascota estado) {
        List<Mascota> mascotas = mascotaRepository.findByEstado(estado);
        return mascotas.stream()
                .map(mascota -> modelMapper.map(mascota, MascotaDTO.class))
                .collect(Collectors.toList());
    }

    public MascotaDTO actualizarMascota(Long id, MascotaDTO dto) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con id: " + id));
        
        mascota.setNombre(dto.getNombre());
        mascota.setEspecie(dto.getEspecie());
        mascota.setRaza(dto.getRaza());
        mascota.setColor(dto.getColor());
        mascota.setTamaño(dto.getTamaño());
        mascota.setFotoUrl(dto.getFotoUrl());
        mascota.setUltimaUbicacion(dto.getUltimaUbicacion());
        mascota.setDescripcion(dto.getDescripcion());
        mascota.setFechaSuceso(dto.getFechaSuceso());
        mascota.setEstado(dto.getEstado());
        mascota.setIdUsuario(dto.getIdUsuario());

        Mascota actualizada = mascotaRepository.save(mascota);
        return modelMapper.map(actualizada, MascotaDTO.class);
    }

    public void eliminarMascota(Long id) {
        Mascota mascota = mascotaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Mascota no encontrada con id: " + id));
        mascotaRepository.delete(mascota);
    }

    public void eliminarPorUsuario(Long idUsuario) {
        mascotaRepository.deleteByIdUsuario(idUsuario);
    }
}

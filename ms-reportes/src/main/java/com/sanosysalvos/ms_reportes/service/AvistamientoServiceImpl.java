package com.sanosysalvos.ms_reportes.service;

import com.sanosysalvos.ms_reportes.config.RabbitMQConfig;
import com.sanosysalvos.ms_reportes.dto.AvistamientoDTO;
import com.sanosysalvos.ms_reportes.exception.ResourceNotFoundException;
import com.sanosysalvos.ms_reportes.model.Avistamiento;
import com.sanosysalvos.ms_reportes.repository.AvistamientoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvistamientoServiceImpl implements AvistamientoService {

    private final AvistamientoRepository avistamientoRepository;
    private final ModelMapper modelMapper;
    private final RabbitTemplate rabbitTemplate;

    // Inyección por constructor
    public AvistamientoServiceImpl(AvistamientoRepository avistamientoRepository, 
                                   ModelMapper modelMapper, 
                                   RabbitTemplate rabbitTemplate) {
        this.avistamientoRepository = avistamientoRepository;
        this.modelMapper = modelMapper;
        this.rabbitTemplate = rabbitTemplate;
    }

    @Override
    public AvistamientoDTO crear(AvistamientoDTO dto) {
        Avistamiento avistamiento = modelMapper.map(dto, Avistamiento.class);
        Avistamiento guardado = avistamientoRepository.save(avistamiento);

        AvistamientoDTO guardadoDto = modelMapper.map(guardado, AvistamientoDTO.class);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_REPORTE_CREADO, guardadoDto);

        return guardadoDto;
    }

    @Override
    public List<AvistamientoDTO> listarRecientes() {
        return avistamientoRepository.findAllByOrderByFechaReporteDesc()
                .stream()
                .map(a -> modelMapper.map(a, AvistamientoDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AvistamientoDTO obtenerPorId(Long id) {
        Avistamiento avistamiento = avistamientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Avistamiento no encontrado con id: " + id));
        return modelMapper.map(avistamiento, AvistamientoDTO.class);
    }
}
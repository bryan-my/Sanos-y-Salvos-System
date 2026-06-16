package com.sanosysalvos.ms_reportes.service;

import com.sanosysalvos.ms_reportes.config.RabbitMQConfig;
import com.sanosysalvos.ms_reportes.dto.AvistamientoDTO;
import com.sanosysalvos.ms_reportes.dto.AvistamientoMensajeDTO;
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
        System.out.println("[AvistamientoServiceImpl] Creando nuevo avistamiento...");
        Avistamiento avistamiento = modelMapper.map(dto, Avistamiento.class);
        Avistamiento guardado = avistamientoRepository.save(avistamiento);
        System.out.println("[AvistamientoServiceImpl] Avistamiento guardado con ID: " + guardado.getId());

        AvistamientoDTO guardadoDto = modelMapper.map(guardado, AvistamientoDTO.class);
        
        // Crear mensaje para RabbitMQ
        AvistamientoMensajeDTO mensaje = new AvistamientoMensajeDTO();
        mensaje.setId(guardado.getId());
        mensaje.setEspecie(guardado.getEspecie());
        mensaje.setDescripcionFisica(guardado.getDescripcionFisica());
        
        System.out.println("[AvistamientoServiceImpl] Enviando mensaje a RabbitMQ: " + mensaje);
        rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, RabbitMQConfig.ROUTING_KEY_REPORTE_CREADO, mensaje);
        System.out.println("[AvistamientoServiceImpl] Mensaje enviado correctamente!");

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
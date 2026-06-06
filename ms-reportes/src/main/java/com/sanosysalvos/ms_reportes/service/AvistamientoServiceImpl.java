package com.sanosysalvos.ms_reportes.service;

import com.sanosysalvos.ms_reportes.config.RabbitMQConfig;
import com.sanosysalvos.ms_reportes.dto.AvistamientoDTO;
import com.sanosysalvos.ms_reportes.model.Avistamiento;
import com.sanosysalvos.ms_reportes.repository.AvistamientoRepository;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AvistamientoServiceImpl implements AvistamientoService {

    @Autowired
    private AvistamientoRepository avistamientoRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

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
}

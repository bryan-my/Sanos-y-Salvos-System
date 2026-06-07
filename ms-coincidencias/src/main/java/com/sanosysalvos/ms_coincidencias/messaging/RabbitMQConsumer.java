package com.sanosysalvos.ms_coincidencias.messaging;

import com.sanosysalvos.ms_coincidencias.dto.AvistamientoMensajeDTO;
import com.sanosysalvos.ms_coincidencias.service.MatchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    @Autowired
    private MatchService matchService;

    @RabbitListener(queues = "reportes.queue")
    public void recibirAvistamiento(AvistamientoMensajeDTO mensaje) {
        if (mensaje == null || mensaje.getId() == null) {
            return;
        }

        matchService.crearPendienteDesdeAvistamiento(mensaje.getId(), 85.5);
    }
}

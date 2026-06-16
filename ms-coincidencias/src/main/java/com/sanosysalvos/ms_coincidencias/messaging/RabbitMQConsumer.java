package com.sanosysalvos.ms_coincidencias.messaging;

import com.sanosysalvos.ms_coincidencias.dto.AvistamientoMensajeDTO;
import com.sanosysalvos.ms_coincidencias.service.MatchService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQConsumer {

    private final MatchService matchService;

    public RabbitMQConsumer(MatchService matchService) {
        this.matchService = matchService;
    }

    @RabbitListener(queues = "reportes.queue")
    public void recibirAvistamiento(AvistamientoMensajeDTO mensaje) {
        System.out.println("[RabbitMQConsumer] ¡Mensaje recibido!");
        System.out.println("[RabbitMQConsumer] Contenido del mensaje: " + mensaje);
        
        if (mensaje == null || mensaje.getId() == null) {
            System.err.println("[RabbitMQConsumer] ERROR: Mensaje o ID son null. Ignorando.");
            return;
        }

        System.out.println("[RabbitMQConsumer] Llamando a procesarNuevoAvistamiento con ID: " + mensaje.getId());
        matchService.procesarNuevoAvistamiento(mensaje.getId());
    }
}

package edu.dosw.parcial.core.validators.chain;

import edu.dosw.parcial.core.utils.ConflictException;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.repositories.ReservaRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DisponibilidadFranjaHandler extends ReservaValidationHandler {

    private final ReservaRepository reservaRepository;
    private final TurnoRepository turnoRepository;

    public DisponibilidadFranjaHandler(ReservaRepository reservaRepository, TurnoRepository turnoRepository) {
        this.reservaRepository = reservaRepository;
        this.turnoRepository = turnoRepository;
    }

    @Override
    public void handle(ReservaValidationContext context) {
        String fecha = context.getRequest().getFecha();
        String hora = context.getRequest().getHora();

        log.info("Paso 1: Validando disponibilidad de franja horaria para {} {}", fecha, hora);

        long reservasActivas = reservaRepository.countByFechaAndHoraAndEstado(fecha, hora, "CONFIRMADA");
        if (reservasActivas >= 3) {
            log.warn("Rechazo en Paso 1: La franja {} {} ya cuenta con {} reservas activas (máx. 3)", fecha, hora, reservasActivas);
            throw new ConflictException("Franja horaria está llena o barbero no disponible");
        }

        List<TurnoDocument> turnosDisponibles = turnoRepository.findByFechaAndHoraAndEstado(fecha, hora, "DISPONIBLE");
        if (turnosDisponibles == null || turnosDisponibles.isEmpty()) {
            log.warn("Rechazo en Paso 1: No existen turnos disponibles para {} {}", fecha, hora);
            throw new ConflictException("Franja horaria está llena o barbero no disponible");
        }

        context.setTurnosDisponibles(turnosDisponibles);
        log.info("Paso 1 superado exitosamente. Turnos disponibles encontrados: {}", turnosDisponibles.size());

        next(context);
    }
}

package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.utils.ConflictException;
import edu.dosw.parcial.core.utils.ResourceNotFoundException;
import edu.dosw.parcial.core.utils.UnprocessableEntityException;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.Map;

@Slf4j
@Component
public class TurnoValidator {

    private static final Map<DayOfWeek, String> DIAS_SEMANA = Map.of(
            DayOfWeek.MONDAY, "LUNES",
            DayOfWeek.TUESDAY, "MARTES",
            DayOfWeek.WEDNESDAY, "MIERCOLES",
            DayOfWeek.THURSDAY, "JUEVES",
            DayOfWeek.FRIDAY, "VIERNES",
            DayOfWeek.SATURDAY, "SABADO",
            DayOfWeek.SUNDAY, "DOMINGO"
    );

    private final TurnoRepository turnoRepository;

    public TurnoValidator(TurnoRepository turnoRepository) {
        this.turnoRepository = turnoRepository;
    }

    public void validarCreacionTurno(Barbero barbero, String barberoId, String fecha, String hora) {
        if (barbero == null) {
            log.warn("Rechazo 404: El barbero con ID '{}' no existe en la base de datos", barberoId);
            throw new ResourceNotFoundException("El barbero no existe en la base de datos");
        }

        LocalDate fechaTurno;
        LocalTime horaTurno;
        try {
            fechaTurno = LocalDate.parse(fecha);
            horaTurno = LocalTime.parse(hora);
        } catch (DateTimeParseException e) {
            log.warn("Rechazo 422: Formato inválido al parsear fecha u hora: {} {}", fecha, hora);
            throw new UnprocessableEntityException("Fecha fuera de los dias u horas de atención del barbero");
        }

        if (fechaTurno.isBefore(LocalDate.now())) {
            log.warn("Rechazo 422: La fecha del turno {} no es futura", fecha);
            throw new UnprocessableEntityException("Fecha fuera de los dias u horas de atención del barbero");
        }

        DayOfWeek dayOfWeek = fechaTurno.getDayOfWeek();
        String diaEspanol = DIAS_SEMANA.get(dayOfWeek);

        if (barbero.getDiasDisponibles() == null || !barbero.getDiasDisponibles().contains(diaEspanol)) {
            log.warn("Rechazo 422: El barbero {} no atiende los días {}", barbero.getNombre(), diaEspanol);
            throw new UnprocessableEntityException("Fecha fuera de los dias u horas de atención del barbero");
        }

        LocalTime horaInicio = LocalTime.parse(barbero.getHoraInicio());
        LocalTime horaFin = LocalTime.parse(barbero.getHoraFin());

        if (horaTurno.isBefore(horaInicio) || horaTurno.isAfter(horaFin.minusHours(1))) {
            log.warn("Rechazo 422: La hora {} está fuera del horario de atención del barbero {}-{}",
                    hora, horaInicio, horaFin);
            throw new UnprocessableEntityException("Fecha fuera de los dias u horas de atención del barbero");
        }

        if (turnoRepository.findByBarberoIdAndFechaAndHora(barberoId, fecha, hora).isPresent()) {
            log.warn("Rechazo 409: Ya existe un turno publicado para el barbero {} en fecha {} y hora {}",
                    barberoId, fecha, hora);
            throw new ConflictException("Ya existe un turno publicado para este barbero en esa fecha y hora");
        }
    }
}

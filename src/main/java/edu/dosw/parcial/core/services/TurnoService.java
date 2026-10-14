package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.TurnoRequestDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
import edu.dosw.parcial.controller.mappers.TurnoMapper;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.core.validators.TurnoValidator;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.BarberoEntityMapper;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TurnoService {

    private final BarberoRepository barberoRepository;
    private final BarberoEntityMapper barberoEntityMapper;
    private final TurnoRepository turnoRepository;
    private final TurnoEntityMapper turnoEntityMapper;
    private final TurnoValidator turnoValidator;
    private final TurnoMapper turnoMapper;

    public TurnoService(
            BarberoRepository barberoRepository,
            BarberoEntityMapper barberoEntityMapper,
            TurnoRepository turnoRepository,
            TurnoEntityMapper turnoEntityMapper,
            TurnoValidator turnoValidator,
            TurnoMapper turnoMapper
    ) {
        this.barberoRepository = barberoRepository;
        this.barberoEntityMapper = barberoEntityMapper;
        this.turnoRepository = turnoRepository;
        this.turnoEntityMapper = turnoEntityMapper;
        this.turnoValidator = turnoValidator;
        this.turnoMapper = turnoMapper;
    }

    public TurnoResponseDTO crearTurno(TurnoRequestDTO request) {
        log.info("Iniciando creación de turno. BarberoId: {}, Fecha: {}, Hora: {}",
                request.getBarberoId(), request.getFecha(), request.getHora());

        Barbero barbero = barberoRepository.findById(request.getBarberoId())
                .map(barberoEntityMapper::toDomain)
                .orElseGet(() -> barberoRepository.findByNombreIgnoreCase(request.getBarberoId())
                        .map(barberoEntityMapper::toDomain)
                        .orElse(null));

        turnoValidator.validarCreacionTurno(barbero, request.getBarberoId(), request.getFecha(), request.getHora());

        String turnoId = "T" + String.format("%02d", turnoRepository.count() + 1);

        Turno turno = Turno.builder()
                .turnoId(turnoId)
                .barberoId(barbero.getId())
                .barberoNombre(barbero.getNombre())
                .fecha(request.getFecha())
                .hora(request.getHora())
                .estado("DISPONIBLE")
                .build();

        TurnoDocument documentoGuardado = turnoRepository.save(turnoEntityMapper.toDocument(turno));
        Turno turnoPersistido = turnoEntityMapper.toDomain(documentoGuardado);

        log.info("Turno creado exitosamente con ID {} para el barbero {}", turnoId, barbero.getNombre());
        return turnoMapper.toResponse(turnoPersistido);
    }
}

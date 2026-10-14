package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.TurnoRequestDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
import edu.dosw.parcial.core.models.Turno;
import org.springframework.stereotype.Component;

@Component
public class TurnoMapper {

    public Turno toDomain(TurnoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Turno.builder()
                .barberoId(dto.getBarberoId())
                .fecha(dto.getFecha())
                .hora(dto.getHora())
                .estado("DISPONIBLE")
                .build();
    }

    public TurnoResponseDTO toResponse(Turno turno) {
        if (turno == null) {
            return null;
        }
        return TurnoResponseDTO.builder()
                .turnoId(turno.getTurnoId())
                .barberoId(turno.getBarberoId())
                .barberoNombre(turno.getBarberoNombre())
                .fecha(turno.getFecha())
                .hora(turno.getHora())
                .estado(turno.getEstado())
                .build();
    }
}

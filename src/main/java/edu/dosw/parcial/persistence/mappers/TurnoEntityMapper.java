package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Turno;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import org.springframework.stereotype.Component;

@Component
public class TurnoEntityMapper {

    public Turno toDomain(TurnoDocument doc) {
        if (doc == null) {
            return null;
        }
        return Turno.builder()
                .id(doc.getId())
                .turnoId(doc.getTurnoId())
                .barberoId(doc.getBarberoId())
                .barberoNombre(doc.getBarberoNombre())
                .fecha(doc.getFecha())
                .hora(doc.getHora())
                .estado(doc.getEstado())
                .build();
    }

    public TurnoDocument toDocument(Turno domain) {
        if (domain == null) {
            return null;
        }
        return TurnoDocument.builder()
                .id(domain.getId())
                .turnoId(domain.getTurnoId())
                .barberoId(domain.getBarberoId())
                .barberoNombre(domain.getBarberoNombre())
                .fecha(domain.getFecha())
                .hora(domain.getHora())
                .estado(domain.getEstado())
                .build();
    }
}

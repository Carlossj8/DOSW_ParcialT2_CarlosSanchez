package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import org.springframework.stereotype.Component;

@Component
public class BarberoEntityMapper {

    public Barbero toDomain(BarberoDocument doc) {
        if (doc == null) {
            return null;
        }
        return Barbero.builder()
                .id(doc.getId())
                .nombre(doc.getNombre())
                .especialidades(doc.getEspecialidades())
                .diasDisponibles(doc.getDiasDisponibles())
                .horaInicio(doc.getHoraInicio())
                .horaFin(doc.getHoraFin())
                .build();
    }

    public BarberoDocument toDocument(Barbero domain) {
        if (domain == null) {
            return null;
        }
        return BarberoDocument.builder()
                .id(domain.getId())
                .nombre(domain.getNombre())
                .especialidades(domain.getEspecialidades())
                .diasDisponibles(domain.getDiasDisponibles())
                .horaInicio(domain.getHoraInicio())
                .horaFin(domain.getHoraFin())
                .build();
    }
}

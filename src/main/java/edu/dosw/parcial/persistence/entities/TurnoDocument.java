package edu.dosw.parcial.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "turnos")
@CompoundIndex(name = "barbero_fecha_hora_idx", def = "{'barberoId': 1, 'fecha': 1, 'hora': 1}", unique = true)
public class TurnoDocument {

    @Id
    private String id;

    private String turnoId;

    private String barberoId;

    private String barberoNombre;

    private String fecha;

    private String hora;

    private String estado;
}

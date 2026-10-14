package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Turno {
    private String id;
    private String turnoId;
    private String barberoId;
    private String barberoNombre;
    private String fecha;
    private String hora;
    private String estado;
}

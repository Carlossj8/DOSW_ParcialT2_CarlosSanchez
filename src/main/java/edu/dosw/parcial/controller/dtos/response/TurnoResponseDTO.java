package edu.dosw.parcial.controller.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoResponseDTO {
    private String turnoId;
    private String barberoId;
    private String barberoNombre;
    private String fecha;
    private String hora;
    private String estado;
}

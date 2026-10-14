package edu.dosw.parcial.controller.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaResponseDTO {
    private String idReserva;
    private String idTurno;
    private String barberoAsignado;
    private String fecha;
    private String hora;
    private Double total;
    private String estado;
}

package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reserva {
    private String idReserva;
    private String idTurno;
    private String barberoAsignado;
    private String fecha;
    private String hora;
    private List<Servicio> servicios;
    private Double total;
    private String estado; // CONFIRMADA, CANCELADA
    private Cliente cliente;
}

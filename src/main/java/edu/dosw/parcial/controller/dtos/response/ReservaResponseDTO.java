package edu.dosw.parcial.controller.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    private List<ServicioDTO> servicios;
    private Double total;
    private String estado;
    private ClienteDTO cliente;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicioDTO {
        private String codigo;
        private String nombre;
        private Double precio;
        private String especialidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteDTO {
        private String nombre;
        private String telefono;
        private String correo;
    }
}

package edu.dosw.parcial.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "reservas")
public class ReservaDocument {

    @Id
    private String id;

    private String idReserva;

    private String idTurno;

    private String barberoAsignado;

    private String fecha;

    private String hora;

    private List<ServicioEmbebido> servicios;

    private Double total;

    private String estado; // CONFIRMADA, CANCELADA

    private ClienteEmbebido cliente;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ServicioEmbebido {
        private String codigo;
        private String nombre;
        private Double precio;
        private String especialidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClienteEmbebido {
        private String nombre;
        private String telefono;
        private String correo;
    }
}

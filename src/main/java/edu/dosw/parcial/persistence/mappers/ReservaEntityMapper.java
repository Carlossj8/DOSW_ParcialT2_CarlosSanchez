package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Cliente;
import edu.dosw.parcial.core.models.Reserva;
import edu.dosw.parcial.core.models.Servicio;
import edu.dosw.parcial.persistence.entities.ReservaDocument;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ReservaEntityMapper {

    public Reserva toDomain(ReservaDocument doc) {
        if (doc == null) {
            return null;
        }

        Cliente cliente = null;
        if (doc.getCliente() != null) {
            cliente = Cliente.builder()
                    .nombre(doc.getCliente().getNombre())
                    .telefono(doc.getCliente().getTelefono())
                    .correo(doc.getCliente().getCorreo())
                    .build();
        }

        List<Servicio> servicios = new ArrayList<>();
        if (doc.getServicios() != null) {
            servicios = doc.getServicios().stream()
                    .map(s -> Servicio.builder()
                            .codigo(s.getCodigo())
                            .nombre(s.getNombre())
                            .precio(s.getPrecio())
                            .especialidad(s.getEspecialidad())
                            .build())
                    .toList();
        }

        return Reserva.builder()
                .idReserva(doc.getIdReserva())
                .idTurno(doc.getIdTurno())
                .barberoAsignado(doc.getBarberoAsignado())
                .fecha(doc.getFecha())
                .hora(doc.getHora())
                .servicios(servicios)
                .total(doc.getTotal())
                .estado(doc.getEstado())
                .cliente(cliente)
                .build();
    }

    public ReservaDocument toDocument(Reserva domain) {
        if (domain == null) {
            return null;
        }

        ReservaDocument.ClienteEmbebido clienteEmbebido = null;
        if (domain.getCliente() != null) {
            clienteEmbebido = ReservaDocument.ClienteEmbebido.builder()
                    .nombre(domain.getCliente().getNombre())
                    .telefono(domain.getCliente().getTelefono())
                    .correo(domain.getCliente().getCorreo())
                    .build();
        }

        List<ReservaDocument.ServicioEmbebido> serviciosEmbebidos = new ArrayList<>();
        if (domain.getServicios() != null) {
            serviciosEmbebidos = domain.getServicios().stream()
                    .map(s -> ReservaDocument.ServicioEmbebido.builder()
                            .codigo(s.getCodigo())
                            .nombre(s.getNombre())
                            .precio(s.getPrecio())
                            .especialidad(s.getEspecialidad())
                            .build())
                    .toList();
        }

        return ReservaDocument.builder()
                .idReserva(domain.getIdReserva())
                .idTurno(domain.getIdTurno())
                .barberoAsignado(domain.getBarberoAsignado())
                .fecha(domain.getFecha())
                .hora(domain.getHora())
                .servicios(serviciosEmbebidos)
                .total(domain.getTotal())
                .estado(domain.getEstado())
                .cliente(clienteEmbebido)
                .build();
    }
}

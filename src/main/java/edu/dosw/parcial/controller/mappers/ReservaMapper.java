package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.core.models.Cliente;
import edu.dosw.parcial.core.models.Reserva;
import org.springframework.stereotype.Component;

@Component
public class ReservaMapper {

    public Reserva toDomain(ReservaRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        Cliente cliente = null;
        if (dto.getCliente() != null) {
            cliente = Cliente.builder()
                    .nombre(dto.getCliente().getNombre())
                    .telefono(dto.getCliente().getTelefono())
                    .correo(dto.getCliente().getCorreo())
                    .build();
        }

        return Reserva.builder()
                .fecha(dto.getFecha())
                .hora(dto.getHora())
                .barberoAsignado(dto.getBarberoPreferido())
                .cliente(cliente)
                .build();
    }

    public ReservaResponseDTO toResponse(Reserva domain) {
        if (domain == null) {
            return null;
        }
        return ReservaResponseDTO.builder()
                .idReserva(domain.getIdReserva())
                .idTurno(domain.getIdTurno())
                .barberoAsignado(domain.getBarberoAsignado())
                .fecha(domain.getFecha())
                .hora(domain.getHora())
                .total(domain.getTotal())
                .estado(domain.getEstado())
                .build();
    }
}

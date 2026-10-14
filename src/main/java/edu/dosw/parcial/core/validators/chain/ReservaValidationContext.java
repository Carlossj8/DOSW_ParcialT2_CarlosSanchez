package edu.dosw.parcial.core.validators.chain;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservaValidationContext {
    private ReservaRequestDTO request;
    private List<TurnoDocument> turnosDisponibles;
    private BarberoDocument barberoAsignado;
    private TurnoDocument turnoAsignado;
    private List<ServicioDocument> serviciosValidados;
    private Double total;
}

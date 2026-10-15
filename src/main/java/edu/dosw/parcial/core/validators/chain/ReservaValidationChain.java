package edu.dosw.parcial.core.validators.chain;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ReservaValidationChain {

    private final ReservaValidationHandler firstHandler;

    public ReservaValidationChain(
            DisponibilidadFranjaHandler h1,
            BarberoDisponibleHandler h2,
            DatosClienteHandler h3,
            ServicioPermitidoHandler h4,
            AsignacionAutomaticaHandler h5) {

        h1.setNext(h2);
        h2.setNext(h3);
        h3.setNext(h4);
        h4.setNext(h5);
        this.firstHandler = h1;
        log.info("Cadena de 5 validaciones de reserva inicializada con éxito.");
    }

    public void execute(ReservaValidationContext context) {
        log.info("Iniciando ejecución de la cadena de validaciones en orden estricto (1 -> 5)");
        firstHandler.handle(context);
        log.info("Cadena de validaciones completada exitosamente sin rechazos.");
    }
}

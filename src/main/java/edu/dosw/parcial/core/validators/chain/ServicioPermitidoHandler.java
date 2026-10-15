package edu.dosw.parcial.core.validators.chain;

import edu.dosw.parcial.core.utils.BadRequestException;
import edu.dosw.parcial.core.utils.ResourceNotFoundException;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class ServicioPermitidoHandler extends ReservaValidationHandler {

    private final ServicioRepository servicioRepository;

    public ServicioPermitidoHandler(ServicioRepository servicioRepository) {
        this.servicioRepository = servicioRepository;
    }

    @Override
    public void handle(ReservaValidationContext context) {
        List<String> codigos = context.getRequest().getServicios();
        log.info("Paso 4: Validando existencia en catálogo activo de servicios: {}", codigos);

        if (codigos == null || codigos.isEmpty()) {
            log.warn("Rechazo en Paso 4: Lista de servicios nula o vacía");
            throw new BadRequestException("Formato de entrada inválido");
        }

        List<ServicioDocument> serviciosValidados = new ArrayList<>();
        for (String codigo : codigos) {
            Optional<ServicioDocument> servOpt = servicioRepository.findByCodigo(codigo);
            if (servOpt.isEmpty() || !servOpt.get().isActivo()) {
                log.warn("Rechazo en Paso 4 (E09): El servicio '{}' no existe en el catálogo activo", codigo);
                throw new ResourceNotFoundException("Servicio inexistente en el catálogo activo");
            }
            serviciosValidados.add(servOpt.get());
        }

        context.setServiciosValidados(serviciosValidados);
        log.info("Paso 4 superado: Todos los servicios ({}) existen y están activos en el catálogo", serviciosValidados.size());

        next(context);
    }
}

package edu.dosw.parcial.core.validators.chain;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.core.utils.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Slf4j
@Component
public class DatosClienteHandler extends ReservaValidationHandler {

    private static final Pattern TELEFONO_PATTERN = Pattern.compile("^\\d{10}$");
    private static final Pattern CORREO_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@gmail\\.com$");

    @Override
    public void handle(ReservaValidationContext context) {
        log.info("Paso 3: Validando datos del cliente");

        ReservaRequestDTO.ClienteDTO cliente = context.getRequest().getCliente();
        if (cliente == null) {
            log.warn("Rechazo en Paso 3: Objeto cliente es nulo");
            throw new BadRequestException("Formato de entrada inválido");
        }

        if (cliente.getNombre() == null || cliente.getNombre().trim().isEmpty()) {
            log.warn("Rechazo en Paso 3 (E03): Nombre de cliente nulo o vacío");
            throw new BadRequestException("Formato de entrada inválido");
        }

        if (cliente.getTelefono() == null || !TELEFONO_PATTERN.matcher(cliente.getTelefono().trim()).matches()) {
            log.warn("Rechazo en Paso 3 (E07): Teléfono '{}' no contiene exactamente 10 dígitos numéricos", cliente.getTelefono());
            throw new BadRequestException("Formato de entrada inválido");
        }

        if (cliente.getCorreo() == null || !CORREO_PATTERN.matcher(cliente.getCorreo().trim()).matches()) {
            log.warn("Rechazo en Paso 3 (E08): Correo '{}' inválido o no pertenece al dominio @gmail.com", cliente.getCorreo());
            throw new BadRequestException("Formato de entrada inválido");
        }

        log.info("Paso 3 superado: Datos del cliente validados correctamente para {}", cliente.getCorreo());
        next(context);
    }
}

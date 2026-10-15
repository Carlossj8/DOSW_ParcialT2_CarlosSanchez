package edu.dosw.parcial.core.validators.chain;

import edu.dosw.parcial.core.utils.ConflictException;
import edu.dosw.parcial.core.utils.UnprocessableEntityException;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
public class BarberoDisponibleHandler extends ReservaValidationHandler {

    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;

    public BarberoDisponibleHandler(BarberoRepository barberoRepository, ServicioRepository servicioRepository) {
        this.barberoRepository = barberoRepository;
        this.servicioRepository = servicioRepository;
    }

    @Override
    public void handle(ReservaValidationContext context) {
        String barberoPreferido = context.getRequest().getBarberoPreferido();
        List<String> codigosServicios = context.getRequest().getServicios();
        List<TurnoDocument> turnosDisponibles = context.getTurnosDisponibles();

        log.info("Paso 2: Validando disponibilidad y especialidades del barbero (preferencia: '{}')", barberoPreferido);

        List<String> especialidadesRequeridas = obtenerEspecialidadesRequeridas(codigosServicios);

        boolean esSinPreferencia = barberoPreferido == null ||
                barberoPreferido.trim().isEmpty() ||
                "Sin preferencia".equalsIgnoreCase(barberoPreferido.trim());

        if (!esSinPreferencia) {
            String pref = barberoPreferido.trim();
            Optional<BarberoDocument> barberoOpt = barberoRepository.findById(pref);
            if (barberoOpt.isEmpty()) {
                barberoOpt = barberoRepository.findByNombreIgnoreCase(pref);
            }

            if (barberoOpt.isEmpty()) {
                log.warn("Rechazo en Paso 2: El barbero solicitado '{}' no existe en el catálogo", pref);
                throw new ConflictException("Franja horaria está llena o barbero no disponible");
            }

            BarberoDocument barbero = barberoOpt.get();

            Optional<TurnoDocument> turnoOpt = turnosDisponibles.stream()
                    .filter(t -> t.getBarberoId().equals(barbero.getId()) ||
                            t.getBarberoNombre().equalsIgnoreCase(barbero.getNombre()))
                    .findFirst();

            if (turnoOpt.isEmpty()) {
                log.warn("Rechazo en Paso 2: El barbero '{}' no tiene turno DISPONIBLE en la franja solicitada", barbero.getNombre());
                throw new ConflictException("Franja horaria está llena o barbero no disponible");
            }

            if (!barberoCubreEspecialidades(barbero, especialidadesRequeridas)) {
                log.warn("Rechazo en Paso 2: El barbero '{}' no cubre las especialidades requeridas: {}",
                        barbero.getNombre(), especialidadesRequeridas);
                throw new UnprocessableEntityException("Ningun barbero cubre los servicios");
            }

            context.setBarberoAsignado(barbero);
            context.setTurnoAsignado(turnoOpt.get());
            log.info("Paso 2 superado: Barbero preferido '{}' disponible y con especialidades compatibles", barbero.getNombre());
        } else {
            boolean hayBarberoCapaz = false;
            for (TurnoDocument turno : turnosDisponibles) {
                Optional<BarberoDocument> bOpt = barberoRepository.findById(turno.getBarberoId());
                if (bOpt.isEmpty()) {
                    bOpt = barberoRepository.findByNombreIgnoreCase(turno.getBarberoNombre());
                }
                if (bOpt.isPresent() && barberoCubreEspecialidades(bOpt.get(), especialidadesRequeridas)) {
                    hayBarberoCapaz = true;
                    break;
                }
            }

            if (!hayBarberoCapaz) {
                log.warn("Rechazo en Paso 2: Ningún barbero libre en la franja cubre todas las especialidades requeridas: {}", especialidadesRequeridas);
                throw new UnprocessableEntityException("Ningun barbero cubre los servicios");
            }

            log.info("Paso 2 superado: Al menos un barbero disponible cubre las especialidades requeridas");
        }

        next(context);
    }

    private List<String> obtenerEspecialidadesRequeridas(List<String> codigos) {
        List<String> especialidades = new ArrayList<>();
        if (codigos != null) {
            for (String codigo : codigos) {
                servicioRepository.findByCodigo(codigo).ifPresent(s -> {
                    if (s.getEspecialidad() != null && !especialidades.contains(s.getEspecialidad())) {
                        especialidades.add(s.getEspecialidad());
                    }
                });
            }
        }
        return especialidades;
    }

    private boolean barberoCubreEspecialidades(BarberoDocument barbero, List<String> especialidadesRequeridas) {
        if (barbero.getEspecialidades() == null) {
            return especialidadesRequeridas.isEmpty();
        }
        List<String> barberoEsp = barbero.getEspecialidades().stream()
                .map(s -> s.toUpperCase(Locale.ROOT))
                .toList();

        for (String req : especialidadesRequeridas) {
            if (!barberoEsp.contains(req.toUpperCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }
}

package edu.dosw.parcial.core.validators.chain;

import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Slf4j
@Component
public class AsignacionAutomaticaHandler extends ReservaValidationHandler {

    private final BarberoRepository barberoRepository;

    public AsignacionAutomaticaHandler(BarberoRepository barberoRepository) {
        this.barberoRepository = barberoRepository;
    }

    @Override
    public void handle(ReservaValidationContext context) {
        log.info("Paso 5: Asignación automática de barbero/turno y cálculo de total");

        List<ServicioDocument> servicios = context.getServiciosValidados();
        List<String> especialidadesRequeridas = servicios.stream()
                .map(ServicioDocument::getEspecialidad)
                .filter(e -> e != null && !e.isBlank())
                .distinct()
                .toList();

        if (context.getBarberoAsignado() == null) {
            for (TurnoDocument turno : context.getTurnosDisponibles()) {
                Optional<BarberoDocument> bOpt = barberoRepository.findById(turno.getBarberoId());
                if (bOpt.isEmpty()) {
                    bOpt = barberoRepository.findByNombreIgnoreCase(turno.getBarberoNombre());
                }
                if (bOpt.isPresent() && barberoCubreEspecialidades(bOpt.get(), especialidadesRequeridas)) {
                    context.setBarberoAsignado(bOpt.get());
                    context.setTurnoAsignado(turno);
                    log.info("Barbero asignado automáticamente: {} con turno {}", bOpt.get().getNombre(), turno.getTurnoId());
                    break;
                }
            }
        } else if (context.getTurnoAsignado() == null) {
            TurnoDocument turno = context.getTurnosDisponibles().stream()
                    .filter(t -> t.getBarberoId().equals(context.getBarberoAsignado().getId()) ||
                            t.getBarberoNombre().equalsIgnoreCase(context.getBarberoAsignado().getNombre()))
                    .findFirst()
                    .orElse(null);
            context.setTurnoAsignado(turno);
        }

        double total = servicios.stream()
                .mapToDouble(s -> s.getPrecio() != null ? s.getPrecio() : 0.0)
                .sum();
        context.setTotal(total);

        log.info("Paso 5 superado exitosamente: Barbero={}, Turno={}, Total={}",
                context.getBarberoAsignado() != null ? context.getBarberoAsignado().getNombre() : "N/A",
                context.getTurnoAsignado() != null ? context.getTurnoAsignado().getTurnoId() : "N/A",
                total);

        next(context);
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

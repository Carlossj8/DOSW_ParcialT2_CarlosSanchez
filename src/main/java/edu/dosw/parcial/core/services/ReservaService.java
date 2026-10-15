package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.core.validators.chain.ReservaValidationChain;
import edu.dosw.parcial.core.validators.chain.ReservaValidationContext;
import edu.dosw.parcial.persistence.entities.ReservaDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.repositories.ReservaRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReservaService {

    private final ReservaValidationChain validationChain;
    private final ReservaRepository reservaRepository;
    private final TurnoRepository turnoRepository;

    public ReservaService(
            ReservaValidationChain validationChain,
            ReservaRepository reservaRepository,
            TurnoRepository turnoRepository) {
        this.validationChain = validationChain;
        this.reservaRepository = reservaRepository;
        this.turnoRepository = turnoRepository;
    }

    public ReservaResponseDTO crearReserva(ReservaRequestDTO request) {
        String correoCliente = request.getCliente() != null ? request.getCliente().getCorreo() : "N/A";
        log.info("Iniciando solicitud de reserva para fecha: {}, hora: {}, cliente: {}",
                request.getFecha(), request.getHora(), correoCliente);

        ReservaValidationContext context = ReservaValidationContext.builder()
                .request(request)
                .build();

        validationChain.execute(context);

        TurnoDocument turno = context.getTurnoAsignado();
        turno.setEstado("OCUPADO");
        turnoRepository.save(turno);
        log.info("Turno {} marcado como OCUPADO en base de datos", turno.getTurnoId());

        long count = reservaRepository.count();
        String idReserva = String.format("R%02d", count + 1);

        List<ReservaDocument.ServicioEmbebido> serviciosEmbebidos = context.getServiciosValidados().stream()
                .map(s -> ReservaDocument.ServicioEmbebido.builder()
                        .codigo(s.getCodigo())
                        .nombre(s.getNombre())
                        .precio(s.getPrecio())
                        .especialidad(s.getEspecialidad())
                        .build())
                .toList();

        ReservaDocument.ClienteEmbebido clienteEmbebido = ReservaDocument.ClienteEmbebido.builder()
                .nombre(request.getCliente().getNombre())
                .telefono(request.getCliente().getTelefono())
                .correo(request.getCliente().getCorreo())
                .build();

        ReservaDocument reservaDoc = ReservaDocument.builder()
                .idReserva(idReserva)
                .idTurno(turno.getTurnoId())
                .barberoAsignado(context.getBarberoAsignado().getNombre())
                .fecha(request.getFecha())
                .hora(request.getHora())
                .servicios(serviciosEmbebidos)
                .total(context.getTotal())
                .estado("CONFIRMADA")
                .cliente(clienteEmbebido)
                .build();

        reservaRepository.save(reservaDoc);

        log.info("Reserva {} guardada exitosamente en estado CONFIRMADA para turno {}", idReserva, turno.getTurnoId());

        List<ReservaResponseDTO.ServicioDTO> serviciosDTO = serviciosEmbebidos.stream()
                .map(s -> ReservaResponseDTO.ServicioDTO.builder()
                        .codigo(s.getCodigo())
                        .nombre(s.getNombre())
                        .precio(s.getPrecio())
                        .especialidad(s.getEspecialidad())
                        .build())
                .toList();

        ReservaResponseDTO.ClienteDTO clienteDTO = ReservaResponseDTO.ClienteDTO.builder()
                .nombre(clienteEmbebido.getNombre())
                .telefono(clienteEmbebido.getTelefono())
                .correo(clienteEmbebido.getCorreo())
                .build();

        return ReservaResponseDTO.builder()
                .idReserva(idReserva)
                .idTurno(turno.getTurnoId())
                .barberoAsignado(context.getBarberoAsignado().getNombre())
                .fecha(request.getFecha())
                .hora(request.getHora())
                .servicios(serviciosDTO)
                .total(context.getTotal())
                .estado("CONFIRMADA")
                .cliente(clienteDTO)
                .build();
    }

    public List<ReservaResponseDTO> obtenerMisReservas(String correoCliente) {
        log.info("Consultando reservas del cliente: {}", correoCliente);
        List<ReservaDocument> reservas = reservaRepository.findByClienteCorreoOrderByFechaDescHoraDesc(correoCliente);
        return reservas.stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<ReservaResponseDTO> obtenerTodasLasReservas() {
        log.info("Consultando todas las reservas del sistema (ADMIN)");
        List<ReservaDocument> reservas = reservaRepository.findAllByOrderByFechaDescHoraDesc();
        return reservas.stream()
                .map(this::mapToDTO)
                .toList();
    }

    private ReservaResponseDTO mapToDTO(ReservaDocument doc) {
        List<ReservaResponseDTO.ServicioDTO> serviciosDTO = doc.getServicios() != null
                ? doc.getServicios().stream()
                .map(s -> ReservaResponseDTO.ServicioDTO.builder()
                        .codigo(s.getCodigo())
                        .nombre(s.getNombre())
                        .precio(s.getPrecio())
                        .especialidad(s.getEspecialidad())
                        .build())
                .toList()
                : List.of();

        ReservaResponseDTO.ClienteDTO clienteDTO = doc.getCliente() != null
                ? ReservaResponseDTO.ClienteDTO.builder()
                .nombre(doc.getCliente().getNombre())
                .telefono(doc.getCliente().getTelefono())
                .correo(doc.getCliente().getCorreo())
                .build()
                : null;

        return ReservaResponseDTO.builder()
                .idReserva(doc.getIdReserva())
                .idTurno(doc.getIdTurno())
                .barberoAsignado(doc.getBarberoAsignado())
                .fecha(doc.getFecha())
                .hora(doc.getHora())
                .servicios(serviciosDTO)
                .total(doc.getTotal())
                .estado(doc.getEstado())
                .cliente(clienteDTO)
                .build();
    }
}

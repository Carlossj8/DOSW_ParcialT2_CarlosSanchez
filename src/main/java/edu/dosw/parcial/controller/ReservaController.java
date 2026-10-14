package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.core.services.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/reservas")
@Tag(name = "Reservas", description = "Endpoints de gestión y creación de reservas en Bob's Barber")
public class ReservaController {

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @Operation(
        summary = "Reservar un turno",
        description = "Permite únicamente al rol CLIENTE reservar un turno ejecutando la cadena de 5 validaciones en orden estricto",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Reserva confirmada"),
        @ApiResponse(responseCode = "400", description = "Formato de entrada inválido"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos suficientes. Debe tener rol CLIENTE"),
        @ApiResponse(responseCode = "404", description = "Servicio inexistente en el catálogo activo"),
        @ApiResponse(responseCode = "409", description = "Franja horaria está llena o barbero no disponible"),
        @ApiResponse(responseCode = "422", description = "Ningun barbero cubre los servicios")
    })
    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ReservaResponseDTO> crearReserva(@Valid @RequestBody ReservaRequestDTO request) {
        log.info("Petición recibida en POST /api/v1/reservas: fecha={}, hora={}, cliente={}",
                request.getFecha(), request.getHora(), request.getCliente() != null ? request.getCliente().getCorreo() : "N/A");
        ReservaResponseDTO response = reservaService.crearReserva(request);
        log.info("Reserva procesada exitosamente con ID: {}", response.getIdReserva());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.TurnoRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
import edu.dosw.parcial.core.services.ReservaService;
import edu.dosw.parcial.core.services.TurnoService;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/turnos")
@Tag(name = "Turnos", description = "Endpoints de gestión y publicación de turnos en Bob's Barber")
public class TurnoController {

    private final TurnoService turnoService;
    private final ReservaService reservaService;

    public TurnoController(TurnoService turnoService, ReservaService reservaService) {
        this.turnoService = turnoService;
        this.reservaService = reservaService;
    }

    @Operation(
        summary = "Crear turno disponible",
        description = "Permite únicamente al rol ADMIN publicar un turno disponible para un barbero",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Turno creado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Formato de entrada inválido"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos. Debe tener rol ADMIN"),
        @ApiResponse(responseCode = "404", description = "El barbero no existe en la base de datos"),
        @ApiResponse(responseCode = "409", description = "Ya existe un turno publicado para este barbero en esa fecha y hora"),
        @ApiResponse(responseCode = "422", description = "Fecha fuera de los dias u horas de atención del barbero")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<TurnoResponseDTO> crearTurno(@Valid @RequestBody TurnoRequestDTO request) {
        log.info("Petición recibida en POST /api/v1/turnos: barberoId={}, fecha={}, hora={}",
                request.getBarberoId(), request.getFecha(), request.getHora());
        TurnoResponseDTO response = turnoService.crearTurno(request);
        log.info("Turno generado con éxito con ID: {}", response.getTurnoId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
        summary = "Ver todos los turnos realizados",
        description = "Permite únicamente al rol ADMIN ver todas las reservas y turnos realizados en el sistema",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de turnos y reservas obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas o no autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos suficientes. Debe tener rol ADMIN")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerTodosLosTurnos() {
        log.info("Petición recibida en GET /api/v1/turnos (ADMIN)");
        List<ReservaResponseDTO> turnos = reservaService.obtenerTodasLasReservas();
        return ResponseEntity.ok(turnos);
    }

    @Operation(
        summary = "Ver mis turnos realizados",
        description = "Permite al cliente autenticado ver el historial de sus reservas. La identidad se toma directamente del token JWT.",
        security = @SecurityRequirement(name = "Bearer Authentication")
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Historial de turnos obtenido exitosamente"),
        @ApiResponse(responseCode = "401", description = "Credenciales inválidas o no autenticado"),
        @ApiResponse(responseCode = "403", description = "No tiene permisos suficientes. Debe tener rol CLIENTE")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<List<ReservaResponseDTO>> obtenerMisTurnosPorId(
            @PathVariable(value = "id", required = false) String id,
            Authentication authentication) {
        String correo = authentication != null ? authentication.getName() : null;
        log.info("Petición recibida en GET /api/v1/turnos/{} para cliente autenticado: {}", id, correo);
        List<ReservaResponseDTO> misTurnos = reservaService.obtenerMisReservas(correo);
        return ResponseEntity.ok(misTurnos);
    }
}

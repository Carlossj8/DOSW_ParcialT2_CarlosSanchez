package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.TurnoRequestDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/turnos")
@Tag(name = "Turnos", description = "Endpoints de gestión y publicación de turnos en Bob's Barber")
public class TurnoController {

    private final TurnoService turnoService;

    public TurnoController(TurnoService turnoService) {
        this.turnoService = turnoService;
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
}

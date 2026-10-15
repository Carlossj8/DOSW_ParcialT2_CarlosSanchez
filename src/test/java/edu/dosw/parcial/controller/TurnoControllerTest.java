package edu.dosw.parcial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.parcial.controller.dtos.request.TurnoRequestDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
import edu.dosw.parcial.controller.handlers.GlobalExceptionHandler;
import edu.dosw.parcial.core.services.TurnoService;
import edu.dosw.parcial.core.utils.ConflictException;
import edu.dosw.parcial.core.utils.ResourceNotFoundException;
import edu.dosw.parcial.core.utils.UnprocessableEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.core.services.ReservaService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class TurnoControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TurnoService turnoService;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private TurnoController turnoController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(turnoController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/turnos debe retornar 201 Created cuando los datos son válidos")
    void crearTurno_debeRetornar201_cuandoDatosSonValidos() throws Exception {
        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha("2026-11-12")
                .hora("10:00")
                .build();

        TurnoResponseDTO response = TurnoResponseDTO.builder()
                .turnoId("T01")
                .barberoId("01")
                .barberoNombre("Miguel")
                .fecha("2026-11-12")
                .hora("10:00")
                .estado("DISPONIBLE")
                .build();

        when(turnoService.crearTurno(any(TurnoRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.turnoId").value("T01"))
                .andExpect(jsonPath("$.barberoId").value("01"))
                .andExpect(jsonPath("$.barberoNombre").value("Miguel"))
                .andExpect(jsonPath("$.fecha").value("2026-11-12"))
                .andExpect(jsonPath("$.hora").value("10:00"))
                .andExpect(jsonPath("$.estado").value("DISPONIBLE"));
    }

    @Test
    @DisplayName("POST /api/v1/turnos debe retornar 400 Bad Request cuando el formato de fecha es incorrecto")
    void crearTurno_debeRetornar400_cuandoFormatoFechaEsIncorrecto() throws Exception {
        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha("12-11-2026")
                .hora("10:00")
                .build();

        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Campos vacíos o formato de entrada incorrecto"));
    }

    @Test
    @DisplayName("POST /api/v1/turnos debe retornar 404 Not Found cuando el barbero no existe")
    void crearTurno_debeRetornar404_cuandoBarberoNoExiste() throws Exception {
        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("99")
                .fecha("2026-11-12")
                .hora("10:00")
                .build();

        when(turnoService.crearTurno(any(TurnoRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("El barbero no existe en la base de datos"));

        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("El barbero no existe en la base de datos"));
    }

    @Test
    @DisplayName("POST /api/v1/turnos debe retornar 409 Conflict cuando el turno ya existe para ese barbero")
    void crearTurno_debeRetornar409_cuandoTurnoEstaDuplicado() throws Exception {
        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha("2026-11-12")
                .hora("10:00")
                .build();

        when(turnoService.crearTurno(any(TurnoRequestDTO.class)))
                .thenThrow(new ConflictException("Ya existe un turno publicado para este barbero en esa fecha y hora"));

        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("Ya existe un turno publicado para este barbero en esa fecha y hora"));
    }

    @Test
    @DisplayName("POST /api/v1/turnos debe retornar 422 Unprocessable cuando la fecha u hora está fuera de disponibilidad")
    void crearTurno_debeRetornar422_cuandoFechaFueraDeDisponibilidad() throws Exception {
        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha("2026-11-13")
                .hora("10:00")
                .build();

        when(turnoService.crearTurno(any(TurnoRequestDTO.class)))
                .thenThrow(new UnprocessableEntityException("Fecha fuera de los dias u horas de atención del barbero"));

        mockMvc.perform(post("/api/v1/turnos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensaje").value("Fecha fuera de los dias u horas de atención del barbero"));
    }

    @Test
    @DisplayName("GET /api/v1/turnos debe retornar 200 OK con la lista de todas las reservas para ADMIN")
    void obtenerTodosLosTurnos_debeRetornar200_conListaDeReservas() throws Exception {
        ReservaResponseDTO reserva = ReservaResponseDTO.builder()
                .idReserva("R01")
                .idTurno("T01")
                .barberoAsignado("Laura")
                .fecha("2026-11-12")
                .hora("10:00")
                .total(18000.0)
                .estado("CONFIRMADA")
                .build();

        when(reservaService.obtenerTodasLasReservas()).thenReturn(List.of(reserva));

        mockMvc.perform(get("/api/v1/turnos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value("R01"))
                .andExpect(jsonPath("$[0].idTurno").value("T01"))
                .andExpect(jsonPath("$[0].barberoAsignado").value("Laura"))
                .andExpect(jsonPath("$[0].fecha").value("2026-11-12"))
                .andExpect(jsonPath("$[0].hora").value("10:00"))
                .andExpect(jsonPath("$[0].total").value(18000.0))
                .andExpect(jsonPath("$[0].estado").value("CONFIRMADA"));
    }

    @Test
    @DisplayName("GET /api/v1/turnos/{id} debe retornar 200 OK con la lista de reservas del cliente autenticado")
    void obtenerMisTurnosPorId_debeRetornar200_conListaDeReservasDelCliente() throws Exception {
        String correo = "carlos.ruiz@gmail.com";
        ReservaResponseDTO reserva = ReservaResponseDTO.builder()
                .idReserva("R01")
                .idTurno("T01")
                .barberoAsignado("Laura")
                .fecha("2026-11-12")
                .hora("10:00")
                .total(18000.0)
                .estado("CONFIRMADA")
                .build();

        when(reservaService.obtenerMisReservas(correo)).thenReturn(List.of(reserva));

        mockMvc.perform(get("/api/v1/turnos/T01")
                        .principal(new UsernamePasswordAuthenticationToken(correo, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value("R01"))
                .andExpect(jsonPath("$[0].idTurno").value("T01"))
                .andExpect(jsonPath("$[0].barberoAsignado").value("Laura"))
                .andExpect(jsonPath("$[0].fecha").value("2026-11-12"))
                .andExpect(jsonPath("$[0].hora").value("10:00"));
    }
}

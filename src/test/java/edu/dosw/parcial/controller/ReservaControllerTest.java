package edu.dosw.parcial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.controller.handlers.GlobalExceptionHandler;
import edu.dosw.parcial.core.services.ReservaService;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

@ExtendWith(MockitoExtension.class)
class ReservaControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ReservaService reservaService;

    @InjectMocks
    private ReservaController reservaController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(reservaController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/reservas debe retornar 201 Created cuando los datos son válidos")
    void crearReserva_debeRetornar201_cuandoDatosSonValidos() throws Exception {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Sin preferencia")
                .servicios(List.of("SER-001", "SER-004"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        ReservaResponseDTO response = ReservaResponseDTO.builder()
                .idReserva("R01")
                .idTurno("T01")
                .barberoAsignado("Andrés")
                .fecha("2026-11-12")
                .hora("10:00")
                .total(45000.0)
                .estado("CONFIRMADA")
                .build();

        when(reservaService.crearReserva(any(ReservaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idReserva").value("R01"))
                .andExpect(jsonPath("$.idTurno").value("T01"))
                .andExpect(jsonPath("$.barberoAsignado").value("Andrés"))
                .andExpect(jsonPath("$.total").value(45000.0))
                .andExpect(jsonPath("$.estado").value("CONFIRMADA"));
    }

    @Test
    @DisplayName("POST /api/v1/reservas debe retornar 400 Bad Request cuando faltan campos o formato incorrecto")
    void crearReserva_debeRetornar400_cuandoFaltanCampos() throws Exception {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Sin preferencia")
                .servicios(List.of())
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("")
                        .telefono("123")
                        .correo("invalido@hotmail.com")
                        .build())
                .build();

        mockMvc.perform(post("/api/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.detalles").exists());
    }

    @Test
    @DisplayName("POST /api/v1/reservas debe retornar 404 Not Found cuando un servicio no existe en el catálogo")
    void crearReserva_debeRetornar404_cuandoServicioNoExiste() throws Exception {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Andrés")
                .servicios(List.of("SER-999"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        when(reservaService.crearReserva(any(ReservaRequestDTO.class)))
                .thenThrow(new ResourceNotFoundException("Servicio inexistente en el catálogo activo"));

        mockMvc.perform(post("/api/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.mensaje").value("Servicio inexistente en el catálogo activo"));
    }

    @Test
    @DisplayName("POST /api/v1/reservas debe retornar 409 Conflict cuando la franja horaria está llena")
    void crearReserva_debeRetornar409_cuandoFranjaEstaLlena() throws Exception {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Sin preferencia")
                .servicios(List.of("SER-001"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        when(reservaService.crearReserva(any(ReservaRequestDTO.class)))
                .thenThrow(new ConflictException("Franja horaria está llena o barbero no disponible"));

        mockMvc.perform(post("/api/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.mensaje").value("Franja horaria está llena o barbero no disponible"));
    }

    @Test
    @DisplayName("POST /api/v1/reservas debe retornar 422 Unprocessable cuando ningún barbero cubre los servicios")
    void crearReserva_debeRetornar422_cuandoNingunBarberoCubreServicios() throws Exception {
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Miguel")
                .servicios(List.of("SER-003"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        when(reservaService.crearReserva(any(ReservaRequestDTO.class)))
                .thenThrow(new UnprocessableEntityException("Ningun barbero cubre los servicios"));

        mockMvc.perform(post("/api/v1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.mensaje").value("Ningun barbero cubre los servicios"));
    }

    @Test
    @DisplayName("GET /api/v1/reservas/mis-reservas debe retornar 200 OK con el historial del cliente")
    void obtenerMisReservas_debeRetornar200_conHistorial() throws Exception {
        String correo = "carlos.ruiz@gmail.com";
        ReservaResponseDTO reserva = ReservaResponseDTO.builder()
                .idReserva("R01")
                .idTurno("T01")
                .barberoAsignado("Andrés")
                .fecha("2026-11-12")
                .hora("10:00")
                .total(45000.0)
                .estado("CONFIRMADA")
                .build();

        when(reservaService.obtenerMisReservas(correo)).thenReturn(List.of(reserva));

        mockMvc.perform(get("/api/v1/reservas/mis-reservas")
                        .principal(new UsernamePasswordAuthenticationToken(correo, null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value("R01"))
                .andExpect(jsonPath("$[0].idTurno").value("T01"))
                .andExpect(jsonPath("$[0].barberoAsignado").value("Andrés"))
                .andExpect(jsonPath("$[0].total").value(45000.0));
    }

    @Test
    @DisplayName("GET /api/v1/reservas debe retornar 200 OK con todas las reservas para ADMIN")
    void obtenerTodasLasReservas_debeRetornar200_conListaCompleta() throws Exception {
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

        mockMvc.perform(get("/api/v1/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idReserva").value("R01"))
                .andExpect(jsonPath("$[0].idTurno").value("T01"))
                .andExpect(jsonPath("$[0].barberoAsignado").value("Laura"));
    }
}

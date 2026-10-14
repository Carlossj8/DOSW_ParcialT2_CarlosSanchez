package edu.dosw.parcial.core;

import edu.dosw.parcial.controller.dtos.request.TurnoRequestDTO;
import edu.dosw.parcial.controller.dtos.response.TurnoResponseDTO;
import edu.dosw.parcial.controller.mappers.TurnoMapper;
import edu.dosw.parcial.core.models.Barbero;
import edu.dosw.parcial.core.services.TurnoService;
import edu.dosw.parcial.core.utils.ConflictException;
import edu.dosw.parcial.core.utils.ResourceNotFoundException;
import edu.dosw.parcial.core.utils.UnprocessableEntityException;
import edu.dosw.parcial.core.validators.TurnoValidator;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.mappers.BarberoEntityMapper;
import edu.dosw.parcial.persistence.mappers.TurnoEntityMapper;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TurnoServiceTest {

    @Mock
    private BarberoRepository barberoRepository;

    @Spy
    private BarberoEntityMapper barberoEntityMapper;

    @Mock
    private TurnoRepository turnoRepository;

    @Spy
    private TurnoEntityMapper turnoEntityMapper;

    private TurnoValidator turnoValidator;

    @Spy
    private TurnoMapper turnoMapper;

    private TurnoService turnoService;

    @BeforeEach
    void setUp() {
        turnoValidator = new TurnoValidator(turnoRepository);
        turnoService = new TurnoService(
                barberoRepository,
                barberoEntityMapper,
                turnoRepository,
                turnoEntityMapper,
                turnoValidator,
                turnoMapper
        );
    }

    @Test
    @DisplayName("Debe crear turno exitosamente cuando los datos son válidos")
    void crearTurno_debeRetornarTurnoCreado_cuandoDatosSonValidos() {
        LocalDate fechaFutura = LocalDate.now().plusWeeks(1);
        while (fechaFutura.getDayOfWeek().getValue() != 1) {
            fechaFutura = fechaFutura.plusDays(1);
        }
        String fechaStr = fechaFutura.toString();

        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha(fechaStr)
                .hora("10:00")
                .build();

        BarberoDocument barberoDoc = BarberoDocument.builder()
                .id("01")
                .nombre("Miguel")
                .especialidades(List.of("CORTE", "CORTE CON BARBA"))
                .diasDisponibles(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES"))
                .horaInicio("10:00")
                .horaFin("17:00")
                .build();

        TurnoDocument turnoGuardado = TurnoDocument.builder()
                .id("mongo-id-1")
                .turnoId("T01")
                .barberoId("01")
                .barberoNombre("Miguel")
                .fecha(fechaStr)
                .hora("10:00")
                .estado("DISPONIBLE")
                .build();

        when(barberoRepository.findById("01")).thenReturn(Optional.of(barberoDoc));
        when(turnoRepository.findByBarberoIdAndFechaAndHora("01", fechaStr, "10:00")).thenReturn(Optional.empty());
        when(turnoRepository.count()).thenReturn(0L);
        when(turnoRepository.save(any(TurnoDocument.class))).thenReturn(turnoGuardado);

        TurnoResponseDTO response = turnoService.crearTurno(request);

        assertNotNull(response);
        assertEquals("T01", response.getTurnoId());
        assertEquals("01", response.getBarberoId());
        assertEquals("Miguel", response.getBarberoNombre());
        assertEquals(fechaStr, response.getFecha());
        assertEquals("10:00", response.getHora());
        assertEquals("DISPONIBLE", response.getEstado());
        verify(turnoRepository).save(any(TurnoDocument.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el barbero no existe")
    void crearTurno_debeLanzarResourceNotFound_cuandoBarberoNoExiste() {
        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("99")
                .fecha("2026-11-20")
                .hora("10:00")
                .build();

        when(barberoRepository.findById("99")).thenReturn(Optional.empty());
        when(barberoRepository.findByNombreIgnoreCase("99")).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> turnoService.crearTurno(request)
        );

        assertEquals("El barbero no existe en la base de datos", exception.getMessage());
        verify(turnoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar ConflictException cuando ya existe un turno en la misma fecha y hora para el barbero")
    void crearTurno_debeLanzarConflict_cuandoTurnoYaExiste() {
        LocalDate fechaFutura = LocalDate.now().plusWeeks(1);
        while (fechaFutura.getDayOfWeek().getValue() != 1) {
            fechaFutura = fechaFutura.plusDays(1);
        }
        String fechaStr = fechaFutura.toString();

        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha(fechaStr)
                .hora("10:00")
                .build();

        BarberoDocument barberoDoc = BarberoDocument.builder()
                .id("01")
                .nombre("Miguel")
                .diasDisponibles(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES"))
                .horaInicio("10:00")
                .horaFin("17:00")
                .build();

        when(barberoRepository.findById("01")).thenReturn(Optional.of(barberoDoc));
        when(turnoRepository.findByBarberoIdAndFechaAndHora("01", fechaStr, "10:00"))
                .thenReturn(Optional.of(TurnoDocument.builder().build()));

        ConflictException exception = assertThrows(
                ConflictException.class,
                () -> turnoService.crearTurno(request)
        );

        assertEquals("Ya existe un turno publicado para este barbero en esa fecha y hora", exception.getMessage());
        verify(turnoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar UnprocessableEntityException cuando la fecha cae en un día no disponible para el barbero")
    void crearTurno_debeLanzarUnprocessable_cuandoDiaNoDisponible() {
        LocalDate fechaViernes = LocalDate.now().plusWeeks(1);
        while (fechaViernes.getDayOfWeek().getValue() != 5) {
            fechaViernes = fechaViernes.plusDays(1);
        }
        String fechaStr = fechaViernes.toString();

        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha(fechaStr)
                .hora("10:00")
                .build();

        BarberoDocument barberoDoc = BarberoDocument.builder()
                .id("01")
                .nombre("Miguel")
                .diasDisponibles(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES"))
                .horaInicio("10:00")
                .horaFin("17:00")
                .build();

        when(barberoRepository.findById("01")).thenReturn(Optional.of(barberoDoc));

        UnprocessableEntityException exception = assertThrows(
                UnprocessableEntityException.class,
                () -> turnoService.crearTurno(request)
        );

        assertEquals("Fecha fuera de los dias u horas de atención del barbero", exception.getMessage());
        verify(turnoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar UnprocessableEntityException cuando la hora está fuera del horario del barbero")
    void crearTurno_debeLanzarUnprocessable_cuandoHoraFueraDeHorario() {
        LocalDate fechaLunes = LocalDate.now().plusWeeks(1);
        while (fechaLunes.getDayOfWeek().getValue() != 1) {
            fechaLunes = fechaLunes.plusDays(1);
        }
        String fechaStr = fechaLunes.toString();

        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha(fechaStr)
                .hora("08:00")
                .build();

        BarberoDocument barberoDoc = BarberoDocument.builder()
                .id("01")
                .nombre("Miguel")
                .diasDisponibles(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES"))
                .horaInicio("10:00")
                .horaFin("17:00")
                .build();

        when(barberoRepository.findById("01")).thenReturn(Optional.of(barberoDoc));

        UnprocessableEntityException exception = assertThrows(
                UnprocessableEntityException.class,
                () -> turnoService.crearTurno(request)
        );

        assertEquals("Fecha fuera de los dias u horas de atención del barbero", exception.getMessage());
        verify(turnoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar UnprocessableEntityException cuando la fecha es pasada")
    void crearTurno_debeLanzarUnprocessable_cuandoFechaEsPasada() {
        String fechaPasada = LocalDate.now().minusDays(1).toString();

        TurnoRequestDTO request = TurnoRequestDTO.builder()
                .barberoId("01")
                .fecha(fechaPasada)
                .hora("10:00")
                .build();

        BarberoDocument barberoDoc = BarberoDocument.builder()
                .id("01")
                .nombre("Miguel")
                .diasDisponibles(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES"))
                .horaInicio("10:00")
                .horaFin("17:00")
                .build();

        when(barberoRepository.findById("01")).thenReturn(Optional.of(barberoDoc));

        UnprocessableEntityException exception = assertThrows(
                UnprocessableEntityException.class,
                () -> turnoService.crearTurno(request)
        );

        assertEquals("Fecha fuera de los dias u horas de atención del barbero", exception.getMessage());
        verify(turnoRepository, never()).save(any());
    }
}

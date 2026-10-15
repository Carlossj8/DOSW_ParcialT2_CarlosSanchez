package edu.dosw.parcial.core;

import edu.dosw.parcial.controller.dtos.request.ReservaRequestDTO;
import edu.dosw.parcial.controller.dtos.response.ReservaResponseDTO;
import edu.dosw.parcial.core.services.ReservaService;
import edu.dosw.parcial.core.utils.BadRequestException;
import edu.dosw.parcial.core.utils.ConflictException;
import edu.dosw.parcial.core.utils.ResourceNotFoundException;
import edu.dosw.parcial.core.utils.UnprocessableEntityException;
import edu.dosw.parcial.core.validators.chain.*;
import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ReservaDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.TurnoDocument;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.ReservaRepository;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import edu.dosw.parcial.persistence.repositories.TurnoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservaServiceTest {

    @Mock
    private ReservaRepository reservaRepository;

    @Mock
    private TurnoRepository turnoRepository;

    @Mock
    private BarberoRepository barberoRepository;

    @Mock
    private ServicioRepository servicioRepository;

    private ReservaService reservaService;

    private BarberoDocument barberoAndres;
    private BarberoDocument barberoMiguel;
    private ServicioDocument servicioCorte;
    private ServicioDocument servicioBarba;
    private ServicioDocument servicioCejas;
    private TurnoDocument turnoAndres;

    @BeforeEach
    void setUp() {
        DisponibilidadFranjaHandler h1 = new DisponibilidadFranjaHandler(reservaRepository, turnoRepository);
        BarberoDisponibleHandler h2 = new BarberoDisponibleHandler(barberoRepository, servicioRepository);
        DatosClienteHandler h3 = new DatosClienteHandler();
        ServicioPermitidoHandler h4 = new ServicioPermitidoHandler(servicioRepository);
        AsignacionAutomaticaHandler h5 = new AsignacionAutomaticaHandler(barberoRepository);

        ReservaValidationChain validationChain = new ReservaValidationChain(h1, h2, h3, h4, h5);
        reservaService = new ReservaService(validationChain, reservaRepository, turnoRepository);

        barberoAndres = BarberoDocument.builder()
                .id("03")
                .nombre("Andrés")
                .especialidades(List.of("CORTE", "BARBA"))
                .build();

        barberoMiguel = BarberoDocument.builder()
                .id("01")
                .nombre("Miguel")
                .especialidades(List.of("CORTE", "CORTE CON BARBA"))
                .build();

        servicioCorte = ServicioDocument.builder()
                .id("SER-001")
                .codigo("SER-001")
                .nombre("Corte clásico")
                .precio(25000.0)
                .especialidad("CORTE")
                .activo(true)
                .build();

        servicioBarba = ServicioDocument.builder()
                .id("SER-004")
                .codigo("SER-004")
                .nombre("Afeitado con navaja")
                .precio(20000.0)
                .especialidad("BARBA")
                .activo(true)
                .build();

        servicioCejas = ServicioDocument.builder()
                .id("SER-003")
                .codigo("SER-003")
                .nombre("Diseño de cejas")
                .precio(15000.0)
                .especialidad("CEJAS")
                .activo(true)
                .build();

        turnoAndres = TurnoDocument.builder()
                .id("t-doc-01")
                .turnoId("T01")
                .barberoId("03")
                .barberoNombre("Andrés")
                .fecha("2026-11-12")
                .hora("10:00")
                .estado("DISPONIBLE")
                .build();
    }

    @Test
    @DisplayName("Debe crear la reserva con éxito cuando el cliente indica barbero preferido y todo es válido")
    void crearReserva_exitosa_conPreferenciaBarbero() {
        // Arrange
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Andrés")
                .servicios(List.of("SER-001", "SER-004"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(1L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of(turnoAndres));
        when(barberoRepository.findById("Andrés")).thenReturn(Optional.empty());
        when(barberoRepository.findByNombreIgnoreCase("Andrés")).thenReturn(Optional.of(barberoAndres));
        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(servicioCorte));
        when(servicioRepository.findByCodigo("SER-004")).thenReturn(Optional.of(servicioBarba));
        when(reservaRepository.count()).thenReturn(0L);

        // Act
        ReservaResponseDTO response = reservaService.crearReserva(request);

        // Assert
        assertNotNull(response);
        assertEquals("R01", response.getIdReserva());
        assertEquals("T01", response.getIdTurno());
        assertEquals("Andrés", response.getBarberoAsignado());
        assertEquals("2026-11-12", response.getFecha());
        assertEquals("10:00", response.getHora());
        assertEquals(45000.0, response.getTotal());
        assertEquals("CONFIRMADA", response.getEstado());

        assertEquals("OCUPADO", turnoAndres.getEstado());
        verify(turnoRepository, times(1)).save(turnoAndres);
        verify(reservaRepository, times(1)).save(any(ReservaDocument.class));
    }

    @Test
    @DisplayName("Debe crear la reserva con éxito asignando automáticamente barbero cuando es 'Sin preferencia'")
    void crearReserva_exitosa_sinPreferenciaBarbero() {
        // Arrange
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

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(0L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of(turnoAndres));
        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(servicioCorte));
        when(servicioRepository.findByCodigo("SER-004")).thenReturn(Optional.of(servicioBarba));
        when(barberoRepository.findById("03")).thenReturn(Optional.of(barberoAndres));
        when(reservaRepository.count()).thenReturn(5L);

        // Act
        ReservaResponseDTO response = reservaService.crearReserva(request);

        // Assert
        assertNotNull(response);
        assertEquals("R06", response.getIdReserva());
        assertEquals("T01", response.getIdTurno());
        assertEquals("Andrés", response.getBarberoAsignado());
        assertEquals(45000.0, response.getTotal());
        assertEquals("CONFIRMADA", response.getEstado());

        assertEquals("OCUPADO", turnoAndres.getEstado());
        verify(turnoRepository, times(1)).save(turnoAndres);
        verify(reservaRepository, times(1)).save(any(ReservaDocument.class));
    }

    @Test
    @DisplayName("E01: Debe fallar con 409 cuando ya existen 3 reservas en esa fecha y hora")
    void crearReserva_fallaE01_franjaLlena_3Reservas() {
        // Arrange
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

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(3L);

        // Act
        ConflictException ex = assertThrows(ConflictException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Franja horaria está llena o barbero no disponible", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E01: Debe fallar con 409 cuando no existe ningún turno DISPONIBLE en la franja")
    void crearReserva_fallaE01_sinTurnosDisponibles() {
        // Arrange
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

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(1L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of());

        // Act
        ConflictException ex = assertThrows(ConflictException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Franja horaria está llena o barbero no disponible", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E02: Debe fallar con 422 cuando el barbero seleccionado no tiene la especialidad requerida")
    void crearReserva_fallaE02_especialidadIncompatible() {
        // Arrange
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

        TurnoDocument turnoMiguel = TurnoDocument.builder()
                .turnoId("T02")
                .barberoId("01")
                .barberoNombre("Miguel")
                .fecha("2026-11-12")
                .hora("10:00")
                .estado("DISPONIBLE")
                .build();

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(0L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of(turnoMiguel));
        when(barberoRepository.findById("Miguel")).thenReturn(Optional.empty());
        when(barberoRepository.findByNombreIgnoreCase("Miguel")).thenReturn(Optional.of(barberoMiguel));
        when(servicioRepository.findByCodigo("SER-003")).thenReturn(Optional.of(servicioCejas));

        // Act
        UnprocessableEntityException ex = assertThrows(UnprocessableEntityException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Ningun barbero cubre los servicios", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E03: Debe fallar con 400 cuando el nombre del cliente es vacío o contiene sólo espacios")
    void crearReserva_fallaE03_nombreClienteInvalido() {
        // Arrange
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Andrés")
                .servicios(List.of("SER-001"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("   ")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(0L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of(turnoAndres));
        when(barberoRepository.findById("Andrés")).thenReturn(Optional.empty());
        when(barberoRepository.findByNombreIgnoreCase("Andrés")).thenReturn(Optional.of(barberoAndres));
        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(servicioCorte));

        // Act
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Formato de entrada inválido", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E07: Debe fallar con 400 cuando el teléfono del cliente no contiene exactamente 10 dígitos numéricos")
    void crearReserva_fallaE07_telefonoClienteInvalido() {
        // Arrange
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Andrés")
                .servicios(List.of("SER-001"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("12345")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(0L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of(turnoAndres));
        when(barberoRepository.findById("Andrés")).thenReturn(Optional.empty());
        when(barberoRepository.findByNombreIgnoreCase("Andrés")).thenReturn(Optional.of(barberoAndres));
        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(servicioCorte));

        // Act
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Formato de entrada inválido", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E08: Debe fallar con 400 cuando el correo del cliente no pertenece al dominio @gmail.com")
    void crearReserva_fallaE08_correoClienteInvalido() {
        // Arrange
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Andrés")
                .servicios(List.of("SER-001"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@hotmail.com")
                        .build())
                .build();

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(0L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of(turnoAndres));
        when(barberoRepository.findById("Andrés")).thenReturn(Optional.empty());
        when(barberoRepository.findByNombreIgnoreCase("Andrés")).thenReturn(Optional.of(barberoAndres));
        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(servicioCorte));

        // Act
        BadRequestException ex = assertThrows(BadRequestException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Formato de entrada inválido", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("E09: Debe fallar con 404 cuando algún servicio solicitado no existe en el catálogo activo")
    void crearReserva_fallaE09_servicioInexistente() {
        // Arrange
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Andrés")
                .servicios(List.of("SER-001", "SER-999"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo("carlos.ruiz@gmail.com")
                        .build())
                .build();

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(0L);
        when(turnoRepository.findByFechaAndHoraAndEstado("2026-11-12", "10:00", "DISPONIBLE")).thenReturn(List.of(turnoAndres));
        when(barberoRepository.findById("Andrés")).thenReturn(Optional.empty());
        when(barberoRepository.findByNombreIgnoreCase("Andrés")).thenReturn(Optional.of(barberoAndres));
        when(servicioRepository.findByCodigo("SER-001")).thenReturn(Optional.of(servicioCorte));
        when(servicioRepository.findByCodigo("SER-999")).thenReturn(Optional.empty());

        // Act
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Servicio inexistente en el catálogo activo", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Orden estricto: Con fallas simultáneas en franja y cliente, debe fallar en el paso 1 (franja)")
    void crearReserva_ordenEstricto_fallaFranjaAntesDeCliente() {
        // Arrange
        ReservaRequestDTO request = ReservaRequestDTO.builder()
                .fecha("2026-11-12")
                .hora("10:00")
                .barberoPreferido("Andrés")
                .servicios(List.of("SER-001"))
                .cliente(ReservaRequestDTO.ClienteDTO.builder()
                        .nombre("")
                        .telefono("123")
                        .correo("invalido@hotmail.com")
                        .build())
                .build();

        when(reservaRepository.countByFechaAndHoraAndEstado("2026-11-12", "10:00", "CONFIRMADA")).thenReturn(3L);

        // Act
        ConflictException ex = assertThrows(ConflictException.class, () -> reservaService.crearReserva(request));

        // Assert
        assertEquals("Franja horaria está llena o barbero no disponible", ex.getMessage());

        verify(turnoRepository, never()).save(any());
        verify(reservaRepository, never()).save(any());
    }

    @Test
    @DisplayName("obtenerMisReservas retorna la lista de reservas mapeadas cuando el cliente tiene historial")
    void obtenerMisReservas_debeRetornarListaDeReservas() {
        // Arrange
        String correo = "carlos.ruiz@gmail.com";
        ReservaDocument doc = ReservaDocument.builder()
                .idReserva("R01")
                .idTurno("T01")
                .barberoAsignado("Andrés")
                .fecha("2026-11-12")
                .hora("10:00")
                .total(45000.0)
                .estado("CONFIRMADA")
                .servicios(List.of(
                        ReservaDocument.ServicioEmbebido.builder()
                                .codigo("SER-001")
                                .nombre("Corte clásico")
                                .precio(25000.0)
                                .especialidad("CORTE")
                                .build()
                ))
                .cliente(ReservaDocument.ClienteEmbebido.builder()
                        .nombre("Carlos Ruiz")
                        .telefono("3208872165")
                        .correo(correo)
                        .build())
                .build();

        when(reservaRepository.findByClienteCorreoOrderByFechaDescHoraDesc(correo)).thenReturn(List.of(doc));

        // Act
        List<ReservaResponseDTO> resultado = reservaService.obtenerMisReservas(correo);

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        ReservaResponseDTO res = resultado.get(0);
        assertEquals("R01", res.getIdReserva());
        assertEquals("T01", res.getIdTurno());
        assertEquals("Andrés", res.getBarberoAsignado());
        assertEquals("2026-11-12", res.getFecha());
        assertEquals("10:00", res.getHora());
        assertEquals(45000.0, res.getTotal());
        assertEquals("CONFIRMADA", res.getEstado());
        assertNotNull(res.getServicios());
        assertEquals(1, res.getServicios().size());
        assertEquals("SER-001", res.getServicios().get(0).getCodigo());
        assertNotNull(res.getCliente());
        assertEquals("Carlos Ruiz", res.getCliente().getNombre());
    }

    @Test
    @DisplayName("obtenerMisReservas retorna lista vacía cuando el cliente no tiene reservas registradas")
    void obtenerMisReservas_debeRetornarListaVacia_cuandoNoHayReservas() {
        // Arrange
        String correo = "cliente.nuevo@gmail.com";
        when(reservaRepository.findByClienteCorreoOrderByFechaDescHoraDesc(correo)).thenReturn(List.of());

        // Act
        List<ReservaResponseDTO> resultado = reservaService.obtenerMisReservas(correo);

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }

    @Test
    @DisplayName("obtenerTodasLasReservas retorna todas las reservas del sistema para el administrador")
    void obtenerTodasLasReservas_debeRetornarTodasLasReservas() {
        // Arrange
        ReservaDocument doc1 = ReservaDocument.builder()
                .idReserva("R02")
                .idTurno("T02")
                .barberoAsignado("Laura")
                .fecha("2026-11-13")
                .hora("11:00")
                .total(15000.0)
                .estado("CONFIRMADA")
                .build();
        ReservaDocument doc2 = ReservaDocument.builder()
                .idReserva("R01")
                .idTurno("T01")
                .barberoAsignado("Miguel")
                .fecha("2026-11-12")
                .hora("10:00")
                .total(25000.0)
                .estado("CONFIRMADA")
                .build();

        when(reservaRepository.findAllByOrderByFechaDescHoraDesc()).thenReturn(List.of(doc1, doc2));

        // Act
        List<ReservaResponseDTO> resultado = reservaService.obtenerTodasLasReservas();

        // Assert
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertEquals("R02", resultado.get(0).getIdReserva());
        assertEquals("R01", resultado.get(1).getIdReserva());
    }

    @Test
    @DisplayName("obtenerTodasLasReservas retorna lista vacía cuando no hay ninguna reserva en el sistema")
    void obtenerTodasLasReservas_debeRetornarListaVacia_cuandoNoHayRegistros() {
        // Arrange
        when(reservaRepository.findAllByOrderByFechaDescHoraDesc()).thenReturn(List.of());

        // Act
        List<ReservaResponseDTO> resultado = reservaService.obtenerTodasLasReservas();

        // Assert
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
    }
}

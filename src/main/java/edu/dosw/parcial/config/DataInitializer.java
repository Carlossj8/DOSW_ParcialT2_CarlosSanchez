package edu.dosw.parcial.config;

import edu.dosw.parcial.persistence.entities.BarberoDocument;
import edu.dosw.parcial.persistence.entities.ServicioDocument;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.repositories.BarberoRepository;
import edu.dosw.parcial.persistence.repositories.ServicioRepository;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final BarberoRepository barberoRepository;
    private final ServicioRepository servicioRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        log.info("Inicializando datos semilla en MongoDB...");
        seedBarberos();
        seedServicios();
        seedUsuarios();
        log.info("Datos semilla inicializados correctamente.");
    }

    private void seedBarberos() {
        if (barberoRepository.count() == 0) {
            List<BarberoDocument> barberos = List.of(
                BarberoDocument.builder()
                    .id("01")
                    .nombre("Miguel")
                    .especialidades(List.of("CORTE", "CORTE CON BARBA"))
                    .diasDisponibles(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES"))
                    .horaInicio("10:00")
                    .horaFin("17:00")
                    .build(),
                BarberoDocument.builder()
                    .id("02")
                    .nombre("Laura")
                    .especialidades(List.of("CAPILAR", "CEJAS"))
                    .diasDisponibles(List.of("LUNES", "MARTES", "MIERCOLES", "JUEVES", "VIERNES"))
                    .horaInicio("10:00")
                    .horaFin("17:00")
                    .build(),
                BarberoDocument.builder()
                    .id("03")
                    .nombre("Andrés")
                    .especialidades(List.of("CORTE", "BARBA"))
                    .diasDisponibles(List.of("MARTES", "JUEVES", "SABADO", "DOMINGO"))
                    .horaInicio("10:00")
                    .horaFin("14:00")
                    .build()
            );
            barberoRepository.saveAll(barberos);
            log.info("Se registraron {} barberos en el catálogo.", barberos.size());
        }
    }

    private void seedServicios() {
        if (servicioRepository.count() == 0) {
            List<ServicioDocument> servicios = List.of(
                ServicioDocument.builder()
                    .id("SER-001")
                    .codigo("SER-001")
                    .nombre("Corte clásico")
                    .precio(25000.0)
                    .especialidad("CORTE")
                    .activo(true)
                    .build(),
                ServicioDocument.builder()
                    .id("SER-002")
                    .codigo("SER-002")
                    .nombre("Corte + barba")
                    .precio(38000.0)
                    .especialidad("CORTE CON BARBA")
                    .activo(true)
                    .build(),
                ServicioDocument.builder()
                    .id("SER-003")
                    .codigo("SER-003")
                    .nombre("Diseño de cejas")
                    .precio(15000.0)
                    .especialidad("CEJAS")
                    .activo(true)
                    .build(),
                ServicioDocument.builder()
                    .id("SER-004")
                    .codigo("SER-004")
                    .nombre("Afeitado con navaja")
                    .precio(20000.0)
                    .especialidad("BARBA")
                    .activo(true)
                    .build(),
                ServicioDocument.builder()
                    .id("SER-005")
                    .codigo("SER-005")
                    .nombre("Tratamiento capilar")
                    .precio(45000.0)
                    .especialidad("CAPILAR")
                    .activo(true)
                    .build()
            );
            servicioRepository.saveAll(servicios);
            log.info("Se registraron {} servicios en el catálogo.", servicios.size());
        }
    }

    private void seedUsuarios() {
        if (!usuarioRepository.existsByCorreo("parcialt2@bobsbarber.com")) {
            usuarioRepository.save(UsuarioDocument.builder()
                .correo("parcialt2@bobsbarber.com")
                .password(passwordEncoder.encode("vamosacar5profe"))
                .rol("ADMIN")
                .nombre("Administrador Bob's Barber")
                .telefono("3001234567")
                .build());
        }

        if (!usuarioRepository.existsByCorreo("admin@gmail.com")) {
            usuarioRepository.save(UsuarioDocument.builder()
                .correo("admin@gmail.com")
                .password(passwordEncoder.encode("admin123"))
                .rol("ADMIN")
                .nombre("Admin Principal")
                .telefono("3007654321")
                .build());
        }

        if (!usuarioRepository.existsByCorreo("carlos.ruiz@gmail.com")) {
            usuarioRepository.save(UsuarioDocument.builder()
                .correo("carlos.ruiz@gmail.com")
                .password(passwordEncoder.encode("cliente123"))
                .rol("CLIENTE")
                .nombre("Carlos Ruiz")
                .telefono("3208872165")
                .build());
        }
    }
}

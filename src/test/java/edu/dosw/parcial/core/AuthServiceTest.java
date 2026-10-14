package edu.dosw.parcial.core;

import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.controller.mappers.AuthMapper;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.services.AuthService;
import edu.dosw.parcial.core.utils.AuthenticationFailedException;
import edu.dosw.parcial.core.utils.JwtUtil;
import edu.dosw.parcial.core.validators.AuthValidator;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import edu.dosw.parcial.persistence.mappers.UsuarioMapper;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Spy
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthValidator authValidator;

    @Mock
    private JwtUtil jwtUtil;

    @Spy
    private AuthMapper authMapper;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authValidator = new AuthValidator(passwordEncoder);
        authService = new AuthService(
                usuarioRepository,
                usuarioMapper,
                authValidator,
                jwtUtil,
                authMapper
        );
    }

    @Test
    @DisplayName("Debe autenticar exitosamente y retornar token con rol cuando las credenciales son válidas")
    void login_debeRetornarTokenYRol_cuandoCredencialesSonValidas() {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .correo("admin@gmail.com")
                .password("admin123")
                .build();

        UsuarioDocument usuarioDoc = UsuarioDocument.builder()
                .id("u1")
                .correo("admin@gmail.com")
                .password("$2a$10$encodedPassword")
                .rol("ADMIN")
                .nombre("Admin Principal")
                .build();

        when(usuarioRepository.findByCorreo("admin@gmail.com")).thenReturn(Optional.of(usuarioDoc));
        when(passwordEncoder.matches("admin123", "$2a$10$encodedPassword")).thenReturn(true);
        when(jwtUtil.generateToken("admin@gmail.com", "ADMIN")).thenReturn("jwt.token.valido");

        // Act
        LoginResponseDTO response = authService.login(request);

        // Assert
        assertNotNull(response);
        assertEquals("jwt.token.valido", response.getToken());
        assertEquals("ADMIN", response.getRol());
        assertEquals("admin@gmail.com", response.getCorreo());
        verify(usuarioRepository).findByCorreo("admin@gmail.com");
        verify(jwtUtil).generateToken("admin@gmail.com", "ADMIN");
    }

    @Test
    @DisplayName("Debe lanzar AuthenticationFailedException cuando el usuario no existe")
    void login_debeLanzarExcepcion_cuandoUsuarioNoExiste() {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .correo("inexistente@gmail.com")
                .password("password123")
                .build();

        when(usuarioRepository.findByCorreo("inexistente@gmail.com")).thenReturn(Optional.empty());

        // Act & Assert
        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login(request)
        );

        assertEquals("Usuario no existe o contraseña errónea", exception.getMessage());
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    @Test
    @DisplayName("Debe lanzar AuthenticationFailedException cuando la contraseña es incorrecta")
    void login_debeLanzarExcepcion_cuandoContrasenaEsIncorrecta() {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .correo("admin@gmail.com")
                .password("wrongpassword")
                .build();

        UsuarioDocument usuarioDoc = UsuarioDocument.builder()
                .id("u1")
                .correo("admin@gmail.com")
                .password("$2a$10$encodedPassword")
                .rol("ADMIN")
                .build();

        when(usuarioRepository.findByCorreo("admin@gmail.com")).thenReturn(Optional.of(usuarioDoc));
        when(passwordEncoder.matches("wrongpassword", "$2a$10$encodedPassword")).thenReturn(false);

        // Act & Assert
        AuthenticationFailedException exception = assertThrows(
                AuthenticationFailedException.class,
                () -> authService.login(request)
        );

        assertEquals("Usuario no existe o contraseña errónea", exception.getMessage());
        verify(jwtUtil, never()).generateToken(any(), any());
    }
}

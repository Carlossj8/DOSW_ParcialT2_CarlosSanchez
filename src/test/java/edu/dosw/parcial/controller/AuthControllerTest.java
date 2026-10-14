package edu.dosw.parcial.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.controller.handlers.GlobalExceptionHandler;
import edu.dosw.parcial.core.services.AuthService;
import edu.dosw.parcial.core.utils.AuthenticationFailedException;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 201 Created con token y rol")
    void login_debeRetornar201_cuandoPeticionEsValida() throws Exception {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .correo("admin@gmail.com")
                .password("admin123")
                .build();

        LoginResponseDTO response = LoginResponseDTO.builder()
                .token("jwt.token.valido")
                .rol("ADMIN")
                .correo("admin@gmail.com")
                .build();

        when(authService.login(any(LoginRequestDTO.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("jwt.token.valido"))
                .andExpect(jsonPath("$.rol").value("ADMIN"))
                .andExpect(jsonPath("$.correo").value("admin@gmail.com"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 400 Bad Request cuando el correo no tiene formato válido")
    void login_debeRetornar400_cuandoCorreoEsInvalido() throws Exception {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .correo("correo-no-valido")
                .password("admin123")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.mensaje").value("Campos vacíos o formato de entrada incorrecto"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 400 Bad Request cuando la contraseña está vacía")
    void login_debeRetornar400_cuandoPasswordEstaVacio() throws Exception {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .correo("admin@gmail.com")
                .password("")
                .build();

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 401 Unauthorized cuando las credenciales no coinciden")
    void login_debeRetornar401_cuandoCredencialesSonErroneas() throws Exception {
        // Arrange
        LoginRequestDTO request = LoginRequestDTO.builder()
                .correo("admin@gmail.com")
                .password("wrongpassword")
                .build();

        when(authService.login(any(LoginRequestDTO.class)))
                .thenThrow(new AuthenticationFailedException("Usuario no existe o contraseña errónea"));

        // Act & Assert
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.mensaje").value("Usuario no existe o contraseña errónea"));
    }
}

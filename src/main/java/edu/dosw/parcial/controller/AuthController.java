package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.core.services.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticación", description = "Endpoints de inicio de sesión y gestión de tokens JWT")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Permite a administradores y clientes autenticarse y obtener un token JWT")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Autenticación exitosa"),
        @ApiResponse(responseCode = "400", description = "Campos vacíos o formato de correo incorrecto"),
        @ApiResponse(responseCode = "401", description = "Usuario no existe o contraseña errónea")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        log.info("Petición recibida en POST /api/v1/auth/login para: {}", request.getCorreo());
        LoginResponseDTO response = authService.login(request);
        log.info("Respondiendo 201 Created para: {}", request.getCorreo());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}

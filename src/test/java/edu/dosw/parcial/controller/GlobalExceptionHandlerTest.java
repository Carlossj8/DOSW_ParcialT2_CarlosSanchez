package edu.dosw.parcial.controller;

import edu.dosw.parcial.controller.handlers.GlobalExceptionHandler;
import edu.dosw.parcial.core.utils.AuthenticationFailedException;
import edu.dosw.parcial.core.utils.BadRequestException;
import edu.dosw.parcial.core.utils.ConflictException;
import edu.dosw.parcial.core.utils.ResourceNotFoundException;
import edu.dosw.parcial.core.utils.UnprocessableEntityException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
    }

    @Test
    @DisplayName("handleBadRequest retorna estado 400 y mensaje correspondiente")
    void handleBadRequest_debeRetornar400() {
        BadRequestException ex = new BadRequestException("Parámetro inválido");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleBadRequest(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(400, response.getBody().get("status"));
        assertEquals("Parámetro inválido", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("handleAuthenticationFailed retorna estado 401 y mensaje correspondiente")
    void handleAuthenticationFailed_debeRetornar401() {
        AuthenticationFailedException ex = new AuthenticationFailedException("Credenciales incorrectas");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAuthenticationFailed(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertEquals(401, response.getBody().get("status"));
        assertEquals("Credenciales incorrectas", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("handleAccessDenied retorna estado 403 y mensaje correspondiente")
    void handleAccessDenied_debeRetornar403() {
        AccessDeniedException ex = new AccessDeniedException("Acceso denegado");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAccessDenied(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
        assertEquals(403, response.getBody().get("status"));
        assertEquals("No tiene permisos suficientes para realizar esta acción", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("handleResourceNotFound retorna estado 404 y mensaje correspondiente")
    void handleResourceNotFound_debeRetornar404() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Recurso no encontrado");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleResourceNotFound(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(404, response.getBody().get("status"));
        assertEquals("Recurso no encontrado", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("handleConflict retorna estado 409 y mensaje correspondiente")
    void handleConflict_debeRetornar409() {
        ConflictException ex = new ConflictException("Conflicto de disponibilidad");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleConflict(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals(409, response.getBody().get("status"));
        assertEquals("Conflicto de disponibilidad", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("handleUnprocessableEntity retorna estado 422 y mensaje correspondiente")
    void handleUnprocessableEntity_debeRetornar422() {
        UnprocessableEntityException ex = new UnprocessableEntityException("Regla de negocio no satisfecha");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleUnprocessableEntity(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals(422, response.getBody().get("status"));
        assertEquals("Regla de negocio no satisfecha", response.getBody().get("mensaje"));
    }

    @Test
    @DisplayName("handleAllExceptions retorna estado 500 para excepciones no controladas")
    void handleAllExceptions_debeRetornar500() {
        RuntimeException ex = new RuntimeException("Error inesperado");
        ResponseEntity<Map<String, Object>> response = exceptionHandler.handleAllExceptions(ex);

        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(500, response.getBody().get("status"));
        assertTrue(response.getBody().get("mensaje").toString().contains("Error inesperado"));
    }
}

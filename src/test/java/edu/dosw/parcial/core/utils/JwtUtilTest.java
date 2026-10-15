package edu.dosw.parcial.core.utils;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private static final String SECRET = "dGhpc2lzYWR1bW15c2VjcmV0Zm9ydGVzdGluZ3B1cnBvc2Vzb25seTEyMzQ1Njc4OTA=";
    private static final long EXPIRATION_MS = 3600000;

    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(SECRET, EXPIRATION_MS);
    }

    @Test
    @DisplayName("generateToken genera un token válido y extrae correo y rol correctamente")
    void generateToken_y_extraccion_exitoso() {
        String token = jwtUtil.generateToken("carlos@bobs.com", "CLIENTE");

        assertNotNull(token);
        assertTrue(jwtUtil.validateToken(token));
        assertEquals("carlos@bobs.com", jwtUtil.extractCorreo(token));
        assertEquals("CLIENTE", jwtUtil.extractRol(token));
    }

    @Test
    @DisplayName("validateToken retorna false cuando el token es inválido o malformado")
    void validateToken_tokenInvalido_retornaFalse() {
        assertFalse(jwtUtil.validateToken("token.invalido.123"));
        assertFalse(jwtUtil.validateToken(""));
    }

    @Test
    @DisplayName("validateToken retorna false cuando el token ha expirado")
    void validateToken_tokenExpirado_retornaFalse() {
        JwtUtil expiredJwtUtil = new JwtUtil(SECRET, -1000);
        String expiredToken = expiredJwtUtil.generateToken("expirado@bobs.com", "ADMIN");

        assertFalse(jwtUtil.validateToken(expiredToken));
    }
}

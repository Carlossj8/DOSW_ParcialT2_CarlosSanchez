package edu.dosw.parcial.core.validators;

import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.utils.AuthenticationFailedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AuthValidator {

    private final PasswordEncoder passwordEncoder;

    public AuthValidator(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void validarCredenciales(Usuario usuario, String rawPassword) {
        if (usuario == null) {
            log.warn("Fallo de validación: Usuario no existe en la base de datos");
            throw new AuthenticationFailedException("Usuario no existe o contraseña errónea");
        }

        if (!passwordEncoder.matches(rawPassword, usuario.getPassword())) {
            log.warn("Fallo de validación: Contraseña incorrecta para el usuario {}", usuario.getCorreo());
            throw new AuthenticationFailedException("Usuario no existe o contraseña errónea");
        }
    }
}

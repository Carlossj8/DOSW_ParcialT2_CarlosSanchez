package edu.dosw.parcial.core.services;

import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.controller.mappers.AuthMapper;
import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.core.utils.JwtUtil;
import edu.dosw.parcial.core.validators.AuthValidator;
import edu.dosw.parcial.persistence.mappers.UsuarioMapper;
import edu.dosw.parcial.persistence.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final AuthValidator authValidator;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;

    public AuthService(
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            AuthValidator authValidator,
            JwtUtil jwtUtil,
            AuthMapper authMapper) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.authValidator = authValidator;
        this.jwtUtil = jwtUtil;
        this.authMapper = authMapper;
    }

    public LoginResponseDTO login(LoginRequestDTO request) {
        log.info("Procesando inicio de sesión para el correo: {}", request.getCorreo());

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .map(usuarioMapper::toDomain)
                .orElse(null);

        authValidator.validarCredenciales(usuario, request.getPassword());

        String token = jwtUtil.generateToken(usuario.getCorreo(), usuario.getRol());
        log.info("Autenticación exitosa para el usuario: {} con rol: {}", usuario.getCorreo(), usuario.getRol());

        return authMapper.toResponse(token, usuario.getRol(), usuario.getCorreo());
    }
}

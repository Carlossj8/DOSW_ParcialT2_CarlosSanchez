package edu.dosw.parcial.controller.mappers;

import edu.dosw.parcial.controller.dtos.request.LoginRequestDTO;
import edu.dosw.parcial.controller.dtos.response.LoginResponseDTO;
import edu.dosw.parcial.core.models.Usuario;
import org.springframework.stereotype.Component;

@Component
public class AuthMapper {

    public Usuario toDomain(LoginRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return Usuario.builder()
                .correo(dto.getCorreo())
                .password(dto.getPassword())
                .build();
    }

    public LoginResponseDTO toResponse(String token, String rol, String correo) {
        return LoginResponseDTO.builder()
                .token(token)
                .rol(rol)
                .correo(correo)
                .build();
    }
}

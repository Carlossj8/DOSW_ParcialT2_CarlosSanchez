package edu.dosw.parcial.persistence.mappers;

import edu.dosw.parcial.core.models.Usuario;
import edu.dosw.parcial.persistence.entities.UsuarioDocument;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toDomain(UsuarioDocument document) {
        if (document == null) {
            return null;
        }
        return Usuario.builder()
                .id(document.getId())
                .correo(document.getCorreo())
                .password(document.getPassword())
                .rol(document.getRol())
                .nombre(document.getNombre())
                .telefono(document.getTelefono())
                .build();
    }

    public UsuarioDocument toDocument(Usuario domain) {
        if (domain == null) {
            return null;
        }
        return UsuarioDocument.builder()
                .id(domain.getId())
                .correo(domain.getCorreo())
                .password(domain.getPassword())
                .rol(domain.getRol())
                .nombre(domain.getNombre())
                .telefono(domain.getTelefono())
                .build();
    }
}

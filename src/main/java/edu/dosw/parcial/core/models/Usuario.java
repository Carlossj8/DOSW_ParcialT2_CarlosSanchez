package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private String id;
    private String correo;
    private String password;
    private String rol; // ADMIN, CLIENTE
    private String nombre;
    private String telefono;
}

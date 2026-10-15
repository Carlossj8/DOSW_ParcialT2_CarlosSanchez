package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servicio {
    private String id;
    private String codigo;
    private String nombre;
    private Double precio;
    private String especialidad;
    private boolean activo;
}

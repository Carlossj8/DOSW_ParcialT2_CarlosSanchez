package edu.dosw.parcial.core.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Barbero {
    private String id;
    private String nombre;
    private List<String> especialidades;
    private List<String> diasDisponibles;
    private String horaInicio;
    private String horaFin;
}

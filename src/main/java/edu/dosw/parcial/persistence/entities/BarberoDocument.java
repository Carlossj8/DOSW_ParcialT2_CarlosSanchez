package edu.dosw.parcial.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "barberos")
public class BarberoDocument {

    @Id
    private String id;

    private String nombre;

    private List<String> especialidades;

    private List<String> diasDisponibles;

    private String horaInicio;

    private String horaFin;
}

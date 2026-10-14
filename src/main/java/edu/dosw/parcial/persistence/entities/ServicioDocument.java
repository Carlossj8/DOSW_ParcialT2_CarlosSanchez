package edu.dosw.parcial.persistence.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "servicios")
public class ServicioDocument {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigo; // SER-001, etc.

    private String nombre;

    private Double precio;

    private String especialidad;

    private boolean activo;
}

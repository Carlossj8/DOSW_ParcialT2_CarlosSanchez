package edu.dosw.parcial.controller.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TurnoRequestDTO {

    @NotBlank(message = "El barberoId es obligatorio")
    private String barberoId;

    @NotBlank(message = "La fecha es obligatoria")
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "Formato de fecha inválido, debe ser YYYY-MM-DD")
    private String fecha;

    @NotBlank(message = "La hora es obligatoria")
    @Pattern(regexp = "^\\d{2}:\\d{2}$", message = "Formato de hora inválido, debe ser HH:mm")
    private String hora;
}

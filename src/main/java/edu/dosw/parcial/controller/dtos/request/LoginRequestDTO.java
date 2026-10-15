package edu.dosw.parcial.controller.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @NotBlank(message = "El correo es obligatorio y no puede estar vacío")
    @Email(message = "Formato de correo incorrecto")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria y no puede estar vacía")
    private String password;
}

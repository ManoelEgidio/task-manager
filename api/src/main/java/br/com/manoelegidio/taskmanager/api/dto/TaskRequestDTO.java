package br.com.manoelegidio.taskmanager.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record TaskRequestDTO(

        @NotBlank(message = "O título é obrigatório.")
        @Size(min = 3, message = "O título deve ter no mínimo 3 caracteres.")
        String title,

        String description,

        Boolean completed
) {
}

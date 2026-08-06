package br.com.manoelegidio.taskmanager.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "DTO para atualização do status de conclusão da tarefa")
public record TaskStatusUpdateDTO(

        @Schema(description = "Novo status de conclusão da tarefa", example = "true")
        @NotNull(message = "O campo completed é obrigatório.")
        Boolean completed
) {
}

package br.com.manoelegidio.taskmanager.api.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "Estrutura padronizada de erro da API")
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponseDTO(

        @Schema(description = "Data e hora em que o erro ocorreu")
        LocalDateTime timestamp,

        @Schema(description = "Código HTTP do status", example = "400")
        int status,

        @Schema(description = "Descrição do status HTTP", example = "Bad Request")
        String error,

        @Schema(description = "Mensagem amigável descrevendo a causa do erro")
        String message,

        @Schema(description = "URI da requisição", example = "/api/v1/tasks")
        String path,

        @Schema(description = "Lista detalhada de erros de validação por campo")
        List<FieldErrorDetail> fieldErrors
) {
    public record FieldErrorDetail(
            String field,
            String message
    ) {}
}

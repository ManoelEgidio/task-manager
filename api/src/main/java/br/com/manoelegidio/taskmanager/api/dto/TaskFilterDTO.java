package br.com.manoelegidio.taskmanager.api.dto;

public record TaskFilterDTO(
        String title,
        String description,
        Boolean completed
) {
}

package br.com.manoelegidio.taskmanager.api.dto;

public record TaskSummaryDTO(
        long total,
        long pending,
        long completed
) {
}

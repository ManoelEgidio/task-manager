package br.com.manoelegidio.taskmanager.api.dto;

import br.com.manoelegidio.taskmanager.api.model.Task;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        Boolean completed,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt
) {
    public static TaskResponseDTO fromEntity(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCompleted(),
                task.getCreatedAt(),
                task.getUpdatedAt()
        );
    }
}

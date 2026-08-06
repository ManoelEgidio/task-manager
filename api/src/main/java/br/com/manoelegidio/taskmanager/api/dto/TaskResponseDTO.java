package br.com.manoelegidio.taskmanager.api.dto;

import br.com.manoelegidio.taskmanager.api.model.Task;

public record TaskResponseDTO(
        Long id,
        String title,
        String description,
        Boolean completed
) {
    public static TaskResponseDTO fromEntity(Task task) {
        return new TaskResponseDTO(
                task.getId(),
                task.getTitle(),
                task.getDescription(),
                task.getCompleted()
        );
    }
}

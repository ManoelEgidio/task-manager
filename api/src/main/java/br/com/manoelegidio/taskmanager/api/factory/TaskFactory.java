package br.com.manoelegidio.taskmanager.api.factory;

import br.com.manoelegidio.taskmanager.api.dto.TaskRequestDTO;
import br.com.manoelegidio.taskmanager.api.model.Task;
import org.springframework.stereotype.Component;

@Component
public class TaskFactory {

    public Task create(TaskRequestDTO dto) {
        String sanitizedTitle = dto.title() != null ? dto.title().trim() : "";
        String sanitizedDescription = (dto.description() != null && !dto.description().isBlank())
                ? dto.description().trim()
                : null;
        Boolean isCompleted = dto.completed() != null ? dto.completed() : false;

        return Task.builder()
                .title(sanitizedTitle)
                .description(sanitizedDescription)
                .completed(isCompleted)
                .build();
    }

    public void updateEntity(Task task, TaskRequestDTO dto) {
        if (dto.title() != null && !dto.title().isBlank()) {
            task.setTitle(dto.title().trim());
        }
        task.setDescription(dto.description() != null && !dto.description().isBlank()
                ? dto.description().trim()
                : null);
        if (dto.completed() != null) {
            task.setCompleted(dto.completed());
        }
    }
}

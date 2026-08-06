package br.com.manoelegidio.taskmanager.api.service;

import br.com.manoelegidio.taskmanager.api.dto.TaskFilterDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskRequestDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskResponseDTO;
import br.com.manoelegidio.taskmanager.api.exception.ResourceNotFoundException;
import br.com.manoelegidio.taskmanager.api.factory.TaskFactory;
import br.com.manoelegidio.taskmanager.api.model.Task;
import br.com.manoelegidio.taskmanager.api.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskFactory taskFactory;

    @Transactional(readOnly = true)
    public Page<TaskResponseDTO> search(TaskFilterDTO filter, Integer page, Integer size, String sort, String direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.fromString(direction), sort));
        return taskRepository.search(filter, pageable).map(TaskResponseDTO::fromEntity);
    }

    @Transactional(readOnly = true)
    public TaskResponseDTO findById(Long id) {
        Task task = getTaskById(id);
        return TaskResponseDTO.fromEntity(task);
    }

    @Transactional
    public TaskResponseDTO create(TaskRequestDTO dto) {
        Task task = taskFactory.create(dto);
        Task saved = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(saved);
    }

    @Transactional
    public TaskResponseDTO update(Long id, TaskRequestDTO dto) {
        Task task = getTaskById(id);
        taskFactory.updateEntity(task, dto);
        Task updated = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(updated);
    }

    @Transactional
    public TaskResponseDTO toggleCompleted(Long id) {
        Task task = getTaskById(id);
        task.setCompleted(!task.getCompleted());
        Task updated = taskRepository.save(task);
        return TaskResponseDTO.fromEntity(updated);
    }

    @Transactional
    public void delete(Long id) {
        Task task = getTaskById(id);
        taskRepository.delete(task);
    }

    private Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa com ID " + id + " não foi encontrada."));
    }
}

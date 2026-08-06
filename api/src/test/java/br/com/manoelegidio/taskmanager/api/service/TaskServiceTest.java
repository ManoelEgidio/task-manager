package br.com.manoelegidio.taskmanager.api.service;

import br.com.manoelegidio.taskmanager.api.dto.TaskFilterDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskRequestDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskResponseDTO;
import br.com.manoelegidio.taskmanager.api.exception.ResourceNotFoundException;
import br.com.manoelegidio.taskmanager.api.factory.TaskFactory;
import br.com.manoelegidio.taskmanager.api.model.Task;
import br.com.manoelegidio.taskmanager.api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Spy
    private TaskFactory taskFactory;

    @InjectMocks
    private TaskService taskService;

    private Task sampleTask;
    private TaskRequestDTO sampleRequestDTO;

    @BeforeEach
    void setUp() {
        sampleTask = Task.builder()
                .id(1L)
                .title("Estudar Spring Boot")
                .description("Desenvolver API RESTful")
                .completed(false)
                .build();
        sampleRequestDTO = new TaskRequestDTO("Estudar Spring Boot", "Desenvolver API RESTful", false);
    }

    @Test
    @DisplayName("Deve buscar tarefas com filtro e paginação via search")
    void search_ShouldReturnPagedTasks() {
        Page<Task> page = new PageImpl<>(List.of(sampleTask));
        when(taskRepository.search(any(), any(Pageable.class))).thenReturn(page);

        TaskFilterDTO filter = new TaskFilterDTO("Spring", null, null);
        Page<TaskResponseDTO> result = taskService.search(filter, 0, 10, "id", "asc");

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).title()).isEqualTo("Estudar Spring Boot");
    }

    @Test
    @DisplayName("Deve buscar tarefa por ID com sucesso")
    void findById_ShouldReturnTask_WhenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));

        TaskResponseDTO result = taskService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.title()).isEqualTo("Estudar Spring Boot");
    }

    @Test
    @DisplayName("Deve lançar exceção quando tarefa não for encontrada por ID")
    void findById_ShouldThrowException_WhenNotFound() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Tarefa com ID 99 não foi encontrada.");
    }

    @Test
    @DisplayName("Deve criar nova tarefa")
    void create_ShouldReturnCreatedTask() {
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.create(sampleRequestDTO);

        assertThat(result).isNotNull();
        assertThat(result.title()).isEqualTo("Estudar Spring Boot");
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    @DisplayName("Deve criar tarefa tratando campos opcionais nulos")
    void create_ShouldHandleNullOptionalFields() {
        TaskRequestDTO dtoWithNulls = new TaskRequestDTO("Nova Tarefa Sem Descrição", null, null);
        Task expectedSaved = Task.builder().id(2L).title("Nova Tarefa Sem Descrição").description(null).completed(false).build();
        when(taskRepository.save(any(Task.class))).thenReturn(expectedSaved);

        TaskResponseDTO result = taskService.create(dtoWithNulls);

        assertThat(result).isNotNull();
        assertThat(result.description()).isNull();
        assertThat(result.completed()).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar tarefa existente")
    void update_ShouldUpdateExistingTask() {
        TaskRequestDTO updateDTO = new TaskRequestDTO("Título Atualizado", "Descrição Atualizada", true);
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(any(Task.class))).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.update(1L, updateDTO);

        assertThat(result).isNotNull();
        verify(taskRepository).save(sampleTask);
    }

    @Test
    @DisplayName("Deve alternar o status de concluído")
    void toggleCompleted_ShouldInvertStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        when(taskRepository.save(sampleTask)).thenReturn(sampleTask);

        TaskResponseDTO result = taskService.toggleCompleted(1L);

        assertThat(sampleTask.getCompleted()).isTrue();
        verify(taskRepository).save(sampleTask);
    }

    @Test
    @DisplayName("Deve deletar tarefa existente por ID")
    void delete_ShouldRemoveTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(sampleTask));
        doNothing().when(taskRepository).delete(sampleTask);

        taskService.delete(1L);

        verify(taskRepository).delete(sampleTask);
    }
}

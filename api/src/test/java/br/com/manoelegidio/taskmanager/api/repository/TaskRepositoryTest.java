package br.com.manoelegidio.taskmanager.api.repository;

import br.com.manoelegidio.taskmanager.api.dto.TaskFilterDTO;
import br.com.manoelegidio.taskmanager.api.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Deve salvar e buscar tarefa por ID")
    void saveAndFindById() {
        Task task = Task.builder()
                .title("Testar repositório")
                .description("Persistência com H2")
                .completed(false)
                .build();

        Task saved = taskRepository.save(task);

        assertThat(saved.getId()).isNotNull();
        assertThat(taskRepository.findById(saved.getId())).isPresent();
    }

    @Test
    @DisplayName("Deve filtrar tarefas por status de conclusão")
    void findByCompleted() {
        Task t1 = Task.builder().title("Concluída").description("Descrição 1").completed(true).build();
        Task t2 = Task.builder().title("Pendente").description("Descrição 2").completed(false).build();
        taskRepository.saveAll(List.of(t1, t2));

        List<Task> completed = taskRepository.findByCompleted(true);
        assertThat(completed).hasSize(1);
        assertThat(completed.get(0).getTitle()).isEqualTo("Concluída");
    }

    @Test
    @DisplayName("Deve buscar tarefas com filtros dinâmicos via Specification")
    void search_WithDynamicFilters() {
        Task t1 = Task.builder().title("Estudar Spring Boot").description("Desenvolvimento backend API").completed(true).build();
        Task t2 = Task.builder().title("Estudar Angular").description("Desenvolvimento frontend SPA").completed(false).build();
        taskRepository.saveAll(List.of(t1, t2));

        Pageable pageable = PageRequest.of(0, 10);

        TaskFilterDTO filterTitle = new TaskFilterDTO("spring", null, null);
        Page<Task> resultTitle = taskRepository.search(filterTitle, pageable);
        assertThat(resultTitle.getContent()).hasSize(1);
        assertThat(resultTitle.getContent().get(0).getTitle()).isEqualTo("Estudar Spring Boot");

        TaskFilterDTO filterDesc = new TaskFilterDTO(null, "frontend", null);
        Page<Task> resultDesc = taskRepository.search(filterDesc, pageable);
        assertThat(resultDesc.getContent()).hasSize(1);

        TaskFilterDTO filterCompleted = new TaskFilterDTO(null, null, true);
        Page<Task> resultCompleted = taskRepository.search(filterCompleted, pageable);
        assertThat(resultCompleted.getContent()).hasSize(1);

        TaskFilterDTO filterEmpty = new TaskFilterDTO(null, null, null);
        Page<Task> resultEmptyFilters = taskRepository.search(filterEmpty, pageable);
        assertThat(resultEmptyFilters.getContent()).hasSize(2);
    }
}

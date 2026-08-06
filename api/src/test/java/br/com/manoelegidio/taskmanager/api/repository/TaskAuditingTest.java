package br.com.manoelegidio.taskmanager.api.repository;

import br.com.manoelegidio.taskmanager.api.config.JpaConfig;
import br.com.manoelegidio.taskmanager.api.model.Task;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class TaskAuditingTest {

    @Autowired
    private TaskRepository taskRepository;

    @Test
    @DisplayName("Deve preencher createdAt e updatedAt automaticamente via Spring Data JPA Auditing ao salvar")
    void shouldPopulateAuditDatesOnSave() {
        Task task = Task.builder()
                .title("Tarefa Auditada")
                .description("Descrição do teste de auditoria")
                .completed(false)
                .build();

        Task savedTask = taskRepository.saveAndFlush(task);

        assertThat(savedTask.getCreatedAt()).isNotNull();
        assertThat(savedTask.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Deve atualizar updatedAt automaticamente ao alterar a tarefa")
    void shouldUpdateLastModifiedDateOnUpdate() throws InterruptedException {
        Task task = Task.builder()
                .title("Tarefa para Atualizar")
                .description("Descrição inicial")
                .completed(false)
                .build();

        Task savedTask = taskRepository.saveAndFlush(task);
        var initialCreatedAt = savedTask.getCreatedAt();
        var initialUpdatedAt = savedTask.getUpdatedAt();

        Thread.sleep(50);

        savedTask.setCompleted(true);
        Task updatedTask = taskRepository.saveAndFlush(savedTask);

        assertThat(updatedTask.getCreatedAt()).isEqualTo(initialCreatedAt);
        assertThat(updatedTask.getUpdatedAt()).isAfterOrEqualTo(initialUpdatedAt);
    }
}

package br.com.manoelegidio.taskmanager.api.factory;

import br.com.manoelegidio.taskmanager.api.dto.TaskRequestDTO;
import br.com.manoelegidio.taskmanager.api.model.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TaskFactoryTest {

    private TaskFactory taskFactory;

    @BeforeEach
    void setUp() {
        taskFactory = new TaskFactory();
    }

    @Test
    @DisplayName("Deve criar entidade Task higienizada a partir do TaskRequestDTO")
    void create_ShouldSanitizeAndCreateTask() {
        TaskRequestDTO dto = new TaskRequestDTO("  Título com Espaços  ", "  Descrição com Espaços  ", true);

        Task task = taskFactory.create(dto);

        assertThat(task.getTitle()).isEqualTo("Título com Espaços");
        assertThat(task.getDescription()).isEqualTo("Descrição com Espaços");
        assertThat(task.getCompleted()).isTrue();
    }

    @Test
    @DisplayName("Deve tratar campos nulos ao criar Task")
    void create_ShouldHandleNullFields() {
        TaskRequestDTO dto = new TaskRequestDTO("Título", null, null);

        Task task = taskFactory.create(dto);

        assertThat(task.getTitle()).isEqualTo("Título");
        assertThat(task.getDescription()).isNull();
        assertThat(task.getCompleted()).isFalse();
    }

    @Test
    @DisplayName("Deve atualizar entidade Task existente higienizando campos")
    void updateEntity_ShouldUpdateTaskFields() {
        Task existingTask = Task.builder().id(1L).title("Antigo Título").description("Antiga Descrição").completed(false).build();
        TaskRequestDTO updateDTO = new TaskRequestDTO("  Novo Título  ", "  Nova Descrição  ", true);

        taskFactory.updateEntity(existingTask, updateDTO);

        assertThat(existingTask.getTitle()).isEqualTo("Novo Título");
        assertThat(existingTask.getDescription()).isEqualTo("Nova Descrição");
        assertThat(existingTask.getCompleted()).isTrue();
    }
}

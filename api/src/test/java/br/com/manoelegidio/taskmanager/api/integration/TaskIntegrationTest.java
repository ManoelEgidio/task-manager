package br.com.manoelegidio.taskmanager.api.integration;

import br.com.manoelegidio.taskmanager.api.dto.TaskRequestDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskResponseDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers(disabledWithoutDocker = true)
@ActiveProfiles("local")
class TaskIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine");

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("Deve executar o ciclo de vida completo da Tarefa (CRUD) conectando no PostgreSQL real via Testcontainers")
    void fullTaskLifecycle_OnRealPostgreSQL() {
        TaskRequestDTO createRequest = new TaskRequestDTO("Testar com PostgreSQL Real", "Validação com Testcontainers", false);
        ResponseEntity<TaskResponseDTO> createResponse = restTemplate.postForEntity("/api/v1/tasks", createRequest, TaskResponseDTO.class);

        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(createResponse.getBody()).isNotNull();
        Long taskId = createResponse.getBody().id();
        assertThat(taskId).isNotNull();
        assertThat(createResponse.getBody().title()).isEqualTo("Testar com PostgreSQL Real");

        ResponseEntity<TaskResponseDTO> getResponse = restTemplate.getForEntity("/api/v1/tasks/" + taskId, TaskResponseDTO.class);

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().description()).isEqualTo("Validação com Testcontainers");

        ResponseEntity<TaskResponseDTO> toggleResponse = restTemplate.exchange(
                "/api/v1/tasks/" + taskId + "/toggle",
                HttpMethod.PATCH,
                HttpEntity.EMPTY,
                TaskResponseDTO.class
        );

        assertThat(toggleResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(toggleResponse.getBody()).isNotNull();
        assertThat(toggleResponse.getBody().completed()).isTrue();

        ResponseEntity<Void> deleteResponse = restTemplate.exchange(
                "/api/v1/tasks/" + taskId,
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                Void.class
        );

        assertThat(deleteResponse.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getAfterDelete = restTemplate.getForEntity("/api/v1/tasks/" + taskId, String.class);
        assertThat(getAfterDelete.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Deve buscar e filtrar tarefas via GET /api/v1/tasks conectando no PostgreSQL real")
    void searchTasks_OnRealPostgreSQL() {
        TaskRequestDTO t1 = new TaskRequestDTO("Aprender Spring Boot", "Desenvolvimento backend", true);
        TaskRequestDTO t2 = new TaskRequestDTO("Aprender Angular 20", "Desenvolvimento frontend", false);

        restTemplate.postForEntity("/api/v1/tasks", t1, TaskResponseDTO.class);
        restTemplate.postForEntity("/api/v1/tasks", t2, TaskResponseDTO.class);

        ResponseEntity<String> searchResponse = restTemplate.getForEntity("/api/v1/tasks?title=Spring&completed=true", String.class);

        assertThat(searchResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(searchResponse.getBody()).contains("Aprender Spring Boot");
        assertThat(searchResponse.getBody()).doesNotContain("Aprender Angular 20");
    }

    @Test
    @DisplayName("Deve retornar o resumo estatístico das tarefas via GET /api/v1/tasks/summary no PostgreSQL real")
    void getSummary_OnRealPostgreSQL() {
        TaskRequestDTO t1 = new TaskRequestDTO("Tarefa 1 Integrada", "Descrição", true);
        TaskRequestDTO t2 = new TaskRequestDTO("Tarefa 2 Integrada", "Descrição", false);

        restTemplate.postForEntity("/api/v1/tasks", t1, TaskResponseDTO.class);
        restTemplate.postForEntity("/api/v1/tasks", t2, TaskResponseDTO.class);

        ResponseEntity<br.com.manoelegidio.taskmanager.api.dto.TaskSummaryDTO> summaryResponse =
                restTemplate.getForEntity("/api/v1/tasks/summary", br.com.manoelegidio.taskmanager.api.dto.TaskSummaryDTO.class);

        assertThat(summaryResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(summaryResponse.getBody()).isNotNull();
        assertThat(summaryResponse.getBody().total()).isGreaterThanOrEqualTo(2L);
    }
}

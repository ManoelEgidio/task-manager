package br.com.manoelegidio.taskmanager.api.controller;

import br.com.manoelegidio.taskmanager.api.dto.TaskRequestDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskResponseDTO;
import br.com.manoelegidio.taskmanager.api.exception.ResourceNotFoundException;
import br.com.manoelegidio.taskmanager.api.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TaskService taskService;

    private TaskResponseDTO sampleResponse;

    @BeforeEach
    void setUp() {
        sampleResponse = new TaskResponseDTO(1L, "Testar Controller", "Criar endpoints REST", false, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
    }

    @Test
    @DisplayName("GET /api/v1/tasks/summary deve retornar resumo estatístico com status 200 OK")
    void getSummary_ShouldReturn200AndSummary() throws Exception {
        br.com.manoelegidio.taskmanager.api.dto.TaskSummaryDTO summary =
                new br.com.manoelegidio.taskmanager.api.dto.TaskSummaryDTO(10L, 6L, 4L);
        when(taskService.getSummary()).thenReturn(summary);

        mockMvc.perform(get("/api/v1/tasks/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(10))
                .andExpect(jsonPath("$.pending").value(6))
                .andExpect(jsonPath("$.completed").value(4));
    }

    @Test
    @DisplayName("GET /api/v1/tasks deve retornar paginação com status 200 OK")
    void search_ShouldReturn200AndPage() throws Exception {
        when(taskService.search(any(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(new PageImpl<>(List.of(sampleResponse)));

        mockMvc.perform(get("/api/v1/tasks?title=Testar&description=REST&completed=false"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Testar Controller"));
    }

    @Test
    @DisplayName("GET /api/v1/tasks/{id} deve retornar 404 quando não encontrado")
    void findById_ShouldReturn404_WhenNotFound() throws Exception {
        when(taskService.findById(99L)).thenThrow(new ResourceNotFoundException("Tarefa com ID 99 não foi encontrada."));

        mockMvc.perform(get("/api/v1/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    @DisplayName("POST /api/v1/tasks deve criar tarefa e retornar status 201")
    void create_ShouldReturn201() throws Exception {
        TaskRequestDTO request = new TaskRequestDTO("Testar Controller", "Criar endpoints REST", false);
        when(taskService.create(any(TaskRequestDTO.class))).thenReturn(sampleResponse);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Testar Controller"));
    }

    @Test
    @DisplayName("POST /api/v1/tasks deve retornar 400 Bad Request se título tiver menos de 3 caracteres")
    void create_ShouldReturn400_WhenTitleTooShort() throws Exception {
        TaskRequestDTO invalidRequest = new TaskRequestDTO("Ab", "Descrição", false);

        mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    @DisplayName("PUT /api/v1/tasks/{id} deve atualizar tarefa e retornar 200 OK")
    void update_ShouldReturn200() throws Exception {
        TaskRequestDTO updateRequest = new TaskRequestDTO("Título Atualizado", "Descrição Atualizada", true);
        TaskResponseDTO updatedResponse = new TaskResponseDTO(1L, "Título Atualizado", "Descrição Atualizada", true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        when(taskService.update(eq(1L), any(TaskRequestDTO.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título Atualizado"));
    }

    @Test
    @DisplayName("PATCH /api/v1/tasks/{id}/toggle deve alterar status e retornar 200")
    void toggleCompleted_ShouldReturn200() throws Exception {
        TaskResponseDTO toggled = new TaskResponseDTO(1L, "Testar Controller", "Criar endpoints REST", true, java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        when(taskService.toggleCompleted(1L)).thenReturn(toggled);

        mockMvc.perform(patch("/api/v1/tasks/1/toggle"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.completed").value(true));
    }

    @Test
    @DisplayName("DELETE /api/v1/tasks/{id} deve retornar 204 No Content")
    void delete_ShouldReturn204() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/v1/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar status 500 quando ocorrer erro inesperado no servidor")
    void shouldReturn500_OnUnexpectedException() throws Exception {
        when(taskService.findById(1L)).thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/api/v1/tasks/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500));
    }
}

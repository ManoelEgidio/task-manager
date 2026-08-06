package br.com.manoelegidio.taskmanager.api.controller;

import br.com.manoelegidio.taskmanager.api.dto.TaskFilterDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskRequestDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskResponseDTO;
import br.com.manoelegidio.taskmanager.api.dto.TaskSummaryDTO;
import br.com.manoelegidio.taskmanager.api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tarefas", description = "Endpoints para gerenciamento de tarefas (CRUD e Busca)")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Obter resumo estatístico das tarefas", description = "Retorna a contagem agregada de tarefas totais, pendentes e concluídas.")
    @GetMapping("/summary")
    public ResponseEntity<TaskSummaryDTO> getSummary() {
        return ResponseEntity.ok(taskService.getSummary());
    }

    @Operation(summary = "Listar e filtrar tarefas", description = "Lista tarefas com suporte a paginação e filtros dinâmicos por título, descrição e status de conclusão.")
    @GetMapping
    public Page<TaskResponseDTO> search(@RequestParam(required = false) String title,
                                        @RequestParam(required = false) String description,
                                        @RequestParam(required = false) Boolean completed,
                                        @RequestParam(required = false, defaultValue = "0") Integer page,
                                        @RequestParam(required = false, defaultValue = "20") Integer size,
                                        @RequestParam(required = false, defaultValue = "id") String sort,
                                        @RequestParam(required = false, defaultValue = "desc") String direction) {

        TaskFilterDTO filter = new TaskFilterDTO(title, description, completed);
        return taskService.search(filter, page, size, sort, direction);
    }

    @Operation(summary = "Obter tarefa por ID", description = "Retorna os dados detalhados de uma tarefa cadastrada pelo seu identificador único.")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.findById(id));
    }

    @Operation(summary = "Criar nova tarefa", description = "Cadastra uma nova tarefa exigindo título (mín. 3 caracteres) e descrição opcional.")
    @PostMapping
    public ResponseEntity<TaskResponseDTO> create(@Valid @RequestBody TaskRequestDTO requestDTO) {
        TaskResponseDTO created = taskService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Atualizar tarefa", description = "Atualiza o título, descrição e status de uma tarefa existente pelo seu ID.")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequestDTO requestDTO) {
        return ResponseEntity.ok(taskService.update(id, requestDTO));
    }

    @Operation(summary = "Alternar status da tarefa", description = "Alterna o status da tarefa entre concluída (true) e pendente (false).")
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<TaskResponseDTO> toggleCompleted(@PathVariable Long id) {
        return ResponseEntity.ok(taskService.toggleCompleted(id));
    }

    @Operation(summary = "Deletar tarefa", description = "Remove permanentemente a tarefa da base de dados pelo seu ID.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

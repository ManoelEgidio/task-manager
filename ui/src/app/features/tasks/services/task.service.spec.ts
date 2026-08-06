import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TaskService } from './task.service';
import { Task, TaskPage, TaskRequest, TaskSummary } from '../models/task.model';
import { environment } from '../../../../environments/environment';

describe('TaskService (Serviço de Tarefas)', () => {
  let service: TaskService;
  let httpMock: HttpTestingController;
  const apiUrl = environment.apiUrl;

  const mockTask: Task = {
    id: 1,
    title: 'Tarefa de Teste',
    description: 'Descrição de teste',
    completed: false
  };

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        TaskService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });

    service = TestBed.inject(TaskService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve ser instanciado com sucesso', () => {
    expect(service).toBeTruthy();
  });

  it('deve buscar o resumo estatístico de tarefas', () => {
    const mockSummary: TaskSummary = { total: 10, pending: 6, completed: 4 };

    service.getTaskSummary().subscribe((summary) => {
      expect(summary).toEqual(mockSummary);
    });

    const req = httpMock.expectOne(`${apiUrl}/summary`);
    expect(req.request.method).toBe('GET');
    req.flush(mockSummary);
  });

  it('deve listar tarefas com parâmetros de paginação e filtro', () => {
    const mockPage: TaskPage = {
      content: [mockTask],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0,
      first: true,
      last: true,
      empty: false
    };

    service.getTasks({ page: 0, size: 10 }).subscribe((page) => {
      expect(page.content.length).toBe(1);
      expect(page.content[0]).toEqual(mockTask);
    });

    const req = httpMock.expectOne((r) => r.url === apiUrl);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('10');
    req.flush(mockPage);
  });

  it('deve cadastrar uma nova tarefa', () => {
    const newRequest: TaskRequest = { title: 'Nova Tarefa', description: 'Descrição' };

    service.createTask(newRequest).subscribe((created) => {
      expect(created).toEqual(mockTask);
    });

    const req = httpMock.expectOne(apiUrl);
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(newRequest);
    req.flush(mockTask);
  });

  it('deve atualizar uma tarefa existente', () => {
    const updateReq: TaskRequest = { title: 'Tarefa Atualizada', description: 'Nova Descrição' };
    const updatedTask = { ...mockTask, title: 'Tarefa Atualizada' };

    service.updateTask(1, updateReq).subscribe((res) => {
      expect(res.title).toBe('Tarefa Atualizada');
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('PUT');
    expect(req.request.body).toEqual(updateReq);
    req.flush(updatedTask);
  });

  it('deve alternar o status de conclusão da tarefa', () => {
    const toggledTask = { ...mockTask, completed: true };

    service.toggleTaskCompleted(1).subscribe((res) => {
      expect(res.completed).toBeTrue();
    });

    const req = httpMock.expectOne(`${apiUrl}/1/toggle`);
    expect(req.request.method).toBe('PATCH');
    req.flush(toggledTask);
  });

  it('deve excluir uma tarefa pelo ID', () => {
    service.deleteTask(1).subscribe((res) => {
      expect(res).toBeNull();
    });

    const req = httpMock.expectOne(`${apiUrl}/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});

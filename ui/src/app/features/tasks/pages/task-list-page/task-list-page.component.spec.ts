import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskListPageComponent } from './task-list-page.component';
import { TaskService } from '../../services/task.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { LoadingService } from '../../../../core/services/loading.service';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { TaskPage, TaskSummary } from '../../models/task.model';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('TaskListPageComponent (Página Principal de Listagem de Tarefas)', () => {
  let component: TaskListPageComponent;
  let fixture: ComponentFixture<TaskListPageComponent>;
  let mockTaskService: jasmine.SpyObj<TaskService>;
  let mockNotificationService: jasmine.SpyObj<NotificationService>;
  let mockLoadingService: jasmine.SpyObj<LoadingService>;
  let mockDialog: jasmine.SpyObj<MatDialog>;

  const mockSummary: TaskSummary = { total: 5, pending: 3, completed: 2 };
  const mockPage: TaskPage = {
    content: [
      { id: 1, title: 'Tarefa 1', description: 'Desc 1', completed: false },
      { id: 2, title: 'Tarefa 2', description: 'Desc 2', completed: true }
    ],
    totalElements: 2,
    totalPages: 1,
    size: 10,
    number: 0,
    first: true,
    last: true,
    empty: false
  };

  beforeEach(async () => {
    mockTaskService = jasmine.createSpyObj('TaskService', [
      'getTasks',
      'getTaskSummary',
      'createTask',
      'updateTask',
      'toggleTaskCompleted',
      'deleteTask'
    ]);
    mockNotificationService = jasmine.createSpyObj('NotificationService', ['showSuccess', 'showError', 'showInfo']);
    mockLoadingService = jasmine.createSpyObj('LoadingService', ['isLoading', 'show', 'hide']);
    mockDialog = jasmine.createSpyObj('MatDialog', ['open']);

    mockTaskService.getTasks.and.returnValue(of(mockPage));
    mockTaskService.getTaskSummary.and.returnValue(of(mockSummary));

    await TestBed.configureTestingModule({
      imports: [TaskListPageComponent, NoopAnimationsModule],
      providers: [
        { provide: TaskService, useValue: mockTaskService },
        { provide: NotificationService, useValue: mockNotificationService },
        { provide: LoadingService, useValue: mockLoadingService },
        { provide: MatDialog, useValue: mockDialog }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskListPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar a página de listagem', () => {
    expect(component).toBeTruthy();
  });

  it('deve carregar tarefas e métricas ao inicializar', () => {
    expect(mockTaskService.getTasks).toHaveBeenCalled();
    expect(mockTaskService.getTaskSummary).toHaveBeenCalled();
    expect(component.tasksList().length).toBe(2);
    expect(component.totalCount()).toBe(5);
  });

  it('deve atualizar o filtro ativo e recarregar tarefas', () => {
    component.onFilterChange('PENDING');
    expect(component.activeFilter()).toBe('PENDING');
    expect(mockTaskService.getTasks).toHaveBeenCalled();
  });

  it('deve atualizar a ordenação e recarregar tarefas', () => {
    component.onSortChange('title,asc');
    expect(component.sortKey()).toBe('title,asc');
    expect(mockTaskService.getTasks).toHaveBeenCalled();
  });

  it('deve alternar status da tarefa com atualização otimista na interface', () => {
    const taskToToggle = mockPage.content[0];
    mockTaskService.toggleTaskCompleted.and.returnValue(of({ ...taskToToggle, completed: true }));

    component.onToggleTask(taskToToggle);

    expect(mockTaskService.toggleTaskCompleted).toHaveBeenCalledWith(taskToToggle.id);
    expect(mockNotificationService.showSuccess).toHaveBeenCalled();
  });
});

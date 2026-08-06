import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskMetricsComponent } from './task-metrics.component';
import { ComponentRef } from '@angular/core';

describe('TaskMetricsComponent (Componente de Métricas de Tarefas)', () => {
  let component: TaskMetricsComponent;
  let componentRef: ComponentRef<TaskMetricsComponent>;
  let fixture: ComponentFixture<TaskMetricsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskMetricsComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskMetricsComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
  });

  it('deve criar o componente', () => {
    expect(component).toBeTruthy();
  });

  it('deve calcular 0% de conclusão quando o total de tarefas for 0', () => {
    componentRef.setInput('totalTasks', 0);
    componentRef.setInput('completedCount', 0);
    fixture.detectChanges();

    expect(component.completionPercentage()).toBe(0);
  });

  it('deve calcular a porcentagem correta de tarefas concluídas', () => {
    componentRef.setInput('totalTasks', 10);
    componentRef.setInput('pendingCount', 2);
    componentRef.setInput('completedCount', 8);
    fixture.detectChanges();

    expect(component.completionPercentage()).toBe(80);
  });

  it('deve arredondar a porcentagem para um número inteiro', () => {
    componentRef.setInput('totalTasks', 3);
    componentRef.setInput('completedCount', 1);
    fixture.detectChanges();

    expect(component.completionPercentage()).toBe(33);
  });
});

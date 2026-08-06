import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskCardComponent } from './task-card.component';
import { Task } from '../../models/task.model';
import { ComponentRef } from '@angular/core';

describe('TaskCardComponent (Componente de Card de Tarefa)', () => {
  let component: TaskCardComponent;
  let componentRef: ComponentRef<TaskCardComponent>;
  let fixture: ComponentFixture<TaskCardComponent>;

  const mockTask: Task = {
    id: 101,
    title: 'Tarefa para Teste de Componente',
    description: 'Descrição de teste',
    completed: false
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [TaskCardComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskCardComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;

    componentRef.setInput('task', mockTask);
    fixture.detectChanges();
  });

  it('deve criar o componente com a tarefa informada no input', () => {
    expect(component).toBeTruthy();
    expect(component.task()).toEqual(mockTask);
  });

  it('deve emitir o evento toggle ao alternar a conclusão da tarefa', () => {
    spyOn(component.toggle, 'emit');

    component.onToggle();

    expect(component.toggle.emit).toHaveBeenCalledWith(mockTask);
  });

  it('deve emitir o evento edit ao clicar na opção de edição', () => {
    spyOn(component.edit, 'emit');

    component.onEdit();

    expect(component.edit.emit).toHaveBeenCalledWith(mockTask);
  });

  it('deve emitir o evento delete ao clicar na opção de exclusão', () => {
    spyOn(component.delete, 'emit');

    component.onDelete();

    expect(component.delete.emit).toHaveBeenCalledWith(mockTask);
  });
});

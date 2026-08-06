import { ComponentFixture, TestBed } from '@angular/core/testing';
import { TaskFormDialogComponent, TaskFormDialogData } from './task-form-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { NoopAnimationsModule } from '@angular/platform-browser/animations';

describe('TaskFormDialogComponent (Componente de Modal de Formulário)', () => {
  let component: TaskFormDialogComponent;
  let fixture: ComponentFixture<TaskFormDialogComponent>;
  let mockDialogRef: jasmine.SpyObj<MatDialogRef<TaskFormDialogComponent>>;

  const mockDialogData: TaskFormDialogData = {
    mode: 'CREATE'
  };

  beforeEach(async () => {
    mockDialogRef = jasmine.createSpyObj('MatDialogRef', ['close']);

    await TestBed.configureTestingModule({
      imports: [TaskFormDialogComponent, NoopAnimationsModule],
      providers: [
        { provide: MAT_DIALOG_DATA, useValue: mockDialogData },
        { provide: MatDialogRef, useValue: mockDialogRef }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(TaskFormDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('deve criar o componente de modal', () => {
    expect(component).toBeTruthy();
    expect(component.isEdit).toBeFalse();
  });

  it('deve validar o título como obrigatório com no mínimo 3 caracteres', () => {
    const titleControl = component.form.get('title');

    titleControl?.setValue('');
    expect(titleControl?.valid).toBeFalse();

    titleControl?.setValue('ab');
    expect(titleControl?.valid).toBeFalse();

    titleControl?.setValue('abc');
    expect(titleControl?.valid).toBeTrue();
  });

  it('não deve fechar o modal ao tentar submeter formulário inválido', () => {
    component.form.get('title')?.setValue('a');
    component.onSubmit();

    expect(mockDialogRef.close).not.toHaveBeenCalled();
  });

  it('deve fechar o modal retornando os dados limpos ao submeter formulário válido', () => {
    component.form.get('title')?.setValue(' Nova Tarefa ');
    component.form.get('description')?.setValue(' Descrição da tarefa ');
    component.onSubmit();

    expect(mockDialogRef.close).toHaveBeenCalledWith({
      title: 'Nova Tarefa',
      description: 'Descrição da tarefa',
      completed: false
    });
  });

  it('deve fechar o modal retornando null ao cancelar', () => {
    component.onCancel();
    expect(mockDialogRef.close).toHaveBeenCalledWith(null);
  });
});

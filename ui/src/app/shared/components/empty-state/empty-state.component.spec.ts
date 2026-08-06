import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EmptyStateComponent } from './empty-state.component';
import { ComponentRef } from '@angular/core';

describe('EmptyStateComponent (Componente de Estado Vazio)', () => {
  let component: EmptyStateComponent;
  let componentRef: ComponentRef<EmptyStateComponent>;
  let fixture: ComponentFixture<EmptyStateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmptyStateComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(EmptyStateComponent);
    component = fixture.componentInstance;
    componentRef = fixture.componentRef;
    fixture.detectChanges();
  });

  it('deve criar o componente com os valores padrão', () => {
    expect(component).toBeTruthy();
    expect(component.icon()).toBe('assignment_late');
    expect(component.title()).toBe('Nenhuma tarefa encontrada');
  });

  it('deve atualizar o título e ícone quando fornecidos via input', () => {
    componentRef.setInput('icon', 'search_off');
    componentRef.setInput('title', 'Busca sem resultados');
    fixture.detectChanges();

    expect(component.icon()).toBe('search_off');
    expect(component.title()).toBe('Busca sem resultados');
  });
});

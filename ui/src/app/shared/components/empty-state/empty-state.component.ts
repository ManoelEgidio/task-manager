import { Component, ChangeDetectionStrategy, input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-empty-state',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './empty-state.component.html',
  styleUrl: './empty-state.component.scss'
})
export class EmptyStateComponent {
  readonly icon = input<string>('assignment_late');
  readonly title = input<string>('Nenhuma tarefa encontrada');
  readonly description = input<string>('Você não possui tarefas cadastradas no momento ou nenhuma corresponde ao filtro aplicado.');
}

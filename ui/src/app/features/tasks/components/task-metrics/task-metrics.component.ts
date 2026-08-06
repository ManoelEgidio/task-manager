import { Component, ChangeDetectionStrategy, input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatCardModule } from '@angular/material/card';
import { MatIconModule } from '@angular/material/icon';

@Component({
  selector: 'app-task-metrics',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIconModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './task-metrics.component.html',
  styleUrl: './task-metrics.component.scss'
})
export class TaskMetricsComponent {
  readonly totalTasks = input<number>(0);
  readonly pendingCount = input<number>(0);
  readonly completedCount = input<number>(0);

  readonly completionPercentage = computed(() => {
    const total = this.totalTasks();
    if (total === 0) return 0;
    return Math.round((this.completedCount() / total) * 100);
  });
}

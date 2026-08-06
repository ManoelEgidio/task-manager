import { Component, ChangeDetectionStrategy, inject, signal, computed, OnInit, DestroyRef } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { CommonModule } from '@angular/common';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatOptionModule } from '@angular/material/core';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { debounceTime, distinctUntilChanged } from 'rxjs/operators';

import { TaskService } from '../../services/task.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { LoadingService } from '../../../../core/services/loading.service';
import { FilterStatus, Task, TaskFilter, TaskRequest } from '../../models/task.model';
import { TaskMetricsComponent } from '../../components/task-metrics/task-metrics.component';
import { TaskCardComponent } from '../../components/task-card/task-card.component';
import { EmptyStateComponent } from '../../../../shared/components/empty-state/empty-state.component';
import { ConfirmDialogComponent, ConfirmDialogData } from '../../../../shared/components/confirm-dialog/confirm-dialog.component';
import { TaskFormDialogComponent, TaskFormDialogData } from '../../components/task-form-dialog/task-form-dialog.component';
import { getDialogConfig } from '../../../../shared/utils/dialog.util';

@Component({
  selector: 'app-task-list-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatOptionModule,
    MatDialogModule,
    TaskMetricsComponent,
    TaskCardComponent,
    EmptyStateComponent
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './task-list-page.component.html',
  styleUrl: './task-list-page.component.scss'
})
export class TaskListPageComponent implements OnInit {
  private readonly taskService = inject(TaskService);
  private readonly notificationService = inject(NotificationService);
  readonly loadingService = inject(LoadingService);
  private readonly dialog = inject(MatDialog);
  private readonly destroyRef = inject(DestroyRef);

  readonly tasksList = signal<Task[]>([]);
  readonly activeFilter = signal<FilterStatus>('ALL');
  readonly sortKey = signal<string>('createdAt,desc');
  readonly searchControl = new FormControl<string>('', { nonNullable: true });

  readonly currentPage = signal<number>(0);
  readonly pageSize = signal<number>(10);
  readonly totalFilteredCount = signal<number>(0);

  readonly totalCount = signal<number>(0);
  readonly pendingCount = signal<number>(0);
  readonly completedCount = signal<number>(0);

  readonly hasMoreTasks = computed(() => this.tasksList().length < this.totalFilteredCount());

  ngOnInit(): void {
    this.loadTasks(false);
    this.loadMetrics();

    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(() => {
        this.currentPage.set(0);
        this.loadTasks(false);
      });
  }

  loadTasks(isLoadMore = false): void {
    const [sortField, sortDirection] = this.sortKey().split(',');
    const pageToFetch = isLoadMore ? this.currentPage() : 0;
    const sizeToFetch = isLoadMore ? this.pageSize() : (this.currentPage() + 1) * this.pageSize();

    const filter: TaskFilter = {
      page: pageToFetch,
      size: sizeToFetch,
      sort: sortField,
      direction: sortDirection as 'asc' | 'desc'
    };

    const search = this.searchControl.value.trim();
    if (search) {
      filter.title = search;
    }

    const status = this.activeFilter();
    if (status === 'PENDING') {
      filter.completed = false;
    } else if (status === 'COMPLETED') {
      filter.completed = true;
    }

    this.taskService.getTasks(filter).subscribe({
      next: (page) => {
        if (isLoadMore) {
          this.tasksList.update(current => [...current, ...page.content]);
        } else {
          this.tasksList.set(page.content);
        }
        this.totalFilteredCount.set(page.totalElements);
      },
      error: () => {}
    });
  }

  onLoadMore(): void {
    if (this.hasMoreTasks() && !this.loadingService.isLoading()) {
      this.currentPage.update(p => p + 1);
      this.loadTasks(true);
    }
  }

  loadMetrics(): void {
    this.taskService.getTaskSummary().subscribe({
      next: (summary) => {
        this.totalCount.set(summary.total);
        this.pendingCount.set(summary.pending);
        this.completedCount.set(summary.completed);
      },
      error: () => {}
    });
  }

  onFilterChange(status: FilterStatus): void {
    this.activeFilter.set(status);
    this.currentPage.set(0);
    this.loadTasks(false);
  }

  onSortChange(key: string): void {
    this.sortKey.set(key);
    this.currentPage.set(0);
    this.loadTasks(false);
  }

  openCreateDialog(): void {
    const dialogData: TaskFormDialogData = { mode: 'CREATE' };
    const dialogRef = this.dialog.open(
      TaskFormDialogComponent,
      getDialogConfig('500px', dialogData)
    );

    dialogRef.afterClosed().subscribe((result: TaskRequest | null) => {
      if (result) {
        this.taskService.createTask(result).subscribe({
          next: () => {
            this.notificationService.showSuccess('Tarefa criada com sucesso!');
            this.currentPage.set(0);
            this.loadTasks(false);
            this.loadMetrics();
          }
        });
      }
    });
  }

  openEditDialog(task: Task): void {
    const dialogData: TaskFormDialogData = { mode: 'EDIT', task };
    const dialogRef = this.dialog.open(
      TaskFormDialogComponent,
      getDialogConfig('500px', dialogData)
    );

    dialogRef.afterClosed().subscribe((result: TaskRequest | null) => {
      if (result) {
        this.taskService.updateTask(task.id, result).subscribe({
          next: () => {
            this.notificationService.showSuccess('Tarefa atualizada com sucesso!');
            this.currentPage.set(0);
            this.loadTasks(false);
          }
        });
      }
    });
  }

  onToggleTask(task: Task): void {
    const previousCompletedState = task.completed;
    const newCompletedState = !previousCompletedState;

    this.tasksList.update(list =>
      list.map(t => (t.id === task.id ? { ...t, completed: newCompletedState } : t))
    );

    if (newCompletedState) {
      this.pendingCount.update(c => Math.max(0, c - 1));
      this.completedCount.update(c => c + 1);
    } else {
      this.pendingCount.update(c => c + 1);
      this.completedCount.update(c => Math.max(0, c - 1));
    }

    this.taskService.toggleTaskCompleted(task.id).subscribe({
      next: (updated) => {
        const msg = updated.completed ? 'Tarefa marcada como concluída!' : 'Tarefa marcada como pendente!';
        this.notificationService.showSuccess(msg);
        if (this.activeFilter() !== 'ALL') {
          this.currentPage.set(0);
          this.loadTasks(false);
        }
      },
      error: () => {
        this.currentPage.set(0);
        this.loadTasks(false);
        this.loadMetrics();
      }
    });
  }

  confirmDeleteTask(task: Task): void {
    const dialogData: ConfirmDialogData = {
      title: 'Excluir Tarefa',
      message: `Tem certeza que deseja excluir permanentemente a tarefa "${task.title}"?`
    };

    const dialogRef = this.dialog.open(
      ConfirmDialogComponent,
      getDialogConfig('420px', dialogData)
    );

    dialogRef.afterClosed().subscribe((confirmed: boolean) => {
      if (confirmed) {
        this.tasksList.update(list => list.filter(t => t.id !== task.id));
        this.totalFilteredCount.update(c => Math.max(0, c - 1));

        this.totalCount.update(c => Math.max(0, c - 1));
        if (task.completed) {
          this.completedCount.update(c => Math.max(0, c - 1));
        } else {
          this.pendingCount.update(c => Math.max(0, c - 1));
        }

        this.taskService.deleteTask(task.id).subscribe({
          next: () => {
            this.notificationService.showSuccess('Tarefa excluída com sucesso!');
            this.currentPage.set(0);
            this.loadTasks(false);
          },
          error: () => {
            this.currentPage.set(0);
            this.loadTasks(false);
            this.loadMetrics();
          }
        });
      }
    });
  }
}

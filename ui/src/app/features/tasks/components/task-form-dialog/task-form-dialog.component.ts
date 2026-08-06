import { Component, ChangeDetectionStrategy, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogModule, MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { Task, TaskRequest } from '../../models/task.model';

export interface TaskFormDialogData {
  mode: 'CREATE' | 'EDIT';
  task?: Task;
}

@Component({
  selector: 'app-task-form-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatDialogModule,
    MatFormFieldModule,
    MatInputModule,
    MatCheckboxModule,
    MatButtonModule,
    MatIconModule
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './task-form-dialog.component.html',
  styleUrl: './task-form-dialog.component.scss'
})
export class TaskFormDialogComponent implements OnInit {
  readonly data = inject<TaskFormDialogData>(MAT_DIALOG_DATA);
  private readonly dialogRef = inject(MatDialogRef<TaskFormDialogComponent>);
  private readonly fb = inject(FormBuilder).nonNullable;

  form!: FormGroup;

  get isEdit(): boolean {
    return this.data?.mode === 'EDIT';
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      title: [
        this.data?.task?.title || '',
        [Validators.required, Validators.minLength(3)]
      ],
      description: [this.data?.task?.description || ''],
      completed: [this.data?.task?.completed ?? false]
    });
  }

  onSubmit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const raw = this.form.value;
    const taskRequest: TaskRequest = {
      title: raw.title.trim(),
      description: raw.description ? raw.description.trim() : null,
      completed: raw.completed
    };

    this.dialogRef.close(taskRequest);
  }

  onCancel(): void {
    this.dialogRef.close(null);
  }
}

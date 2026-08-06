import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Task, TaskFilter, TaskPage, TaskRequest, TaskSummary } from '../models/task.model';
import { environment } from '../../../../environments/environment';
import { buildHttpParams } from '../../../shared/utils/http-params.util';

@Injectable({
  providedIn: 'root'
})
export class TaskService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  getTaskSummary(): Observable<TaskSummary> {
    return this.http.get<TaskSummary>(`${this.apiUrl}/summary`);
  }

  getTasks(filter: TaskFilter = {}): Observable<TaskPage> {
    const params = buildHttpParams(filter);
    return this.http.get<TaskPage>(this.apiUrl, { params });
  }

  getTaskById(id: number): Observable<Task> {
    return this.http.get<Task>(`${this.apiUrl}/${id}`);
  }

  createTask(task: TaskRequest): Observable<Task> {
    return this.http.post<Task>(this.apiUrl, task);
  }

  updateTask(id: number, task: TaskRequest): Observable<Task> {
    return this.http.put<Task>(`${this.apiUrl}/${id}`, task);
  }

  toggleTaskCompleted(id: number): Observable<Task> {
    return this.http.patch<Task>(`${this.apiUrl}/${id}/toggle`, {});
  }

  deleteTask(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}



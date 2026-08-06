export interface Task {
  id: number;
  title: string;
  description?: string | null;
  completed: boolean;
  createdAt?: string;
  updatedAt?: string;
}

export interface TaskRequest {
  title: string;
  description?: string | null;
  completed?: boolean;
}

export interface TaskPage {
  content: Task[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

export interface TaskFilter {
  title?: string;
  description?: string;
  completed?: boolean | null;
  page?: number;
  size?: number;
  sort?: string;
  direction?: 'asc' | 'desc';
}

export type FilterStatus = 'ALL' | 'PENDING' | 'COMPLETED';

export interface TaskSummary {
  total: number;
  pending: number;
  completed: number;
}

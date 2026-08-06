import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';
import { NotificationService } from '../services/notification.service';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const notificationService = inject(NotificationService);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      let errorMessage = 'Ocorreu um erro inesperado ao se comunicar com o servidor.';

      if (error.error instanceof ErrorEvent) {
        errorMessage = `Erro de conexão: ${error.error.message}`;
      } else if (error.error?.message) {
        errorMessage = error.error.message;
      } else if (error.status === 0) {
        errorMessage = 'Não foi possível conectar à API backend. Verifique se o servidor Spring Boot está ativo.';
      } else if (error.status === 404) {
        errorMessage = 'O recurso solicitado não foi encontrado (404).';
      } else if (error.status === 400) {
        errorMessage = 'Falha na validação dos dados de entrada (400).';
      }

      notificationService.showError(errorMessage);
      return throwError(() => error);
    })
  );
};

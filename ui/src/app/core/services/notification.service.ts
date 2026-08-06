import { Injectable, inject } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly snackBar = inject(MatSnackBar);

  showSuccess(message: string, duration = 3500): void {
    this.openSnackBar(message, 'snack-bar-success', duration);
  }

  showError(message: string, duration = 5000): void {
    this.openSnackBar(message, 'snack-bar-error', duration);
  }

  showInfo(message: string, duration = 3500): void {
    this.openSnackBar(message, 'snack-bar-info', duration);
  }

  private openSnackBar(message: string, panelClass: string, duration: number): void {
    const config: MatSnackBarConfig = {
      duration,
      horizontalPosition: 'end',
      verticalPosition: 'top',
      panelClass: [panelClass]
    };
    this.snackBar.open(message, 'Fechar', config);
  }
}

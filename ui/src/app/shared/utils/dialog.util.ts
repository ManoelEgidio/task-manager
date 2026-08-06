import { MatDialogConfig } from '@angular/material/dialog';

export function getDialogConfig<T>(width = '500px', data?: T): MatDialogConfig<T> {
  return {
    width,
    panelClass: 'app-dialog',
    backdropClass: 'app-backdrop',
    data
  };
}

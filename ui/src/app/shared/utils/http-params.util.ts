import { HttpParams } from '@angular/common/http';

export function buildHttpParams<T extends object>(paramsObject: T): HttpParams {
  let params = new HttpParams();

  Object.entries(paramsObject as Record<string, unknown>).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      const formattedValue = typeof value === 'string' ? value.trim() : String(value);
      params = params.set(key, formattedValue);
    }
  });

  return params;
}

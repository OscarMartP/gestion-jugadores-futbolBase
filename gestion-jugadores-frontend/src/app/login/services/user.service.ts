import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import baserUrl from './helper';
import { Observable, of } from 'rxjs';
import { map, catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private httpClient: HttpClient) { }

  public añadirUsuario(user:any){
    return this.httpClient.post(`${baserUrl}/usuarios/`,user);
  }

  // Obtener el usuario autenticado desde backend
  public getUsuarioActual(): Observable<any> {
    return this.httpClient.get<any>(`${baserUrl}/usuarios/me`);
  }

  // Intentar obtener el id del usuario: primero decodificando el token, si no, llamando al endpoint /usuarios/me
  public getIdUsuario(): Observable<number | null> {
    const token = localStorage.getItem('token') || sessionStorage.getItem('token') || '';
    if (token) {
      try {
        const payload = JSON.parse(atob(token.split('.')[1]));
        const id = payload.id || payload.userId || payload.sub;
        if (id) {
          return of(Number(id));
        }
      } catch (e) {
        // ignore
      }
    }
    return this.getUsuarioActual().pipe(
      map(u => u?.id ?? null),
      catchError(() => of(null))
    );
  }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Partido } from '../models/partido';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class PartidoService {

  private baseURL = `${baserUrl}/api/v1/partidos`;

  constructor(private http: HttpClient) {}

  // Crear partido
  crearPartido(partido: Partido): Observable<Partido> {
    return this.http.post<Partido>(this.baseURL, partido);
  }

  // Obtener partido por ID
  obtenerPartidoPorId(id: number): Observable<Partido> {
    return this.http.get<Partido>(`${this.baseURL}/${id}`);
  }

  // Obtener partidos por equipo
  obtenerPartidosPorEquipo(equipoId: number): Observable<Partido[]> {
    return this.http.get<Partido[]>(`${this.baseURL}/equipo/${equipoId}`);
  }

  // Obtener partidos activos por equipo
  obtenerPartidosActivosPorEquipo(equipoId: number): Observable<Partido[]> {
    return this.http.get<Partido[]>(`${this.baseURL}/activos/equipo/${equipoId}`);
  }

  // Activar partido
  activarPartido(id: number): Observable<Partido> {
    return this.http.put<Partido>(`${this.baseURL}/${id}/activar`, {});
  }

  // Desactivar partido
  desactivarPartido(id: number): Observable<Partido> {
    return this.http.put<Partido>(`${this.baseURL}/${id}/desactivar`, {});
  }

  // Verificar si equipo tiene partido activo
  tienePartidoActivo(equipoId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseURL}/equipo/${equipoId}/tiene-activo`);
  }

  // Actualizar partido
  actualizarPartido(id: number, partido: Partido): Observable<Partido> {
    return this.http.put<Partido>(`${this.baseURL}/${id}`, partido);
  }

  // Eliminar partido
  eliminarPartido(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/${id}`);
  }
}

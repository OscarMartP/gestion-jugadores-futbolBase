import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { EstadisticasEquipo, EstadisticasJugadorEquipo } from '../models/estadisticas-equipo';
import { EventoJugador } from '../models/partido';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class EstadisticasService {

  private baseURL = `${baserUrl}/api/v1/estadisticas`;

  constructor(private http: HttpClient) {}

  // Obtener estadísticas de equipo desde el backend
  obtenerEstadisticasEquipo(equipoId: number, temporada?: string): Observable<any> {
    const url = temporada 
      ? `${this.baseURL}/equipo/${equipoId}?temporada=${temporada}`
      : `${this.baseURL}/equipo/${equipoId}`;
    return this.http.get<any>(url);
  }

  // Obtener estadísticas de todos los jugadores de un equipo
  obtenerEstadisticasJugadores(equipoId: number, temporada?: string): Observable<any[]> {
    const url = temporada
      ? `${this.baseURL}/equipo/${equipoId}/jugadores?temporada=${temporada}`
      : `${this.baseURL}/equipo/${equipoId}/jugadores`;
    return this.http.get<any[]>(url);
  }

  // Obtener top goleadores
  obtenerTopGoleadores(equipoId: number, limite: number = 5): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseURL}/equipo/${equipoId}/top-goleadores?limite=${limite}`);
  }

  // Obtener top asistentes
  obtenerTopAsistentes(equipoId: number, limite: number = 5): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseURL}/equipo/${equipoId}/top-asistentes?limite=${limite}`);
  }

  // Obtener resumen completo
  obtenerResumenEquipo(equipoId: number, temporada?: string): Observable<any> {
    const url = temporada
      ? `${this.baseURL}/equipo/${equipoId}/resumen?temporada=${temporada}`
      : `${this.baseURL}/equipo/${equipoId}/resumen`;
    return this.http.get<any>(url);
  }
}

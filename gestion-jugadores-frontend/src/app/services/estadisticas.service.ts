import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { 
  EstadisticasJugadorDTO, 
  EstadisticasEquipoDTO, 
  ResumenEstadisticasDTO 
} from '../models/estadisticas.model';
import { EstadisticasPartidoDTO } from '../models/estadisticas-partido.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class EstadisticasService {
  private apiUrl = `${environment.apiUrl}/estadisticas`;

  constructor(private http: HttpClient) { }

  /**
   * Obtener estadísticas de un jugador
   */
  obtenerEstadisticasJugador(jugadorId: number, temporada?: string): Observable<EstadisticasJugadorDTO> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    return this.http.get<EstadisticasJugadorDTO>(`${this.apiUrl}/jugador/${jugadorId}`, { params });
  }

  /**
   * Obtener estadísticas de todos los jugadores de un equipo
   */
  obtenerEstadisticasJugadoresEquipo(equipoId: number, temporada?: string): Observable<EstadisticasJugadorDTO[]> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    return this.http.get<EstadisticasJugadorDTO[]>(`${this.apiUrl}/equipo/${equipoId}/jugadores`, { params });
  }

  /**
   * Obtener estadísticas del equipo
   */
  obtenerEstadisticasEquipo(equipoId: number, temporada?: string): Observable<EstadisticasEquipoDTO> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    return this.http.get<EstadisticasEquipoDTO>(`${this.apiUrl}/equipo/${equipoId}`, { params });
  }

  /**
   * Obtener resumen completo de estadísticas
   */
  obtenerResumenEquipo(equipoId: number, temporada?: string): Observable<ResumenEstadisticasDTO> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    return this.http.get<ResumenEstadisticasDTO>(`${this.apiUrl}/equipo/${equipoId}/resumen`, { params });
  }

  /**
   * Obtener top goleadores del equipo
   */
  obtenerTopGoleadores(equipoId: number, temporada?: string, limite: number = 5): Observable<EstadisticasJugadorDTO[]> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    params = params.set('limite', limite.toString());
    return this.http.get<EstadisticasJugadorDTO[]>(`${this.apiUrl}/equipo/${equipoId}/top-goleadores`, { params });
  }

  /**
   * Obtener top asistentes del equipo
   */
  obtenerTopAsistentes(equipoId: number, temporada?: string, limite: number = 5): Observable<EstadisticasJugadorDTO[]> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    params = params.set('limite', limite.toString());
    return this.http.get<EstadisticasJugadorDTO[]>(`${this.apiUrl}/equipo/${equipoId}/top-asistentes`, { params });
  }

  /**
   * Obtener jugadores con mejor rating
   */
  obtenerMejorRating(equipoId: number, temporada?: string, limite: number = 5): Observable<EstadisticasJugadorDTO[]> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    params = params.set('limite', limite.toString());
    return this.http.get<EstadisticasJugadorDTO[]>(`${this.apiUrl}/equipo/${equipoId}/mejor-rating`, { params });
  }

  /**
   * Actualizar estadísticas de un jugador
   */
  actualizarEstadisticasJugador(jugadorId: number, temporada?: string): Observable<string> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    return this.http.put(`${this.apiUrl}/jugador/${jugadorId}/actualizar`, null, { 
      params,
      responseType: 'text'
    });
  }

  /**
   * Actualizar estadísticas del equipo
   */
  actualizarEstadisticasEquipo(equipoId: number, temporada?: string): Observable<string> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    return this.http.put(`${this.apiUrl}/equipo/${equipoId}/actualizar`, null, { 
      params,
      responseType: 'text'
    });
  }

  /**
   * Actualizar todas las estadísticas
   */
  actualizarTodasLasEstadisticas(temporada?: string): Observable<string> {
    let params = new HttpParams();
    if (temporada) {
      params = params.set('temporada', temporada);
    }
    return this.http.put(`${this.apiUrl}/actualizar-todas`, null, { 
      params,
      responseType: 'text'
    });
  }

  /**
   * Obtener temporada actual
   */
  obtenerTemporadaActual(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = now.getMonth() + 1; // 0-indexed
    
    if (month < 7) {
      return `${year - 1}-${year}`;
    } else {
      return `${year}-${year + 1}`;
    }
  }

  /**
   * Obtener estadísticas de un partido individual
   */
  obtenerEstadisticasPartido(partidoId: number): Observable<EstadisticasPartidoDTO> {
    return this.http.get<EstadisticasPartidoDTO>(`${this.apiUrl}/partido/${partidoId}`);
  }
}

import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface InformeJugador {
  jugadorId: number;
  nombreCompleto: string;
  posicion: string;
  temporada: string;
  analisisTecnico: string;
}

export interface AnalisisJugador {
  id: number;
  jugadorId: number;
  nombreCompleto: string;
  posicion: string;
  analisisTecnico: string;
  temporada: string;
  fechaGeneracion: string;
  tokensUsados?: number;
  modeloIa?: string;
}

export interface InformePartido {
  partidoId: number;
  titulo: string;
  fecha: string;
  resultado: string;
  resumenTactico: string;
  puntosDestacados?: string;
  areasMejora?: string;
}

/**
 * Servicio para interactuar con el módulo de Inteligencia Artificial.
 * Genera análisis técnicos automáticos de jugadores y partidos.
 */
@Injectable({
  providedIn: 'root'
})
export class AiAnalysisService {

  private apiUrl = `${environment.apiUrl}/ai`;

  constructor(private http: HttpClient) { }

  /**
   * Verifica que el módulo de IA esté operativo
   */
  healthCheck(): Observable<string> {
    return this.http.get(`${this.apiUrl}/health`, { responseType: 'text' });
  }

  /**
   * Genera informe técnico de un jugador con IA.
   * Usa prompts optimizados para minimizar coste de tokens.
   * El informe se guarda automáticamente en el servidor.
   * 
   * @param jugadorId ID del jugador
   * @param temporada Temporada a analizar (default: 2024/2025)
   */
  generarInformeJugador(jugadorId: number, temporada: string = '2024/2025'): Observable<InformeJugador> {
    const params = new HttpParams().set('temporada', temporada);
    console.log(`🧠 Generando informe IA para jugador ${jugadorId}, temporada ${temporada}`);
    return this.http.post<InformeJugador>(
      `${this.apiUrl}/jugador/${jugadorId}/informe`,
      null,
      { params }
    );
  }

  /**
   * Obtiene todos los análisis guardados de un jugador.
   * 
   * @param jugadorId ID del jugador
   * @returns Lista de análisis ordenados por fecha descendente
   */
  obtenerAnalisisJugador(jugadorId: number): Observable<AnalisisJugador[]> {
    console.log(`📚 Obteniendo análisis históricos del jugador ${jugadorId}`);
    return this.http.get<AnalisisJugador[]>(
      `${this.apiUrl}/jugador/${jugadorId}/analisis`
    );
  }

  /**
   * Obtiene un análisis específico por su ID.
   * 
   * @param analisisId ID del análisis
   * @returns Datos del análisis
   */
  obtenerAnalisisPorId(analisisId: number): Observable<AnalisisJugador> {
    console.log(`📄 Obteniendo análisis ${analisisId}`);
    return this.http.get<AnalisisJugador>(
      `${this.apiUrl}/analisis/${analisisId}`
    );
  }

  /**
   * Genera informe técnico de un partido con IA
   * 
   * @param partidoId ID del partido
   */
  generarInformePartido(partidoId: number): Observable<InformePartido> {
    console.log(`🧠 Generando informe IA para partido ${partidoId}`);
    return this.http.post<InformePartido>(
      `${this.apiUrl}/partido/${partidoId}/informe`,
      null
    );
  }
}

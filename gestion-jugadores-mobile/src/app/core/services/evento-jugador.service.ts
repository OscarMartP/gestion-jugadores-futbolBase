import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EventoJugador } from '../models/partido';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class EventoJugadorService {

  private baseURL = `${baserUrl}/api/v1/eventos`;

  constructor(private http: HttpClient) {}

  // Registrar evento
  registrarEvento(evento: EventoJugador): Observable<EventoJugador> {
    return this.http.post<EventoJugador>(this.baseURL, evento);
  }

  // Obtener eventos por partido
  obtenerEventosPorPartido(partidoId: number): Observable<EventoJugador[]> {
    return this.http.get<EventoJugador[]>(`${this.baseURL}/partido/${partidoId}`);
  }

  // Obtener eventos por jugador
  obtenerEventosPorJugador(jugadorId: number): Observable<EventoJugador[]> {
    return this.http.get<EventoJugador[]>(`${this.baseURL}/jugador/${jugadorId}`);
  }

  // Eliminar evento
  eliminarEvento(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseURL}/${id}`);
  }
}

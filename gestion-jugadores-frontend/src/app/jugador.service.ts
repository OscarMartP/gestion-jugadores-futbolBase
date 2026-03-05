import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Jugador } from './jugador';
import { Equipo } from './equipo';
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class JugadorService {

  private baseURL = `${environment.apiUrl}/jugadores`;
  private equiposURL = `${environment.apiUrl}/equipos`;

  constructor(private httpClient: HttpClient) {}

  obtenerListaDeJugadores(): Observable<Jugador[]> {
    return this.httpClient.get<Jugador[]>(this.baseURL);
  }

  registrarJugador(jugador: Partial<Jugador>)
: Observable<Jugador> {
    return this.httpClient.post<Jugador>(this.baseURL, jugador);
  }

  actualizarJugador(id: number, jugador: Jugador): Observable<Object> {
    return this.httpClient.put(`${this.baseURL}/${id}`, jugador);
  }

  obtenerJugadorPorId(id: number): Observable<Jugador> {
    return this.httpClient.get<Jugador>(`${this.baseURL}/${id}`);
  }

  eliminarJugador(id: number): Observable<Object> {
    return this.httpClient.delete(`${this.baseURL}/${id}`);
  }

  obtenerJugadoresPorEquipoId(equipoId: number): Observable<Jugador[]> {
    const token = localStorage.getItem('token');
    const headers = token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : undefined;
    // El backend expone GET /api/v1/jugadores?equipoId=ID
    return this.httpClient.get<Jugador[]>(`${this.baseURL}?equipoId=${equipoId}`, { headers });
  }

  obtenerJugadoresPorUsuario(): Observable<Jugador[]> {
    const token = localStorage.getItem('token');
    const headers = token ? new HttpHeaders().set('Authorization', `Bearer ${token}`) : undefined;
    return this.httpClient.get<Jugador[]>(`${this.baseURL}`, { headers });
  }

  getEquiposPorUsuario(usuarioId: number): Observable<Equipo[]> {
    return this.httpClient.get<Equipo[]>(`${this.equiposURL}/${usuarioId}`);
  }
}

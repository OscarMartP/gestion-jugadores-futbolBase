import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Jugador } from './jugador';
import { Equipo } from './equipo';

@Injectable({
  providedIn: 'root'
})
export class JugadorService {

  private baseURL = "http://localhost:8080/api/v1/jugadores";
  private equiposURL = "http://localhost:8080/api/v1/equipos";

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
    return this.httpClient.get<Jugador[]>(`${this.baseURL}/equipo/${equipoId}`, { headers });
  }

  getEquiposPorUsuario(usuarioId: number): Observable<Equipo[]> {
    return this.httpClient.get<Equipo[]>(`${this.equiposURL}/${usuarioId}`);
  }
}

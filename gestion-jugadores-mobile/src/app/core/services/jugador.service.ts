import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Jugador } from '../models/jugador';
import { Equipo } from '../models/equipo';

@Injectable({
  providedIn: 'root'
})
export class JugadorService {

  private baseURL = "http://localhost:8080/api/v1/jugadores";
  private equiposURL = "http://localhost:8080/api/v1/equipos";

  constructor(private httpClient: HttpClient) {}

  obtenerListaDeJugadores(): Observable<Jugador[]> {
    console.log('🔗 Llamando a:', this.baseURL);
    return this.httpClient.get<Jugador[]>(this.baseURL);
  }

  crearJugador(jugador: any): Observable<Jugador> {
    console.log('📤 Creando jugador:', jugador);
    return this.httpClient.post<Jugador>(this.baseURL, jugador);
  }

  registrarJugador(jugador: Partial<Jugador>): Observable<Jugador> {
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
    console.log('🔗 Obteniendo jugadores del usuario autenticado...');
    const token = localStorage.getItem('token');
    console.log('🔑 Token encontrado:', !!token);
    
    // Con el interceptor ya configurado, el token se agrega automáticamente
    // El backend en JugadorControladorV2 usa GET /api/v1/jugadores sin parámetros
    // para devolver jugadores del usuario autenticado
    return this.httpClient.get<Jugador[]>(this.baseURL);
  }

  getEquiposPorUsuario(usuarioId: number): Observable<Equipo[]> {
    return this.httpClient.get<Equipo[]>(`${this.equiposURL}/${usuarioId}`);
  }
}
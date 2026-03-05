import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Equipo } from '../models/equipo';
import baserUrl from './helper';

@Injectable({
  providedIn: 'root'
})
export class EquipoService {

  constructor(private http: HttpClient) { }

  crearEquipo(equipo: Equipo, userId: number): Observable<Equipo> {
    return this.http.post<Equipo>(`${baserUrl}/equipos/registrar/${userId}`, equipo);
  }

  obtenerEquiposPorUsuario(userId: number): Observable<Equipo[]> {
    return this.http.get<Equipo[]>(`${baserUrl}/equipos/usuario/${userId}`);
  }

  // Registrar equipo para el usuario autenticado (no pasar userId)
  crearEquipoMe(equipo: Equipo): Observable<Equipo> {
    return this.http.post<Equipo>(`${baserUrl}/equipos/registrar`, equipo);
  }

  // Obtener equipos del usuario autenticado
  obtenerEquiposMe(): Observable<Equipo[]> {
    console.log('🔗 Obteniendo equipos del usuario...');
    const token = localStorage.getItem('token');
    console.log('🔑 Token encontrado:', !!token);
    
    if (!token) {
      console.log('❌ Sin token, intentando lista general');
      return this.obtenerListaDeEquipos();
    }
    
    return this.http.get<Equipo[]>(`${baserUrl}/equipos/me`);
  }

  // Obtener lista de todos los equipos
  obtenerListaDeEquipos(): Observable<Equipo[]> {
    console.log('🔗 Llamando a:', `${baserUrl}/equipos`);
    return this.http.get<Equipo[]>(`${baserUrl}/equipos`);
  }

  // Obtener equipo por ID
  obtenerEquipoPorId(equipoId: number): Observable<Equipo> {
    return this.http.get<Equipo>(`${baserUrl}/equipos/${equipoId}`);
  }

  // Alias para obtener equipo (usado en seleccion-alineacion)
  obtenerEquipo(equipoId: number): Observable<Equipo> {
    return this.obtenerEquipoPorId(equipoId);
  }

  // Actualizar equipo
  actualizarEquipo(id: number, equipo: Equipo): Observable<Equipo> {
    return this.http.put<Equipo>(`${baserUrl}/equipos/${id}`, equipo);
  }

  // Eliminar equipo
  eliminarEquipo(id: number): Observable<void> {
    return this.http.delete<void>(`${baserUrl}/equipos/${id}`);
  }
}
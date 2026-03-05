import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from '../environments/environment';

//Conexion con BACK
@Injectable({ providedIn: 'root' })
export class PartidoService {
  private baseUrl = `${environment.apiUrl}/partidos`;

  // Variable que indica si hay partido activo
  hayPartidoActivo: boolean = false;

  constructor(private http: HttpClient) {}

  crearPartido(partido: any): Observable<any> {
    return this.http.post(`${this.baseUrl}`, partido);
  }

  obtenerPartidosPorEquipo(equipoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/equipo/${equipoId}`);
  }

  obtenerPartidoPorId(id: number): Observable<any> {
    return this.http.get<any>(`${this.baseUrl}/${id}`);
  }

  // Obtener partidos activos por equipo
  obtenerPartidosActivosPorEquipo(equipoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/activos/equipo/${equipoId}`);
  }
  
  // Obtener todos los partidos activos
  obtenerPartidosActivos(): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/activos`);
  }
  
  // Verificar si un equipo tiene partido activo
  tienePartidoActivo(equipoId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/equipo/${equipoId}/tiene-activo`);
  }
  
  // Activar un partido
  activarPartido(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/activar`, {});
  }
  
  // Desactivar un partido
  desactivarPartido(id: number): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}/desactivar`, {});
  }

  // Actualizar un partido
  actualizarPartido(id: number, partido: any): Observable<any> {
    return this.http.put(`${this.baseUrl}/${id}`, partido);
  }

  // Eliminar un partido
  eliminarPartido(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }

  // Actualizar alineación (titulares y suplentes)
  actualizarAlineacion(partidoId: number, titulares: number[], suplentes: number[]): Observable<any> {
    return this.http.put(`${this.baseUrl}/${partidoId}/alineacion`, { titulares, suplentes });
  }

}

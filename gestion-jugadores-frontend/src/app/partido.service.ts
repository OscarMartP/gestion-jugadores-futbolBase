import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
//Conexion con BACK
@Injectable({ providedIn: 'root' })
export class PartidoService {
  private baseUrl = 'http://localhost:8080/api/v1/partidos';

  constructor(private http: HttpClient) {}

  crearPartido(partido: any): Observable<any> {
    return this.http.post(`${this.baseUrl}`, partido);
  }

  obtenerPartidosPorEquipo(equipoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/equipo/${equipoId}`);
  }

}

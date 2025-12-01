import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
//Conexion con BACK
@Injectable({ providedIn: 'root' })
export class EventoJugadorService {
  private baseUrl = 'http://localhost:8080/api/v1/eventos';

  constructor(private http: HttpClient) {}

  registrarEvento(evento: any): Observable<any> {
    return this.http.post(`${this.baseUrl}`, evento);
  }

  obtenerEventosPorJugador(jugadorId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/jugador/${jugadorId}`);
  }

  obtenerEventosPorPartido(partidoId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/partido/${partidoId}`);
  }

  registrar(evento: any): Observable<any> {
    return this.http.post(`${this.baseUrl}`, evento);
  }
}

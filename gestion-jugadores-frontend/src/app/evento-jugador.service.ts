import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable } from "rxjs";
import { environment } from '../environments/environment';
//Conexion con BACK
@Injectable({ providedIn: 'root' })
export class EventoJugadorService {
  private baseUrl = `${environment.apiUrl}/eventos`;

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

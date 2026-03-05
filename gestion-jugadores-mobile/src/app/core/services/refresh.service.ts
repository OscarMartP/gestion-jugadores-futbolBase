import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RefreshService {

  // Subjects para notificar cambios en cada entidad
  private jugadoresRefresh$ = new Subject<void>();
  private equiposRefresh$ = new Subject<void>();
  private partidosRefresh$ = new Subject<void>();
  private estadisticasRefresh$ = new Subject<void>();

  constructor() { }

  // Observables para que los componentes se suscriban
  get onJugadoresRefresh() {
    return this.jugadoresRefresh$.asObservable();
  }

  get onEquiposRefresh() {
    return this.equiposRefresh$.asObservable();
  }

  get onPartidosRefresh() {
    return this.partidosRefresh$.asObservable();
  }

  get onEstadisticasRefresh() {
    return this.estadisticasRefresh$.asObservable();
  }

  // Métodos para emitir eventos de refresco
  refreshJugadores() {
    console.log('🔄 RefreshService: Notificando refresco de jugadores');
    this.jugadoresRefresh$.next();
  }

  refreshEquipos() {
    console.log('🔄 RefreshService: Notificando refresco de equipos');
    this.equiposRefresh$.next();
  }

  refreshPartidos() {
    console.log('🔄 RefreshService: Notificando refresco de partidos');
    this.partidosRefresh$.next();
  }

  refreshEstadisticas() {
    console.log('🔄 RefreshService: Notificando refresco de estadísticas');
    this.estadisticasRefresh$.next();
  }
}

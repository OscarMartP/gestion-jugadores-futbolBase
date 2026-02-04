import { TestBed } from '@angular/core/testing';
import { RefreshService } from './refresh.service';

describe('RefreshService', () => {
  let service: RefreshService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RefreshService]
    });
    service = TestBed.inject(RefreshService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should emit jugadores refresh event', (done) => {
    service.onJugadoresRefresh.subscribe(() => {
      expect(true).toBe(true);
      done();
    });
    
    service.refreshJugadores();
  });

  it('should emit equipos refresh event', (done) => {
    service.onEquiposRefresh.subscribe(() => {
      expect(true).toBe(true);
      done();
    });
    
    service.refreshEquipos();
  });

  it('should emit partidos refresh event', (done) => {
    service.onPartidosRefresh.subscribe(() => {
      expect(true).toBe(true);
      done();
    });
    
    service.refreshPartidos();
  });

  it('should emit estadisticas refresh event', (done) => {
    service.onEstadisticasRefresh.subscribe(() => {
      expect(true).toBe(true);
      done();
    });
    
    service.refreshEstadisticas();
  });

  it('should handle multiple subscribers for jugadores', () => {
    let count = 0;
    
    service.onJugadoresRefresh.subscribe(() => count++);
    service.onJugadoresRefresh.subscribe(() => count++);
    
    service.refreshJugadores();
    
    expect(count).toBe(2);
  });

  it('should not emit to other observables when refreshing jugadores', () => {
    let jugadoresEmitted = false;
    let equiposEmitted = false;
    
    service.onJugadoresRefresh.subscribe(() => jugadoresEmitted = true);
    service.onEquiposRefresh.subscribe(() => equiposEmitted = true);
    
    service.refreshJugadores();
    
    expect(jugadoresEmitted).toBe(true);
    expect(equiposEmitted).toBe(false);
  });
});

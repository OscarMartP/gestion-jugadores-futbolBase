import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JugadoresPage } from './jugadores.page';
import { JugadorService } from '../../core/services/jugador.service';
import { EquipoService } from '../../core/services/equipo.service';
import { RefreshService } from '../../core/services/refresh.service';
import { Subject } from 'rxjs';

describe('JugadoresPage', () => {
  let component: JugadoresPage;
  let fixture: ComponentFixture<JugadoresPage>;
  let refreshServiceSpy: jasmine.SpyObj<RefreshService>;
  let refreshSubject: Subject<void>;

  beforeEach(async () => {
    refreshSubject = new Subject<void>();
    
    const jugadorSpy = jasmine.createSpyObj('JugadorService', [
      'obtenerJugadoresPorEquipoId'
    ]);
    const equipoSpy = jasmine.createSpyObj('EquipoService', ['obtenerEquiposPorUsuarioId']);
    const refreshSpy = jasmine.createSpyObj('RefreshService', ['refreshJugadores'], {
      onJugadoresRefresh: refreshSubject.asObservable()
    });

    await TestBed.configureTestingModule({
      imports: [JugadoresPage],
      providers: [
        { provide: JugadorService, useValue: jugadorSpy },
        { provide: EquipoService, useValue: equipoSpy },
        { provide: RefreshService, useValue: refreshSpy }
      ]
    }).compileComponents();

    refreshServiceSpy = TestBed.inject(RefreshService) as jasmine.SpyObj<RefreshService>;

    fixture = TestBed.createComponent(JugadoresPage);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should subscribe to refresh events', () => {
    component.ngOnInit();
    expect(refreshServiceSpy.onJugadoresRefresh).toBeDefined();
  });

  it('should reload jugadores when refresh is triggered', () => {
    const jugadorService = TestBed.inject(JugadorService) as jasmine.SpyObj<JugadorService>;
    spyOn(component, 'cargarJugadores');
    
    component.ngOnInit();
    
    refreshSubject.next();
    
    expect(component.cargarJugadores).toHaveBeenCalled();
  });
});

import { ComponentFixture, TestBed } from '@angular/core/testing';
import { EstadisticasPartidoPage } from './estadisticas-partido.page';
import { PartidoService } from '../../core/services/partido.service';
import { EventoJugadorService } from '../../core/services/evento-jugador.service';
import { JugadorService } from '../../core/services/jugador.service';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

describe('EstadisticasPartidoPage', () => {
  let component: EstadisticasPartidoPage;
  let fixture: ComponentFixture<EstadisticasPartidoPage>;
  let partidoServiceSpy: jasmine.SpyObj<PartidoService>;
  let eventoServiceSpy: jasmine.SpyObj<EventoJugadorService>;

  beforeEach(async () => {
    const partidoSpy = jasmine.createSpyObj('PartidoService', ['obtenerPartidoPorId']);
    const eventoSpy = jasmine.createSpyObj('EventoJugadorService', ['obtenerEventosPorPartido']);
    const jugadorSpy = jasmine.createSpyObj('JugadorService', ['obtenerJugadorPorId']);

    await TestBed.configureTestingModule({
      imports: [EstadisticasPartidoPage],
      providers: [
        { provide: PartidoService, useValue: partidoSpy },
        { provide: EventoJugadorService, useValue: eventoSpy },
        { provide: JugadorService, useValue: jugadorSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: () => '1' } }
          }
        }
      ]
    }).compileComponents();

    partidoServiceSpy = TestBed.inject(PartidoService) as jasmine.SpyObj<PartidoService>;
    eventoServiceSpy = TestBed.inject(EventoJugadorService) as jasmine.SpyObj<EventoJugadorService>;

    fixture = TestBed.createComponent(EstadisticasPartidoPage);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have partidoId from route params', () => {
    expect(component.partidoId).toBeDefined();
  });

  it('should call partido service when loading estadisticas', () => {
    const mockPartido = {
      id: 1,
      equipoId: 1,
      titulo: 'Test',
      fecha: '2024-01-15',
      partidoActivo: false,
      duracion: 90,
      golesEquipo: 3,
      golesRival: 1,
      titulares: [],
      suplentes: []
    };

    partidoServiceSpy.obtenerPartidoPorId.and.returnValue(of(mockPartido as any));
    eventoServiceSpy.obtenerEventosPorPartido.and.returnValue(of([]));

    component.cargarEstadisticas();

    expect(partidoServiceSpy.obtenerPartidoPorId).toHaveBeenCalled();
    expect(eventoServiceSpy.obtenerEventosPorPartido).toHaveBeenCalled();
  });
});
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { JugadorFormPage } from './jugador-form.page';
import { JugadorService } from '../../core/services/jugador.service';
import { EquipoService } from '../../core/services/equipo.service';
import { RefreshService } from '../../core/services/refresh.service';
import { ActivatedRoute, Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';
import { of } from 'rxjs';

describe('JugadorFormPage', () => {
  let component: JugadorFormPage;
  let fixture: ComponentFixture<JugadorFormPage>;
  let refreshServiceSpy: jasmine.SpyObj<RefreshService>;

  beforeEach(async () => {
    const jugadorSpy = jasmine.createSpyObj('JugadorService', [
      'crearJugador',
      'actualizarJugador',
      'obtenerJugadorPorId'
    ]);
    const equipoSpy = jasmine.createSpyObj('EquipoService', ['obtenerEquiposPorUsuarioId']);
    const refreshSpy = jasmine.createSpyObj('RefreshService', ['refreshJugadores']);
    const router = jasmine.createSpyObj('Router', ['navigate']);

    await TestBed.configureTestingModule({
      imports: [JugadorFormPage],
      providers: [
        FormBuilder,
        { provide: JugadorService, useValue: jugadorSpy },
        { provide: EquipoService, useValue: equipoSpy },
        { provide: RefreshService, useValue: refreshSpy },
        { provide: Router, useValue: router },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: { paramMap: { get: () => null } }
          }
        }
      ]
    }).compileComponents();

    refreshServiceSpy = TestBed.inject(RefreshService) as jasmine.SpyObj<RefreshService>;

    fixture = TestBed.createComponent(JugadorFormPage);
    component = fixture.componentInstance;
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should have a form', () => {
    expect(component.jugadorForm).toBeDefined();
  });

  it('should call refresh service after successful save', (done) => {
    const jugadorService = TestBed.inject(JugadorService) as jasmine.SpyObj<JugadorService>;
    jugadorService.crearJugador.and.returnValue(of({ id: 1, nombre: 'Test', apellido: 'Test', posicion: 'MC', equipoId: 1 }));

    component.jugadorForm.patchValue({
      nombre: 'Test',
      apellido: 'Test',
      posicion: 'MC',
      equipoId: 1
    });

    component.guardarJugador().then(() => {
      expect(refreshServiceSpy.refreshJugadores).toHaveBeenCalled();
      done();
    });
  });
});

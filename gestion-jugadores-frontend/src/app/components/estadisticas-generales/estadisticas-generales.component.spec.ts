import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { of, throwError } from 'rxjs';

import { EstadisticasGeneralesComponent } from './estadisticas-generales.component';
import { EstadisticasService } from '../../services/estadisticas.service';
import { EquipoService } from '../../equipo.service';
import { 
  EstadisticasJugadorDTO, 
  EstadisticasEquipoDTO, 
  ResumenEstadisticasDTO 
} from '../../models/estadisticas.model';
import { Equipo } from '../../equipo';

describe('EstadisticasGeneralesComponent', () => {
  let component: EstadisticasGeneralesComponent;
  let fixture: ComponentFixture<EstadisticasGeneralesComponent>;
  let estadisticasService: jasmine.SpyObj<EstadisticasService>;
  let equipoService: jasmine.SpyObj<EquipoService>;

  // Datos mock
  const mockEquipos: Equipo[] = [
    { id: 1, nombre: 'Equipo A', duracionPartido: 90, tipoFutbol: 'FUTBOL_11', usuarioId: 1 },
    { id: 2, nombre: 'Equipo B', duracionPartido: 70, tipoFutbol: 'FUTBOL_7', usuarioId: 1 }
  ];

  const mockEstadisticasEquipo: EstadisticasEquipoDTO = {
    id: 1,
    equipoId: 1,
    equipoNombre: 'Equipo A',
    temporada: '2025-2026',
    partidosJugados: 20,
    partidosGanados: 12,
    partidosEmpatados: 5,
    partidosPerdidos: 3,
    puntos: 41,
    golesFavor: 35,
    golesContra: 20,
    diferenciaGoles: 15,
    tarjetasAmarillas: 15,
    tarjetasRojas: 2,
    totalPasesClave: 100,
    pasesClave0_15: 15,
    pasesClave16_30: 20,
    pasesClave31_45: 18,
    pasesClave46_60: 17,
    pasesClave61_75: 16,
    pasesClave76_90: 14,
    mayorPasador: 'Juan Pérez'
  } as EstadisticasEquipoDTO;

  const mockTopGoleadores: EstadisticasJugadorDTO[] = [
    {
      id: 1,
      jugadorId: 1,
      jugadorNombre: 'Juan',
      jugadorApellido: 'Pérez',
      totalGoles: 15,
      totalAsistencias: 8,
      partidosJugados: 20
    } as EstadisticasJugadorDTO,
    {
      id: 2,
      jugadorId: 2,
      jugadorNombre: 'Pedro',
      jugadorApellido: 'García',
      totalGoles: 12,
      totalAsistencias: 5,
      partidosJugados: 18
    } as EstadisticasJugadorDTO
  ];

  const mockResumen: ResumenEstadisticasDTO = {
    estadisticasEquipo: mockEstadisticasEquipo,
    topGoleadores: mockTopGoleadores,
    topAsistentes: [],
    menosTargetas: [],
    totalJugadores: 15
  };

  beforeEach(async () => {
    // Crear spies
    const estadisticasServiceSpy = jasmine.createSpyObj('EstadisticasService', [
      'obtenerResumenEquipo',
      'obtenerEstadisticasJugadoresEquipo',
      'obtenerEstadisticasEquipo'
    ]);
    const equipoServiceSpy = jasmine.createSpyObj('EquipoService', [
      'obtenerListaDeEquipos'
    ]);

    await TestBed.configureTestingModule({
      declarations: [ EstadisticasGeneralesComponent ],
      imports: [ 
        HttpClientTestingModule,
        FormsModule 
      ],
      providers: [
        { provide: EstadisticasService, useValue: estadisticasServiceSpy },
        { provide: EquipoService, useValue: equipoServiceSpy }
      ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EstadisticasGeneralesComponent);
    component = fixture.componentInstance;
    estadisticasService = TestBed.inject(EstadisticasService) as jasmine.SpyObj<EstadisticasService>;
    equipoService = TestBed.inject(EquipoService) as jasmine.SpyObj<EquipoService>;

    // Configurar respuestas por defecto
    equipoService.obtenerListaDeEquipos.and.returnValue(of(mockEquipos));
    estadisticasService.obtenerResumenEquipo.and.returnValue(of(mockResumen));
    estadisticasService.obtenerEstadisticasJugadoresEquipo.and.returnValue(of([]));
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  // ========== TESTS DE INICIALIZACIÓN ==========

  describe('ngOnInit', () => {
    it('debería cargar equipos al inicializar', () => {
      fixture.detectChanges(); // Trigger ngOnInit

      expect(equipoService.obtenerListaDeEquipos).toHaveBeenCalled();
      expect(component.equipos.length).toBe(2);
      expect(component.equipos[0].nombre).toBe('Equipo A');
    });

    it('debería calcular la temporada actual correctamente', () => {
      fixture.detectChanges();

      expect(component.temporada).toBeTruthy();
      expect(component.temporada).toMatch(/^\d{4}-\d{4}$/); // Formato: YYYY-YYYY
    });
  });

  describe('obtenerTemporadaActual', () => {
    it('debería generar temporada correcta antes de julio', () => {
      jasmine.clock().install();
      jasmine.clock().mockDate(new Date(2026, 2, 15)); // Marzo 2026

      component.obtenerTemporadaActual();
      expect(component.temporada).toBe('2025-2026');

      jasmine.clock().uninstall();
    });

    it('debería generar temporada correcta después de julio', () => {
      jasmine.clock().install();
      jasmine.clock().mockDate(new Date(2026, 8, 15)); // Septiembre 2026

      component.obtenerTemporadaActual();
      expect(component.temporada).toBe('2026-2027');

      jasmine.clock().uninstall();
    });
  });

  // ========== TESTS DE CARGA DE ESTADÍSTICAS ==========

  describe('cargarEstadisticas', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.equipoSeleccionado = 1;
    });

    it('debería cargar resumen cuando se selecciona un equipo', () => {
      component.cargarEstadisticas();

      expect(estadisticasService.obtenerResumenEquipo).toHaveBeenCalledWith(1, component.temporada);
      expect(component.resumen).toEqual(mockResumen);
      expect(component.estadisticasEquipo).toEqual(mockEstadisticasEquipo);
    });

    it('debería mostrar indicador de carga mientras carga estadísticas', () => {
      component.cargarEstadisticas();

      expect(component.cargando).toBe(true);
      
      // Simular finalización de carga
      fixture.detectChanges();
      fixture.whenStable().then(() => {
        expect(component.cargando).toBe(false);
      });
    });

    it('debería cargar top goleadores del resumen', () => {
      component.cargarEstadisticas();

      expect(component.topGoleadores.length).toBe(2);
      expect(component.topGoleadores[0].totalGoles).toBe(15);
      expect(component.topGoleadores[1].totalGoles).toBe(12);
    });

    it('no debería cargar estadísticas si no hay equipo seleccionado', () => {
      component.equipoSeleccionado = null;
      component.cargarEstadisticas();

      expect(estadisticasService.obtenerResumenEquipo).not.toHaveBeenCalled();
    });

    it('debería cargar estadísticas de jugadores del equipo', () => {
      const mockJugadores: EstadisticasJugadorDTO[] = [
        { jugadorId: 1, totalGoles: 10 } as EstadisticasJugadorDTO,
        { jugadorId: 2, totalGoles: 8 } as EstadisticasJugadorDTO
      ];
      estadisticasService.obtenerEstadisticasJugadoresEquipo.and.returnValue(of(mockJugadores));

      component.cargarEstadisticas();

      expect(estadisticasService.obtenerEstadisticasJugadoresEquipo).toHaveBeenCalledWith(1, component.temporada);
      expect(component.jugadores.length).toBe(2);
    });
  });

  // ========== TESTS DE CAMBIO DE EQUIPO ==========

  describe('onEquipoChange', () => {
    it('debería cargar estadísticas cuando cambia el equipo', () => {
      fixture.detectChanges();
      component.equipoSeleccionado = 1;
      spyOn(component, 'cargarEstadisticas');

      component.onEquipoChange();

      expect(component.cargarEstadisticas).toHaveBeenCalled();
    });

    it('no debería cargar si no hay equipo seleccionado', () => {
      component.equipoSeleccionado = null;
      spyOn(component, 'cargarEstadisticas');

      component.onEquipoChange();

      expect(component.cargarEstadisticas).not.toHaveBeenCalled();
    });
  });

  // ========== TESTS DE ESTADÍSTICAS DEL EQUIPO ==========

  describe('estadísticas del equipo', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.equipoSeleccionado = 1;
      component.cargarEstadisticas();
    });

    it('debería mostrar partidos jugados correctamente', () => {
      expect(component.estadisticasEquipo?.partidosJugados).toBe(20);
    });

    it('debería mostrar victorias, empates y derrotas', () => {
      expect(component.estadisticasEquipo?.partidosGanados).toBe(12);
      expect(component.estadisticasEquipo?.partidosEmpatados).toBe(5);
      expect(component.estadisticasEquipo?.partidosPerdidos).toBe(3);
    });

    it('debería calcular puntos correctamente (victorias*3 + empates*1)', () => {
      const puntosEsperados = 12 * 3 + 5 * 1; // 36 + 5 = 41
      expect(component.estadisticasEquipo?.puntos).toBe(puntosEsperados);
    });

    it('debería mostrar goles a favor y en contra', () => {
      expect(component.estadisticasEquipo?.golesFavor).toBe(35);
      expect(component.estadisticasEquipo?.golesContra).toBe(20);
      expect(component.estadisticasEquipo?.diferenciaGoles).toBe(15);
    });

    it('debería mostrar el mayor pasador', () => {
      expect(component.estadisticasEquipo?.mayorPasador).toBe('Juan Pérez');
    });

    it('debería tener distribución temporal de pases clave', () => {
      expect(component.estadisticasEquipo?.totalPasesClave).toBe(100);
      expect(component.estadisticasEquipo?.pasesClave0_15).toBe(15);
      expect(component.estadisticasEquipo?.pasesClave16_30).toBe(20);
    });
  });

  // ========== TESTS DE TOP JUGADORES ==========

  describe('top goleadores', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.equipoSeleccionado = 1;
      component.cargarEstadisticas();
    });

    it('debería ordenar goleadores de mayor a menor', () => {
      expect(component.topGoleadores[0].totalGoles).toBeGreaterThan(
        component.topGoleadores[1].totalGoles
      );
    });

    it('debería mostrar nombre completo de jugadores', () => {
      const primerGoleador = component.topGoleadores[0];
      expect(primerGoleador.jugadorNombre).toBe('Juan');
      expect(primerGoleador.jugadorApellido).toBe('Pérez');
    });
  });

  // ========== TESTS DE SELECCIÓN DE ESTADÍSTICA ==========

  describe('seleccionarEstadistica', () => {
    it('debería cambiar a pases clave', () => {
      component.seleccionarEstadistica('pasesClave');
      expect(component.estadisticaSeleccionada).toBe('pasesClave');
    });

    it('debería cambiar a tiros a puerta', () => {
      component.seleccionarEstadistica('tirosAPuerta');
      expect(component.estadisticaSeleccionada).toBe('tirosAPuerta');
    });

    it('debería cambiar a robos', () => {
      component.seleccionarEstadistica('robos');
      expect(component.estadisticaSeleccionada).toBe('robos');
    });
  });

  // ========== TESTS DE VISIBILIDAD ==========

  describe('control de visibilidad', () => {
    it('debería mostrar jugadores por defecto', () => {
      fixture.detectChanges();
      expect(component.mostrarJugadores).toBe(true);
    });

    it('debería mostrar equipo por defecto', () => {
      fixture.detectChanges();
      expect(component.mostrarEquipo).toBe(true);
    });
  });

  // ========== TESTS DE MANEJO DE ERRORES ==========

  describe('manejo de errores', () => {
    it('debería manejar error al cargar equipos', () => {
      equipoService.obtenerListaDeEquipos.and.returnValue(
        throwError({ status: 500, message: 'Server error' })
      );

      spyOn(console, 'log');
      fixture.detectChanges();

      expect(console.log).toHaveBeenCalled();
    });

    it('debería manejar error al cargar estadísticas', () => {
      estadisticasService.obtenerResumenEquipo.and.returnValue(
        throwError({ status: 404, message: 'Not found' })
      );

      component.equipoSeleccionado = 1;
      spyOn(console, 'error');
      
      component.cargarEstadisticas();

      expect(console.error).toHaveBeenCalled();
      expect(component.cargando).toBe(false);
    });
  });

  // ========== TESTS DE CÁLCULOS ==========

  describe('cálculos de estadísticas', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.equipoSeleccionado = 1;
      component.cargarEstadisticas();
    });

    it('debería verificar que victorias + empates + derrotas = partidos jugados', () => {
      const stats = component.estadisticasEquipo!;
      const total = stats.partidosGanados + stats.partidosEmpatados + stats.partidosPerdidos;
      expect(total).toBe(stats.partidosJugados);
    });

    it('debería verificar que diferencia de goles = favor - contra', () => {
      const stats = component.estadisticasEquipo!;
      expect(stats.diferenciaGoles).toBe(stats.golesFavor - stats.golesContra);
    });

    it('debería verificar suma de distribución temporal de pases clave', () => {
      const stats = component.estadisticasEquipo!;
      const sumaParcial = 
        stats.pasesClave0_15 + stats.pasesClave16_30 + stats.pasesClave31_45 +
        stats.pasesClave46_60 + stats.pasesClave61_75 + stats.pasesClave76_90;
      expect(sumaParcial).toBe(stats.totalPasesClave);
    });
  });
});

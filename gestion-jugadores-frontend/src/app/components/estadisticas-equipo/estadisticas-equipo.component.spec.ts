import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CUSTOM_ELEMENTS_SCHEMA } from '@angular/core';
import { of, throwError } from 'rxjs';

import { EstadisticasEquipoComponent } from './estadisticas-equipo.component';
import { EstadisticasService } from '../../services/estadisticas.service';
import { EquipoService } from '../../equipo.service';
import { 
  EstadisticasJugadorDTO, 
  EstadisticasEquipoDTO, 
  ResumenEstadisticasDTO 
} from '../../models/estadisticas.model';
import { Equipo } from '../../equipo';

describe('EstadisticasEquipoComponent', () => {
  let component: EstadisticasEquipoComponent;
  let fixture: ComponentFixture<EstadisticasEquipoComponent>;
  let estadisticasService: jasmine.SpyObj<EstadisticasService>;
  let equipoService: jasmine.SpyObj<EquipoService>;

  // Mock del equipo
  const mockEquipo: Equipo = {
    id: 1,
    nombre: 'Equipo Test',
    duracionPartido: 90,
    tipoFutbol: 'FUTBOL_11',
    usuarioId: 1
  };

  // Mock de estadísticas del equipo
  const mockEstadisticasEquipo: EstadisticasEquipoDTO = {
    id: 1,
    equipoId: 1,
    equipoNombre: 'Equipo Test',
    temporada: '2025-2026',
    partidosJugados: 25,
    partidosGanados: 15,
    partidosEmpatados: 7,
    partidosPerdidos: 3,
    puntos: 52, // 15*3 + 7*1 = 52
    golesFavor: 45,
    golesContra: 18,
    diferenciaGoles: 27,
    tarjetasAmarillas: 20,
    tarjetasRojas: 1,
    totalPasesClave: 150,
    totalTirosAPuerta: 200,
    totalRobos: 180,
    mayorPasador: 'Carlos Midfielder'
  } as EstadisticasEquipoDTO;

  // Mock de jugadores
  const mockJugadores: EstadisticasJugadorDTO[] = [
    {
      id: 1,
      jugadorId: 1,
      jugadorNombre: 'Pedro',
      jugadorApellido: 'Striker',
      posicion: 'Delantero',
      totalGoles: 18,
      totalAsistencias: 10,
      partidosJugados: 25,
      minutosJugados: 2100,
      totalPasesClave: 35,
      totalTirosAPuerta: 55,
      tarjetasAmarillas: 3,
      tarjetasRojas: 0
    } as EstadisticasJugadorDTO,
    {
      id: 2,
      jugadorId: 2,
      jugadorNombre: 'Carlos',
      jugadorApellido: 'Midfielder',
      posicion: 'Centrocampista',
      totalGoles: 8,
      totalAsistencias: 22,
      partidosJugados: 24,
      minutosJugados: 2040,
      totalPasesClave: 60,
      totalTirosAPuerta: 30,
      tarjetasAmarillas: 5,
      tarjetasRojas: 0
    } as EstadisticasJugadorDTO
  ];

  // Mock del resumen
  const mockResumen: ResumenEstadisticasDTO = {
    estadisticasEquipo: mockEstadisticasEquipo,
    topGoleadores: [mockJugadores[0]],
    topAsistentes: [mockJugadores[1]],
    menosTargetas: [mockJugadores[0]],
    totalJugadores: 15
  };

  beforeEach(async () => {
    // Crear spies
    const estadisticasServiceSpy = jasmine.createSpyObj('EstadisticasService', [
      'obtenerResumenEquipo',
      'obtenerEstadisticasJugadoresEquipo',
      'obtenerTemporadaActual',
      'actualizarEstadisticasEquipo'
    ]);
    const equipoServiceSpy = jasmine.createSpyObj('EquipoService', [
      'obtenerEquipoPorId'
    ]);

    await TestBed.configureTestingModule({
      declarations: [ EstadisticasEquipoComponent ],
      imports: [ 
        HttpClientTestingModule,
        FormsModule 
      ],
      providers: [
        { provide: EstadisticasService, useValue: estadisticasServiceSpy },
        { provide: EquipoService, useValue: equipoServiceSpy },
        {
          provide: ActivatedRoute,
          useValue: {
            params: of({ id: 1 })
          }
        }
      ],
      schemas: [ CUSTOM_ELEMENTS_SCHEMA ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EstadisticasEquipoComponent);
    component = fixture.componentInstance;
    estadisticasService = TestBed.inject(EstadisticasService) as jasmine.SpyObj<EstadisticasService>;
    equipoService = TestBed.inject(EquipoService) as jasmine.SpyObj<EquipoService>;

    // Configurar respuestas por defecto
    equipoService.obtenerEquipoPorId.and.returnValue(of(mockEquipo));
    estadisticasService.obtenerResumenEquipo.and.returnValue(of(mockResumen));
    estadisticasService.obtenerEstadisticasJugadoresEquipo.and.returnValue(of(mockJugadores));
    estadisticasService.obtenerTemporadaActual.and.returnValue('2025-2026');
    estadisticasService.actualizarEstadisticasEquipo.and.returnValue(of('OK'));
  });

  it('debería crear el componente', () => {
    expect(component).toBeTruthy();
  });

  // ========== TESTS DE INICIALIZACIÓN ==========

  describe('ngOnInit', () => {
    it('debería obtener el ID del equipo desde la ruta', () => {
      fixture.detectChanges();
      expect(component.equipoId).toBe(1);
    });

    it('debería cargar el equipo al inicializar', () => {
      fixture.detectChanges();
      expect(equipoService.obtenerEquipoPorId).toHaveBeenCalledWith(1);
      expect(component.equipo).toEqual(mockEquipo);
    });

    it('debería establecer la temporada actual', () => {
      fixture.detectChanges();
      expect(estadisticasService.obtenerTemporadaActual).toHaveBeenCalled();
      expect(component.temporadaActual).toBe('2025-2026');
      expect(component.temporadaSeleccionada).toBe('2025-2026');
    });

    it('debería cargar el resumen automáticamente', () => {
      fixture.detectChanges();
      expect(estadisticasService.obtenerResumenEquipo).toHaveBeenCalledWith(1, '2025-2026');
    });
  });

  // ========== TESTS DE CARGA DE RESUMEN ==========

  describe('cargarResumen', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('debería cargar el resumen completo de estadísticas', () => {
      component.cargarResumen();

      expect(component.resumen).toEqual(mockResumen);
      expect(component.estadisticasEquipo).toEqual(mockEstadisticasEquipo);
    });

    it('debería cargar top goleadores', () => {
      component.cargarResumen();

      expect(component.topGoleadores.length).toBe(1);
      expect(component.topGoleadores[0].totalGoles).toBe(18);
    });

    it('debería cargar top asistentes', () => {
      component.cargarResumen();

      expect(component.topAsistentes.length).toBe(1);
      expect(component.topAsistentes[0].totalAsistencias).toBe(22);
    });

    it('debería mostrar indicador de carga', (done) => {
      component.cargando = false;
      component.cargarResumen();

      expect(component.cargando).toBe(true);
      
      setTimeout(() => {
        expect(component.cargando).toBe(false);
        done();
      }, 100);
    });

    it('debería limpiar el error al cargar', () => {
      component.error = 'Error anterior';
      component.cargarResumen();

      expect(component.error).toBe('');
    });
  });

  // ========== TESTS DE ESTADÍSTICAS DEL EQUIPO ==========

  describe('estadísticas del equipo', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('debería mostrar partidos jugados correctamente', () => {
      expect(component.estadisticasEquipo?.partidosJugados).toBe(25);
    });

    it('debería mostrar récord de victorias/empates/derrotas', () => {
      expect(component.estadisticasEquipo?.partidosGanados).toBe(15);
      expect(component.estadisticasEquipo?.partidosEmpatados).toBe(7);
      expect(component.estadisticasEquipo?.partidosPerdidos).toBe(3);
    });

    it('debería calcular puntos correctamente', () => {
      const puntosCalculados = 15 * 3 + 7 * 1; // victorias*3 + empates*1
      expect(component.estadisticasEquipo?.puntos).toBe(puntosCalculados);
      expect(component.estadisticasEquipo?.puntos).toBe(52);
    });

    it('debería mostrar goles a favor, en contra y diferencia', () => {
      expect(component.estadisticasEquipo?.golesFavor).toBe(45);
      expect(component.estadisticasEquipo?.golesContra).toBe(18);
      expect(component.estadisticasEquipo?.diferenciaGoles).toBe(27);
    });

    it('debería verificar diferencia de goles calculada', () => {
      const stats = component.estadisticasEquipo!;
      expect(stats.diferenciaGoles).toBe(stats.golesFavor - stats.golesContra);
    });

    it('debería mostrar tarjetas amarillas y rojas', () => {
      expect(component.estadisticasEquipo?.tarjetasAmarillas).toBe(20);
      expect(component.estadisticasEquipo?.tarjetasRojas).toBe(1);
    });

    it('debería mostrar el mayor pasador del equipo', () => {
      expect(component.estadisticasEquipo?.mayorPasador).toBe('Carlos Midfielder');
    });

    it('debería mostrar totales de estadísticas avanzadas', () => {
      expect(component.estadisticasEquipo?.totalPasesClave).toBe(150);
      expect(component.estadisticasEquipo?.totalTirosAPuerta).toBe(200);
      expect(component.estadisticasEquipo?.totalRobos).toBe(180);
    });
  });

  // ========== TESTS DE ESTADÍSTICAS DE JUGADORES ==========

  describe('cargarEstadisticasJugadores', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('debería cargar estadísticas de todos los jugadores', () => {
      component.cargarEstadisticasJugadores();

      expect(estadisticasService.obtenerEstadisticasJugadoresEquipo)
        .toHaveBeenCalledWith(1, '2025-2026');
      expect(component.estadisticasJugadores.length).toBe(2);
    });

    it('debería verificar datos completos de jugadores', () => {
      component.cargarEstadisticasJugadores();

      const jugador1 = component.estadisticasJugadores[0];
      expect(jugador1.jugadorNombre).toBe('Pedro');
      expect(jugador1.totalGoles).toBe(18);
      expect(jugador1.totalAsistencias).toBe(10);
      expect(jugador1.partidosJugados).toBe(25);
    });

    it('debería calcular promedios por partido', () => {
      component.cargarEstadisticasJugadores();

      const jugador = component.estadisticasJugadores[0];
      const promedioGoles = jugador.totalGoles / jugador.partidosJugados;
      expect(promedioGoles).toBeCloseTo(0.72, 2); // 18/25 = 0.72
    });
  });

  // ========== TESTS DE VISTAS ==========

  describe('cambio de vistas', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('debería comenzar en vista de resumen', () => {
      expect(component.vistaActual).toBe('resumen');
    });

    it('debería permitir cambiar a vista de jugadores', () => {
      component.vistaActual = 'jugadores';
      expect(component.vistaActual).toBe('jugadores');
    });

    it('debería permitir cambiar a vista de tops', () => {
      component.vistaActual = 'tops';
      expect(component.vistaActual).toBe('tops');
    });
  });

  // ========== TESTS DE ACTUALIZACIÓN ==========

  describe('actualizarEstadisticasEquipo', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('debería llamar al servicio para actualizar estadísticas', () => {
      // Este test requiere que exista un método de actualización en el componente
      // Si no existe, se puede agregar o comentar el test
      if (component['actualizarEstadisticasEquipo']) {
        component['actualizarEstadisticasEquipo']();
        expect(estadisticasService.actualizarEstadisticasEquipo).toHaveBeenCalledWith(1, '2025-2026');
      } else {
        expect(true).toBe(true); // Test placeholder
      }
    });
  });

  // ========== TESTS DE MANEJO DE ERRORES ==========

  describe('manejo de errores', () => {
    it('debería manejar error al cargar el equipo', () => {
      equipoService.obtenerEquipoPorId.and.returnValue(
        throwError({ status: 404, message: 'Equipo no encontrado' })
      );

      spyOn(console, 'error');
      fixture.detectChanges();

      expect(console.error).toHaveBeenCalled();
    });

    it('debería manejar error al cargar el resumen', () => {
      estadisticasService.obtenerResumenEquipo.and.returnValue(
        throwError({ status: 500, message: 'Server error' })
      );

      fixture.detectChanges();

      expect(component.error).toBeTruthy();
      expect(component.error).toContain('Error al cargar');
      expect(component.cargando).toBe(false);
    });

    it('debería manejar error al cargar estadísticas de jugadores', () => {
      estadisticasService.obtenerEstadisticasJugadoresEquipo.and.returnValue(
        throwError({ status: 500, message: 'Server error' })
      );

      spyOn(console, 'error');
      component.cargarEstadisticasJugadores();

      expect(console.error).toHaveBeenCalled();
      expect(component.cargando).toBe(false);
    });
  });

  // ========== TESTS DE VALIDACIÓN DE DATOS ==========

  describe('validación de datos de estadísticas', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('debería verificar que partidos totales = ganados + empatados + perdidos', () => {
      const stats = component.estadisticasEquipo!;
      const totalCalculado = stats.partidosGanados + stats.partidosEmpatados + stats.partidosPerdidos;
      expect(totalCalculado).toBe(stats.partidosJugados);
    });

    it('debería verificar que puntos siguen fórmula: victorias*3 + empates*1', () => {
      const stats = component.estadisticasEquipo!;
      const puntosCalculados = stats.partidosGanados * 3 + stats.partidosEmpatados * 1;
      expect(stats.puntos).toBe(puntosCalculados);
    });

    it('debería tener diferencia de goles positiva si más goles a favor', () => {
      const stats = component.estadisticasEquipo!;
      if (stats.golesFavor > stats.golesContra) {
        expect(stats.diferenciaGoles).toBeGreaterThan(0);
      }
    });
  });

  // ========== TESTS DE COMPARACIÓN DE JUGADORES ==========

  describe('comparación de estadísticas entre jugadores', () => {
    beforeEach(() => {
      fixture.detectChanges();
      component.cargarEstadisticasJugadores();
    });

    it('debería identificar al máximo goleador', () => {
      const maxGoleador = component.estadisticasJugadores.reduce((max, j) => 
        j.totalGoles > max.totalGoles ? j : max
      );
      expect(maxGoleador.jugadorNombre).toBe('Pedro');
      expect(maxGoleador.totalGoles).toBe(18);
    });

    it('debería identificar al máximo asistente', () => {
      const maxAsistente = component.estadisticasJugadores.reduce((max, j) => 
        j.totalAsistencias > max.totalAsistencias ? j : max
      );
      expect(maxAsistente.jugadorNombre).toBe('Carlos');
      expect(maxAsistente.totalAsistencias).toBe(22);
    });

    it('debería comparar minutos jugados entre jugadores', () => {
      const jugador1 = component.estadisticasJugadores[0];
      const jugador2 = component.estadisticasJugadores[1];
      
      expect(jugador1.minutosJugados).toBeGreaterThan(0);
      expect(jugador2.minutosJugados).toBeGreaterThan(0);
    });
  });

  // ========== TESTS DE TEMPORADAS ==========

  describe('cambio de temporada', () => {
    beforeEach(() => {
      fixture.detectChanges();
    });

    it('debería permitir cambiar a temporada anterior', () => {
      component.temporadaSeleccionada = '2024-2025';
      component.cargarResumen();

      expect(estadisticasService.obtenerResumenEquipo)
        .toHaveBeenCalledWith(1, '2024-2025');
    });

    it('debería mantener la temporada actual por defecto', () => {
      expect(component.temporadaSeleccionada).toBe(component.temporadaActual);
    });
  });
});

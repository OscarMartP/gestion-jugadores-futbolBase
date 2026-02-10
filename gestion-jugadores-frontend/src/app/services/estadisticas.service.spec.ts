import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { EstadisticasService } from './estadisticas.service';
import { 
  EstadisticasJugadorDTO, 
  EstadisticasEquipoDTO, 
  ResumenEstadisticasDTO 
} from '../models/estadisticas.model';
import { EstadisticasPartidoDTO } from '../models/estadisticas-partido.model';
import { environment } from '../../environments/environment';

describe('EstadisticasService', () => {
  let service: EstadisticasService;
  let httpMock: HttpTestingController;
  const apiUrl = `${environment.apiUrl}/estadisticas`;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [EstadisticasService]
    });
    service = TestBed.inject(EstadisticasService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify(); // Verificar que no hay peticiones HTTP pendientes
  });

  it('debería crearse el servicio correctamente', () => {
    expect(service).toBeTruthy();
  });

  // ========== TESTS DE ESTADÍSTICAS DE JUGADOR ==========

  describe('obtenerEstadisticasJugador', () => {
    it('debería obtener estadísticas de un jugador sin temporada', () => {
      const mockEstadisticas: EstadisticasJugadorDTO = {
        id: 1,
        jugadorId: 1,
        jugadorNombre: 'Juan',
        jugadorApellido: 'Pérez',
        posicion: 'Delantero',
        temporada: '2025-2026',
        totalGoles: 10,
        totalAsistencias: 5,
        tarjetasAmarillas: 2,
        tarjetasRojas: 0,
        partidosJugados: 20,
        minutosJugados: 1800,
        totalPasesClave: 15,
        totalTirosAPuerta: 25,
        totalRobos: 8
      } as EstadisticasJugadorDTO;

      service.obtenerEstadisticasJugador(1).subscribe(stats => {
        expect(stats).toEqual(mockEstadisticas);
        expect(stats.totalGoles).toBe(10);
        expect(stats.totalAsistencias).toBe(5);
      });

      const req = httpMock.expectOne(`${apiUrl}/jugador/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEstadisticas);
    });

    it('debería obtener estadísticas de un jugador con temporada específica', () => {
      const mockEstadisticas: EstadisticasJugadorDTO = {
        id: 1,
        jugadorId: 1,
        temporada: '2024-2025',
        totalGoles: 8
      } as EstadisticasJugadorDTO;

      service.obtenerEstadisticasJugador(1, '2024-2025').subscribe(stats => {
        expect(stats.temporada).toBe('2024-2025');
      });

      const req = httpMock.expectOne(`${apiUrl}/jugador/1?temporada=2024-2025`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEstadisticas);
    });
  });

  describe('obtenerEstadisticasJugadoresEquipo', () => {
    it('debería obtener estadísticas de todos los jugadores de un equipo', () => {
      const mockJugadores: EstadisticasJugadorDTO[] = [
        { id: 1, jugadorId: 1, totalGoles: 10, jugadorNombre: 'Juan' } as EstadisticasJugadorDTO,
        { id: 2, jugadorId: 2, totalGoles: 5, jugadorNombre: 'Pedro' } as EstadisticasJugadorDTO,
        { id: 3, jugadorId: 3, totalGoles: 8, jugadorNombre: 'Luis' } as EstadisticasJugadorDTO
      ];

      service.obtenerEstadisticasJugadoresEquipo(1).subscribe(jugadores => {
        expect(jugadores.length).toBe(3);
        expect(jugadores[0].totalGoles).toBe(10);
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1/jugadores`);
      expect(req.request.method).toBe('GET');
      req.flush(mockJugadores);
    });
  });

  // ========== TESTS DE ESTADÍSTICAS DE EQUIPO ==========

  describe('obtenerEstadisticasEquipo', () => {
    it('debería obtener estadísticas del equipo', () => {
      const mockEstadisticas: EstadisticasEquipoDTO = {
        id: 1,
        equipoId: 1,
        equipoNombre: 'Equipo Test',
        temporada: '2025-2026',
        partidosJugados: 20,
        partidosGanados: 12,
        partidosEmpatados: 5,
        partidosPerdidos: 3,
        puntos: 41,
        golesFavor: 35,
        golesContra: 20,
        diferenciaGoles: 15,
        totalPasesClave: 100,
        mayorPasador: 'Juan Pérez'
      } as EstadisticasEquipoDTO;

      service.obtenerEstadisticasEquipo(1, '2025-2026').subscribe(stats => {
        expect(stats.partidosJugados).toBe(20);
        expect(stats.puntos).toBe(41);
        expect(stats.golesFavor).toBe(35);
        expect(stats.mayorPasador).toBe('Juan Pérez');
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1?temporada=2025-2026`);
      expect(req.request.method).toBe('GET');
      req.flush(mockEstadisticas);
    });

    it('debería calcular correctamente los puntos (victorias=3, empates=1)', () => {
      const mockEstadisticas: EstadisticasEquipoDTO = {
        partidosGanados: 10,
        partidosEmpatados: 5,
        puntos: 35 // 10*3 + 5*1 = 35
      } as EstadisticasEquipoDTO;

      service.obtenerEstadisticasEquipo(1).subscribe(stats => {
        const puntosEsperados = stats.partidosGanados * 3 + stats.partidosEmpatados * 1;
        expect(stats.puntos).toBe(puntosEsperados);
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1`);
      req.flush(mockEstadisticas);
    });
  });

  // ========== TESTS DE RESUMEN ==========

  describe('obtenerResumenEquipo', () => {
    it('debería obtener resumen completo de estadísticas', () => {
      const mockResumen: ResumenEstadisticasDTO = {
        estadisticasEquipo: {
          partidosJugados: 20,
          puntos: 45
        } as EstadisticasEquipoDTO,
        topGoleadores: [
          { totalGoles: 15, jugadorNombre: 'Goleador1' } as EstadisticasJugadorDTO,
          { totalGoles: 10, jugadorNombre: 'Goleador2' } as EstadisticasJugadorDTO
        ],
        topAsistentes: [
          { totalAsistencias: 12, jugadorNombre: 'Asistente1' } as EstadisticasJugadorDTO
        ],
        menosTargetas: [],
        totalJugadores: 15
      };

      service.obtenerResumenEquipo(1, '2025-2026').subscribe(resumen => {
        expect(resumen.topGoleadores.length).toBe(2);
        expect(resumen.topGoleadores[0].totalGoles).toBe(15);
        expect(resumen.topAsistentes[0].totalAsistencias).toBe(12);
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1/resumen?temporada=2025-2026`);
      expect(req.request.method).toBe('GET');
      req.flush(mockResumen);
    });
  });

  // ========== TESTS DE TOP JUGADORES ==========

  describe('obtenerTopGoleadores', () => {
    it('debería obtener top 5 goleadores por defecto', () => {
      const mockGoleadores: EstadisticasJugadorDTO[] = [
        { totalGoles: 20 } as EstadisticasJugadorDTO,
        { totalGoles: 15 } as EstadisticasJugadorDTO,
        { totalGoles: 12 } as EstadisticasJugadorDTO,
        { totalGoles: 10 } as EstadisticasJugadorDTO,
        { totalGoles: 8 } as EstadisticasJugadorDTO
      ];

      service.obtenerTopGoleadores(1).subscribe(goleadores => {
        expect(goleadores.length).toBe(5);
        expect(goleadores[0].totalGoles).toBeGreaterThan(goleadores[1].totalGoles);
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1/top-goleadores?limite=5`);
      expect(req.request.method).toBe('GET');
      req.flush(mockGoleadores);
    });

    it('debería permitir especificar límite personalizado', () => {
      const mockGoleadores: EstadisticasJugadorDTO[] = [
        { totalGoles: 20 } as EstadisticasJugadorDTO,
        { totalGoles: 15 } as EstadisticasJugadorDTO,
        { totalGoles: 12 } as EstadisticasJugadorDTO
      ];

      service.obtenerTopGoleadores(1, '2025-2026', 3).subscribe(goleadores => {
        expect(goleadores.length).toBe(3);
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1/top-goleadores?temporada=2025-2026&limite=3`);
      req.flush(mockGoleadores);
    });
  });

  describe('obtenerTopAsistentes', () => {
    it('debería obtener top asistentes ordenados correctamente', () => {
      const mockAsistentes: EstadisticasJugadorDTO[] = [
        { totalAsistencias: 15 } as EstadisticasJugadorDTO,
        { totalAsistencias: 12 } as EstadisticasJugadorDTO,
        { totalAsistencias: 8 } as EstadisticasJugadorDTO
      ];

      service.obtenerTopAsistentes(1, '2025-2026', 3).subscribe(asistentes => {
        expect(asistentes[0].totalAsistencias).toBeGreaterThanOrEqual(asistentes[1].totalAsistencias);
        expect(asistentes[1].totalAsistencias).toBeGreaterThanOrEqual(asistentes[2].totalAsistencias);
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1/top-asistentes?temporada=2025-2026&limite=3`);
      req.flush(mockAsistentes);
    });
  });

  // ========== TESTS DE ESTADÍSTICAS DE PARTIDO ==========

  describe('obtenerEstadisticasPartido', () => {
    it('debería obtener estadísticas de un partido específico', () => {
      const mockPartido: EstadisticasPartidoDTO = {
        id: 1,
        equipoId: 1,
        equipoNombre: 'Equipo Test',
        fecha: '2026-02-10',
        titulo: 'Partido Test',
        resultado: 'Victoria',
        golesEquipo: 3,
        golesRival: 1,
        totalGoles: 3,
        totalAsistencias: 2,
        totalPasesClave: 10,
        totalTarjetasAmarillas: 1,
        totalTarjetasRojas: 0,
        eventosPorJugador: [
          {
            jugadorId: 1,
            jugadorNombre: 'Juan Pérez',
            goles: 2,
            asistencias: 1,
            pasesClave: 5,
            tarjetasAmarillas: 0,
            tarjetasRojas: 0,
            robos: 3,
            tirosAPuerta: 4
          }
        ],
        distribucionGoles: {
          intervalo0_15: 1,
          intervalo16_30: 0,
          intervalo31_45: 1,
          intervalo46_60: 0,
          intervalo61_75: 1,
          intervalo76_90: 0
        },
        distribucionAsistencias: {
          intervalo0_15: 0,
          intervalo16_30: 1,
          intervalo31_45: 0,
          intervalo46_60: 1,
          intervalo61_75: 0,
          intervalo76_90: 0
        }
      } as EstadisticasPartidoDTO;

      service.obtenerEstadisticasPartido(1).subscribe(partido => {
        expect(partido.id).toBe(1);
        expect(partido.totalGoles).toBe(3);
        expect(partido.resultado).toBe('Victoria');
        expect(partido.eventosPorJugador.length).toBe(1);
        expect(partido.eventosPorJugador[0].goles).toBe(2);
        // Verificar distribución temporal
        expect(partido.distribucionGoles.intervalo0_15).toBe(1);
        expect(partido.distribucionGoles.intervalo31_45).toBe(1);
      });

      const req = httpMock.expectOne(`${apiUrl}/partido/1`);
      expect(req.request.method).toBe('GET');
      req.flush(mockPartido);
    });

    it('debería manejar partido con múltiples eventos por jugador', () => {
      const mockPartido: EstadisticasPartidoDTO = {
        id: 2,
        eventosPorJugador: [
          { jugadorId: 1, goles: 2, asistencias: 1 },
          { jugadorId: 2, goles: 1, asistencias: 0 },
          { jugadorId: 3, goles: 0, asistencias: 2 }
        ]
      } as EstadisticasPartidoDTO;

      service.obtenerEstadisticasPartido(2).subscribe(partido => {
        expect(partido.eventosPorJugador.length).toBe(3);
        const totalGoles = partido.eventosPorJugador.reduce((sum, j) => sum + j.goles, 0);
        const totalAsistencias = partido.eventosPorJugador.reduce((sum, j) => sum + j.asistencias, 0);
        expect(totalGoles).toBe(3);
        expect(totalAsistencias).toBe(3);
      });

      const req = httpMock.expectOne(`${apiUrl}/partido/2`);
      req.flush(mockPartido);
    });

    it('debería verificar distribución temporal completa', () => {
      const mockPartido: EstadisticasPartidoDTO = {
        id: 3,
        distribucionGoles: {
          intervalo0_15: 1,
          intervalo16_30: 2,
          intervalo31_45: 0,
          intervalo46_60: 1,
          intervalo61_75: 2,
          intervalo76_90: 1
        }
      } as EstadisticasPartidoDTO;

      service.obtenerEstadisticasPartido(3).subscribe(partido => {
        const totalGolesDistribucion = 
          partido.distribucionGoles.intervalo0_15 +
          partido.distribucionGoles.intervalo16_30 +
          partido.distribucionGoles.intervalo31_45 +
          partido.distribucionGoles.intervalo46_60 +
          partido.distribucionGoles.intervalo61_75 +
          partido.distribucionGoles.intervalo76_90;
        expect(totalGolesDistribucion).toBe(7);
      });

      const req = httpMock.expectOne(`${apiUrl}/partido/3`);
      req.flush(mockPartido);
    });
  });

  // ========== TESTS DE ACTUALIZACIÓN ==========

  describe('actualizarEstadisticasJugador', () => {
    it('debería actualizar estadísticas de un jugador', () => {
      service.actualizarEstadisticasJugador(1, '2025-2026').subscribe(response => {
        expect(response).toBe('Estadísticas actualizadas');
      });

      const req = httpMock.expectOne(`${apiUrl}/jugador/1/actualizar?temporada=2025-2026`);
      expect(req.request.method).toBe('PUT');
      req.flush('Estadísticas actualizadas');
    });
  });

  describe('actualizarEstadisticasEquipo', () => {
    it('debería actualizar estadísticas del equipo', () => {
      service.actualizarEstadisticasEquipo(1).subscribe(response => {
        expect(response).toBeDefined();
      });

      const req = httpMock.expectOne(`${apiUrl}/equipo/1/actualizar`);
      expect(req.request.method).toBe('PUT');
      req.flush('OK');
    });
  });

  // ========== TESTS DE UTILIDADES ==========

  describe('obtenerTemporadaActual', () => {
    it('debería calcular temporada antes de julio', () => {
      // Mock de fecha en marzo (mes 3)
      jasmine.clock().install();
      jasmine.clock().mockDate(new Date(2026, 2, 15)); // Marzo 15, 2026

      const temporada = service.obtenerTemporadaActual();
      expect(temporada).toBe('2025-2026');

      jasmine.clock().uninstall();
    });

    it('debería calcular temporada después de julio', () => {
      // Mock de fecha en septiembre (mes 9)
      jasmine.clock().install();
      jasmine.clock().mockDate(new Date(2026, 8, 15)); // Septiembre 15, 2026

      const temporada = service.obtenerTemporadaActual();
      expect(temporada).toBe('2026-2027');

      jasmine.clock().uninstall();
    });
  });

  // ========== TESTS DE MANEJO DE ERRORES ==========

  describe('manejo de errores', () => {
    it('debería manejar error 404 al buscar estadísticas', () => {
      service.obtenerEstadisticasJugador(999).subscribe(
        () => fail('Debería haber fallado'),
        (error) => {
          expect(error.status).toBe(404);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/jugador/999`);
      req.flush('Not found', { status: 404, statusText: 'Not Found' });
    });

    it('debería manejar error 500 del servidor', () => {
      service.obtenerEstadisticasEquipo(1).subscribe(
        () => fail('Debería haber fallado'),
        (error) => {
          expect(error.status).toBe(500);
        }
      );

      const req = httpMock.expectOne(`${apiUrl}/equipo/1`);
      req.flush('Server error', { status: 500, statusText: 'Internal Server Error' });
    });
  });
});

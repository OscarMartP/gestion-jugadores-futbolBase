import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { EstadisticasService } from '../../services/estadisticas.service';
import { EquipoService } from '../../equipo.service';
import { 
  EstadisticasJugadorDTO, 
  EstadisticasEquipoDTO, 
  ResumenEstadisticasDTO 
} from '../../models/estadisticas.model';
import { Equipo } from '../../equipo';

@Component({
  selector: 'app-estadisticas-equipo',
  templateUrl: './estadisticas-equipo.component.html',
  styleUrls: ['./estadisticas-equipo.component.css']
})
export class EstadisticasEquipoComponent implements OnInit {
  equipoId: number = 0;
  equipo: Equipo | null = null;
  temporadaActual: string = '';
  temporadaSeleccionada: string = '';
  
  // Datos de estadísticas
  resumen: ResumenEstadisticasDTO | null = null;
  estadisticasEquipo: EstadisticasEquipoDTO | null = null;
  estadisticasJugadores: EstadisticasJugadorDTO[] = [];
  topGoleadores: EstadisticasJugadorDTO[] = [];
  topAsistentes: EstadisticasJugadorDTO[] = [];
  mejorRating: EstadisticasJugadorDTO[] = [];
  menosTargetas: EstadisticasJugadorDTO[] = [];
  
  // Control de UI
  cargando: boolean = false;
  error: string = '';
  vistaActual: 'resumen' | 'jugadores' | 'tops' = 'resumen';
  actualizando: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private estadisticasService: EstadisticasService,
    private equipoService: EquipoService
  ) { }

  ngOnInit(): void {
    // Obtener ID del equipo desde la ruta
    this.route.params.subscribe(params => {
      this.equipoId = +params['id'];
      if (this.equipoId) {
        this.cargarEquipo();
        this.inicializar();
      }
    });
  }

  inicializar(): void {
    this.temporadaActual = this.estadisticasService.obtenerTemporadaActual();
    this.temporadaSeleccionada = this.temporadaActual;
    this.cargarResumen();
  }

  cargarEquipo(): void {
    this.equipoService.obtenerEquipoPorId(this.equipoId).subscribe({
      next: (equipo) => {
        this.equipo = equipo;
      },
      error: (err) => {
        console.error('Error al cargar equipo:', err);
      }
    });
  }

  cargarResumen(): void {
    this.cargando = true;
    this.error = '';
    
    this.estadisticasService.obtenerResumenEquipo(this.equipoId, this.temporadaSeleccionada).subscribe({
      next: (resumen) => {
        this.resumen = resumen;
        this.estadisticasEquipo = resumen.estadisticasEquipo;
        this.topGoleadores = resumen.topGoleadores;
        this.topAsistentes = resumen.topAsistentes;
        this.menosTargetas = resumen.menosTargetas;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar resumen:', err);
        this.error = 'Error al cargar las estadísticas. Por favor, intenta de nuevo.';
        this.cargando = false;
      }
    });
  }

  cargarEstadisticasJugadores(): void {
    this.cargando = true;
    this.error = '';
    
    this.estadisticasService.obtenerEstadisticasJugadoresEquipo(this.equipoId, this.temporadaSeleccionada).subscribe({
      next: (estadisticas) => {
        this.estadisticasJugadores = estadisticas;
        this.cargando = false;
      },
      error: (err) => {
        console.error('Error al cargar estadísticas de jugadores:', err);
        this.error = 'Error al cargar las estadísticas de los jugadores.';
        this.cargando = false;
      }
    });
  }

  cargarMejorRating(): void {
    this.estadisticasService.obtenerMejorRating(this.equipoId, this.temporadaSeleccionada, 10).subscribe({
      next: (jugadores) => {
        this.mejorRating = jugadores;
      },
      error: (err) => {
        console.error('Error al cargar mejor rating:', err);
      }
    });
  }

  cambiarVista(vista: 'resumen' | 'jugadores' | 'tops'): void {
    this.vistaActual = vista;
    
    if (vista === 'jugadores' && this.estadisticasJugadores.length === 0) {
      this.cargarEstadisticasJugadores();
    }
    
    if (vista === 'tops' && this.mejorRating.length === 0) {
      this.cargarMejorRating();
    }
  }

  cambiarTemporada(): void {
    this.cargarResumen();
    if (this.vistaActual === 'jugadores') {
      this.cargarEstadisticasJugadores();
    }
    if (this.vistaActual === 'tops') {
      this.cargarMejorRating();
    }
  }

  actualizarEstadisticas(): void {
    if (confirm('¿Estás seguro de actualizar las estadísticas? Esto recalculará todas las estadísticas del equipo.')) {
      this.actualizando = true;
      
      this.estadisticasService.actualizarEstadisticasEquipo(this.equipoId, this.temporadaSeleccionada).subscribe({
        next: (mensaje) => {
          console.log(mensaje);
          alert('Estadísticas actualizadas correctamente');
          this.actualizando = false;
          this.cargarResumen();
          if (this.vistaActual === 'jugadores') {
            this.cargarEstadisticasJugadores();
          }
        },
        error: (err) => {
          console.error('Error al actualizar estadísticas:', err);
          alert('Error al actualizar las estadísticas');
          this.actualizando = false;
        }
      });
    }
  }

  calcularPorcentajeVictorias(): number {
    if (!this.estadisticasEquipo || this.estadisticasEquipo.partidosJugados === 0) {
      return 0;
    }
    return (this.estadisticasEquipo.partidosGanados / this.estadisticasEquipo.partidosJugados) * 100;
  }

  obtenerColorRating(rating: number): string {
    if (rating >= 8) return '#4CAF50'; // Verde
    if (rating >= 6) return '#FFC107'; // Amarillo
    if (rating >= 4) return '#FF9800'; // Naranja
    return '#F44336'; // Rojo
  }

  obtenerNombreCompleto(jugador: EstadisticasJugadorDTO): string {
    return `${jugador.jugadorNombre} ${jugador.jugadorApellido}`;
  }
}

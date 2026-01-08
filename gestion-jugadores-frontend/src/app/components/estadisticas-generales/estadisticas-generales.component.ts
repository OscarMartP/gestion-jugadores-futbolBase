import { Component, OnInit } from '@angular/core';
import { EstadisticasService } from '../../services/estadisticas.service';
import { EquipoService } from '../../equipo.service';
import { Equipo } from '../../equipo';
import { EstadisticasEquipoDTO, EstadisticasJugadorDTO, ResumenEstadisticasDTO } from '../../models/estadisticas.model';

@Component({
  selector: 'app-estadisticas-generales',
  templateUrl: './estadisticas-generales.component.html',
  styleUrls: ['./estadisticas-generales.component.css']
})
export class EstadisticasGeneralesComponent implements OnInit {
  equipos: Equipo[] = [];
  equipoSeleccionado: number | null = null;
  temporada: string = '';
  
  // Datos de estadísticas
  resumen: ResumenEstadisticasDTO | null = null;
  estadisticasEquipo: EstadisticasEquipoDTO | null = null;
  jugadores: EstadisticasJugadorDTO[] = [];
  topGoleadores: EstadisticasJugadorDTO[] = [];
  topAsistentes: EstadisticasJugadorDTO[] = [];
  mejorRating: EstadisticasJugadorDTO[] = [];
  
  cargando: boolean = false;
  mostrarJugadores: boolean = true;
  mostrarEquipo: boolean = true;

  constructor(
    private estadisticasService: EstadisticasService,
    private equipoService: EquipoService
  ) { }

  ngOnInit(): void {
    this.obtenerTemporadaActual();
    this.cargarEquipos();
  }

  cargarEquipos(): void {
    this.equipoService.obtenerListaDeEquipos().subscribe(
      data => {
        this.equipos = data;
      },
      error => console.log(error)
    );
  }

  obtenerTemporadaActual(): void {
    const year = new Date().getFullYear();
    const month = new Date().getMonth() + 1;
    
    // Si estamos antes de julio, temporada es año anterior - año actual
    // Si estamos después de julio, temporada es año actual - año siguiente
    if (month < 7) {
      this.temporada = `${year - 1}-${year}`;
    } else {
      this.temporada = `${year}-${year + 1}`;
    }
  }

  onEquipoChange(): void {
    if (this.equipoSeleccionado) {
      this.cargarEstadisticas();
    }
  }

  cargarEstadisticas(): void {
    if (!this.equipoSeleccionado) return;
    
    this.cargando = true;
    
    // Cargar resumen completo
    this.estadisticasService.obtenerResumenEquipo(this.equipoSeleccionado, this.temporada).subscribe(
      data => {
        this.resumen = data;
        this.estadisticasEquipo = data.estadisticasEquipo;
        this.topGoleadores = data.topGoleadores;
        this.topAsistentes = data.topAsistentes;
        this.cargando = false;
      },
      error => {
        console.log(error);
        this.cargando = false;
      }
    );
    
    // Cargar todos los jugadores
    this.estadisticasService.obtenerEstadisticasJugadoresEquipo(this.equipoSeleccionado, this.temporada).subscribe(
      data => {
        this.jugadores = data;
      },
      error => console.log(error)
    );
    
    // Cargar mejor rating
    this.estadisticasService.obtenerMejorRating(this.equipoSeleccionado, this.temporada, 5).subscribe(
      data => {
        this.mejorRating = data;
      },
      error => console.log(error)
    );
  }

  actualizarEstadisticas(): void {
    if (!this.equipoSeleccionado) return;
    
    this.cargando = true;
    this.estadisticasService.actualizarEstadisticasEquipo(this.equipoSeleccionado, this.temporada).subscribe(
      response => {
        console.log('Estadísticas actualizadas:', response);
        this.cargarEstadisticas();
      },
      error => {
        console.log(error);
        this.cargando = false;
      }
    );
  }

  getNombreEquipo(): string {
    const equipo = this.equipos.find(e => e.id === this.equipoSeleccionado);
    return equipo ? equipo.nombre : 'Equipo';
  }
}

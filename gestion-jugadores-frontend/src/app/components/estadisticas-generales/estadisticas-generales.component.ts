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
  perfilesJugadores: PerfilJugador[] = [];
  
  cargando: boolean = false;
  mostrarJugadores: boolean = true;
  mostrarEquipo: boolean = true;
  estadisticaSeleccionada: 'pasesClave' | 'tirosAPuerta' | 'robos' = 'pasesClave';

  constructor(
    private estadisticasService: EstadisticasService,
    private equipoService: EquipoService
  ) { }

  seleccionarEstadistica(tipo: 'pasesClave' | 'tirosAPuerta' | 'robos'): void {
    this.estadisticaSeleccionada = tipo;
  }

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
    
    console.log('🔍 Iniciando carga de estadísticas - Equipo:', this.equipoSeleccionado, 'Temporada:', this.temporada);
    
    // Cargar resumen completo
    this.estadisticasService.obtenerResumenEquipo(this.equipoSeleccionado, this.temporada).subscribe(
      data => {
        console.log('✅ Respuesta recibida del backend:', data);
        this.resumen = data;
        this.estadisticasEquipo = data.estadisticasEquipo;
        console.log('📊 Estadísticas del equipo:', this.estadisticasEquipo);
        console.log('👑 Mayor Pasador:', this.estadisticasEquipo?.mayorPasador);
        this.topGoleadores = data.topGoleadores;
        this.topAsistentes = data.topAsistentes;
        this.cargando = false;
      },
      error => {
        console.error('❌ Error al cargar resumen:', error);
        this.cargando = false;
      }
    );
    
    // Cargar todos los jugadores
    this.estadisticasService.obtenerEstadisticasJugadoresEquipo(this.equipoSeleccionado, this.temporada).subscribe(
      data => {
        this.jugadores = data;
        this.clasificarPerfilesJugadores(data);
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

  clasificarPerfilesJugadores(jugadores: EstadisticasJugadorDTO[]): void {
    this.perfilesJugadores = [];
    
    console.log('🎭 Clasificando perfiles de jugadores:', jugadores);
    
    // Filtrar jugadores con al menos 3 pases clave para tener datos significativos
    const jugadoresConPases = jugadores.filter(j => j.totalPasesClave >= 3);
    
    console.log('  Jugadores con >= 3 pases clave:', jugadoresConPases.length);
    
    jugadoresConPases.forEach(jugador => {
      const total = jugador.totalPasesClave;
      const ganando = jugador.pasesClaveGanando || 0;
      const empatando = jugador.pasesClaveEmpatando || 0;
      const perdiendo = jugador.pasesClavePerdiendo || 0;
      
      console.log(`  ${jugador.jugadorNombre}: Total=${total}, G=${ganando}, E=${empatando}, P=${perdiendo}`);
      
      let perfil = '';
      let icono = '';
      let descripcion = '';
      let colorClass = '';
      
      // Calcular porcentajes
      const pctGanando = (ganando / total) * 100;
      const pctEmpatando = (empatando / total) * 100;
      const pctPerdiendo = (perdiendo / total) * 100;
      
      // Clasificar según el perfil
      if (pctPerdiendo >= 50) {
        perfil = 'Jugador de Remontada';
        icono = '🔥';
        descripcion = `Crea ${pctPerdiendo.toFixed(0)}% de sus pases clave perdiendo`;
        colorClass = 'perfil-remontada';
      } else if (pctGanando >= 50) {
        perfil = 'Jugador Inconsistente';
        icono = '⚠️';
        descripcion = `Solo aparece ganando (${pctGanando.toFixed(0)}%)`;
        colorClass = 'perfil-inconsistente';
      } else if (Math.abs(pctGanando - pctEmpatando) <= 15 && Math.abs(pctGanando - pctPerdiendo) <= 15) {
        perfil = 'Jugador Líder';
        icono = '⭐';
        descripcion = 'Genera en todas las situaciones';
        colorClass = 'perfil-lider';
      } else if (pctEmpatando >= 40) {
        perfil = 'Jugador Equilibrado';
        icono = '⚖️';
        descripcion = `Rinde mejor empatando (${pctEmpatando.toFixed(0)}%)`;
        colorClass = 'perfil-equilibrado';
      } else {
        perfil = 'Jugador Regular';
        icono = '📊';
        descripcion = 'Perfil mixto';
        colorClass = 'perfil-regular';
      }
      
      this.perfilesJugadores.push({
        nombre: `${jugador.jugadorNombre} ${jugador.jugadorApellido}`,
        perfil: perfil,
        icono: icono,
        descripcion: descripcion,
        colorClass: colorClass,
        totalPasesClave: total,
        pctGanando: pctGanando,
        pctEmpatando: pctEmpatando,
        pctPerdiendo: pctPerdiendo
      });
      
      console.log(`    -> Perfil asignado: ${perfil}`);
    });
    
    // Ordenar por total de pases clave (más relevantes primero)
    this.perfilesJugadores.sort((a, b) => b.totalPasesClave - a.totalPasesClave);
    
    // Limitar a los 5 jugadores más relevantes
    this.perfilesJugadores = this.perfilesJugadores.slice(0, 5);
    
    console.log('  Perfiles finales:', this.perfilesJugadores.length);
  }
}

interface PerfilJugador {
  nombre: string;
  perfil: string;
  icono: string;
  descripcion: string;
  colorClass: string;
  totalPasesClave: number;
  pctGanando: number;
  pctEmpatando: number;
  pctPerdiendo: number;
}

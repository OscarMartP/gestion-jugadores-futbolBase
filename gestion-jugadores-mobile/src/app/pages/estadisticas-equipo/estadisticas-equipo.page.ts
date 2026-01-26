import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon,
  IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner,
  IonChip, IonLabel, IonSegment, IonSegmentButton, IonList, IonItem,
  IonBadge, IonProgressBar, IonSelect, IonSelectOption, IonGrid,
  IonRow, IonCol
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { arrowBack, statsChart, people, person, trophy, football, shield } from 'ionicons/icons';
import { EstadisticasService } from '../../core/services/estadisticas.service';
import { EquipoService } from '../../core/services/equipo.service';

@Component({
  selector: 'app-estadisticas-equipo',
  templateUrl: './estadisticas-equipo.page.html',
  styleUrls: ['./estadisticas-equipo.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon,
    IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner,
    IonChip, IonLabel, IonSegment, IonSegmentButton, IonList, IonItem,
    IonBadge, IonProgressBar, IonSelect, IonSelectOption, IonGrid,
    IonRow, IonCol,
    CommonModule, FormsModule
  ]
})
export class EstadisticasEquipoPage implements OnInit {

  equipos: any[] = [];
  equipoSeleccionado: number | null = null;
  cargando = false;
  
  // Datos de estadísticas
  estadisticasEquipo: any = null;
  estadisticasJugadores: any[] = [];
  
  // Vistas
  vistaSeleccionada: 'generales' | 'individuales' = 'generales';
  eventoSeleccionado: 'pasesClave' | 'tirosAPuerta' | 'robos' = 'pasesClave';
  seccionSeleccionada: 'resultado' | 'tiempo' = 'resultado';

  constructor(
    private router: Router,
    private estadisticasService: EstadisticasService,
    private equipoService: EquipoService
  ) {
    addIcons({ arrowBack, statsChart, people, person, trophy, football, shield });
  }

  ngOnInit() {
    this.cargarEquipos();
  }

  ionViewWillEnter() {
    if (this.equipoSeleccionado) {
      this.cargarEstadisticas();
    }
  }

  volver() {
    this.router.navigate(['/tabs/partidos']);
  }

  cargarEquipos() {
    this.equipoService.obtenerEquiposMe().subscribe({
      next: (equipos: any[]) => {
        this.equipos = equipos;
        if (equipos.length > 0 && !this.equipoSeleccionado) {
          this.equipoSeleccionado = equipos[0].id;
          this.cargarEstadisticas();
        }
      },
      error: (err: any) => console.error('Error al cargar equipos:', err)
    });
  }

  onEquipoChange() {
    if (this.equipoSeleccionado) {
      this.cargarEstadisticas();
    }
  }

  cargarEstadisticas() {
    if (!this.equipoSeleccionado) return;
    
    this.cargando = true;
    
    Promise.all([
      this.estadisticasService.obtenerEstadisticasEquipo(this.equipoSeleccionado).toPromise(),
      this.estadisticasService.obtenerEstadisticasJugadores(this.equipoSeleccionado).toPromise()
    ]).then(([estadisticas, jugadores]: any[]) => {
      console.log('📊 Estadísticas equipo:', estadisticas);
      console.log('👥 Estadísticas jugadores:', jugadores);
      
      this.estadisticasEquipo = estadisticas;
      this.estadisticasJugadores = jugadores || [];
      this.cargando = false;
    }).catch(error => {
      console.error('❌ Error al cargar estadísticas:', error);
      this.cargando = false;
    });
  }

  // ========== MÉTODOS PARA VISTA GENERALES ==========

  getLabelsIntervalosTiempo(): string[] {
    return ['0-15', '16-30', '31-45', '46-60', '61-75', '76-90'];
  }

  getArrayIntervalosTiempo(): number[] {
    if (!this.estadisticasEquipo) return [0, 0, 0, 0, 0, 0];
    
    const prefijo = this.getPrefijoEvento();
    return [
      this.estadisticasEquipo[`${prefijo}0_15`] || 0,
      this.estadisticasEquipo[`${prefijo}16_30`] || 0,
      this.estadisticasEquipo[`${prefijo}31_45`] || 0,
      this.estadisticasEquipo[`${prefijo}46_60`] || 0,
      this.estadisticasEquipo[`${prefijo}61_75`] || 0,
      this.estadisticasEquipo[`${prefijo}76_90`] || 0
    ];
  }

  getTotalIntervalosTiempo(): number {
    return this.getArrayIntervalosTiempo().reduce((total, valor) => total + valor, 0);
  }

  getValoresResultado(): { ganando: number, empatando: number, perdiendo: number } {
    if (!this.estadisticasEquipo) return { ganando: 0, empatando: 0, perdiendo: 0 };
    
    const prefijo = this.getPrefijoEvento();
    return {
      ganando: this.estadisticasEquipo[`${prefijo}Ganando`] || 0,
      empatando: this.estadisticasEquipo[`${prefijo}Empatando`] || 0,
      perdiendo: this.estadisticasEquipo[`${prefijo}Perdiendo`] || 0
    };
  }

  getTotalResultado(): number {
    const valores = this.getValoresResultado();
    return valores.ganando + valores.empatando + valores.perdiendo;
  }

  getPrefijoEvento(): string {
    if (this.eventoSeleccionado === 'pasesClave') return 'pasesClave';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'tirosAPuerta';
    if (this.eventoSeleccionado === 'robos') return 'robos';
    return '';
  }

  getTituloEvento(): string {
    if (this.eventoSeleccionado === 'pasesClave') return 'Pases Clave';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'Tiros a Puerta';
    if (this.eventoSeleccionado === 'robos') return 'Robos';
    return '';
  }

  getIconoEvento(): string {
    if (this.eventoSeleccionado === 'pasesClave') return 'football';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'trophy';
    if (this.eventoSeleccionado === 'robos') return 'shield';
    return 'statsChart';
  }

  calcularPorcentaje(valor: number, total: number): number {
    if (total === 0) return 0;
    return Math.round((valor / total) * 100);
  }

  // ========== MÉTODOS PARA VISTA INDIVIDUALES ==========

  getJugadoresOrdenados(): any[] {
    if (!this.estadisticasJugadores || this.estadisticasJugadores.length === 0) {
      return [];
    }

    // Ordenar por el evento seleccionado
    const campo = this.getCampoOrdenamiento();
    return [...this.estadisticasJugadores].sort((a, b) => {
      const valorA = a[campo] || 0;
      const valorB = b[campo] || 0;
      return valorB - valorA;
    });
  }

  getCampoOrdenamiento(): string {
    if (this.eventoSeleccionado === 'pasesClave') return 'pasesClave';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'tirosAPuerta';
    if (this.eventoSeleccionado === 'robos') return 'robos';
    return 'goles';
  }

  getValorJugador(jugador: any): number {
    const campo = this.getCampoOrdenamiento();
    return jugador[campo] || 0;
  }

  getPromedioJugador(jugador: any): number {
    const valor = this.getValorJugador(jugador);
    const partidos = jugador.partidosJugados || 1;
    return Math.round((valor / partidos) * 100) / 100;
  }

  getMaxValorJugadores(): number {
    const jugadores = this.getJugadoresOrdenados();
    if (jugadores.length === 0) return 1;
    return this.getValorJugador(jugadores[0]) || 1;
  }
}


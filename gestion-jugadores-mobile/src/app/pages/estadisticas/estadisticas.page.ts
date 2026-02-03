import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonCard, 
  IonCardContent, IonCardHeader, IonCardTitle, IonIcon,
  IonRefresher, IonRefresherContent, IonSelect, IonSelectOption,
  IonItem, IonLabel, IonSegment, IonSegmentButton, IonList,
  IonBadge, IonProgressBar, IonSpinner, IonGrid, IonRow, IonCol,
  IonChip, IonButton, LoadingController
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { statsChart, trophy, football, shield, people, person, syncOutline } from 'ionicons/icons';
import { EstadisticasService } from '../../core/services/estadisticas.service';
import { EquipoService } from '../../core/services/equipo.service';
import { RefreshService } from '../../core/services/refresh.service';

@Component({
  selector: 'app-estadisticas',
  templateUrl: './estadisticas.page.html',
  styleUrls: ['./estadisticas.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonCard, 
    IonCardContent, IonCardHeader, IonCardTitle, IonIcon,
    IonRefresher, IonRefresherContent, IonSelect, IonSelectOption,
    IonItem, IonLabel, IonSegment, IonSegmentButton, IonList,
    IonBadge, IonProgressBar, IonSpinner, IonGrid, IonRow, IonCol,
    IonChip, IonButton,
    CommonModule, FormsModule
  ]
})
export class EstadisticasPage implements OnInit {

  equipos: any[] = [];
  equipoSeleccionado: number | null = null;
  cargando = false;
  
  // Datos de estadísticas
  estadisticasEquipo: any = null;
  estadisticasJugadores: any[] = [];
  
  // Vistas
  vistaSeleccionada: 'generales' | 'individuales' = 'generales';
  eventoSeleccionado: 'pasesClave' | 'tirosAPuerta' | 'robos' | 'perdidas' = 'pasesClave';
  seccionSeleccionada: 'resultado' | 'tiempo' = 'resultado';

  constructor(
    private estadisticasService: EstadisticasService,
    private equipoService: EquipoService,
    private refreshService: RefreshService,
    private loadingController: LoadingController
  ) {
    addIcons({ statsChart, trophy, football, shield, people, person, syncOutline });
  }

  ngOnInit() {
    this.cargarEquipos();
    
    // Suscribirse a eventos de refresco
    this.refreshService.onEstadisticasRefresh.subscribe(() => {
      console.log('📢 Evento de refresco recibido en EstadisticasPage');
      if (this.equipoSeleccionado) {
        this.cargarEstadisticas();
      }
    });
  }

  ionViewWillEnter() {
    if (this.equipoSeleccionado) {
      this.cargarEstadisticas();
    }
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

  doRefresh(event: any) {
    this.cargarEstadisticas();
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

  // ========== MÉTODOS PARA VISTA GENERALES ==========

  getLabelsIntervalosTiempo(): string[] {
    return ['0-15', '16-30', '31-45', '46-60', '61-75', '76-90'];
  }

  getArrayIntervalosTiempo(): number[] {
    if (!this.estadisticasEquipo) return [0, 0, 0, 0, 0, 0];
    
    const prefijo = this.getPrefijoEvento();
    console.log('🔍 Buscando intervalos con prefijo:', prefijo);
    console.log('📦 Datos disponibles:', this.estadisticasEquipo);
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
    console.log('🔍 Buscando valores resultado con prefijo:', prefijo);
    console.log('📦 Campos disponibles:', Object.keys(this.estadisticasEquipo));
    const resultado = {
      ganando: this.estadisticasEquipo[`${prefijo}Ganando`] || 0,
      empatando: this.estadisticasEquipo[`${prefijo}Empatando`] || 0,
      perdiendo: this.estadisticasEquipo[`${prefijo}Perdiendo`] || 0
    };
    console.log('✅ Resultado:', resultado);
    return resultado;
  }

  getTotalResultado(): number {
    const valores = this.getValoresResultado();
    return valores.ganando + valores.empatando + valores.perdiendo;
  }

  getPrefijoEvento(): string {
    if (this.eventoSeleccionado === 'pasesClave') return 'pasesClave';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'tirosAPuerta';
    if (this.eventoSeleccionado === 'robos') return 'robos';
    if (this.eventoSeleccionado === 'perdidas') return 'perdidas';
    return '';
  }

  getTituloEvento(): string {
    if (this.eventoSeleccionado === 'pasesClave') return 'Pases Clave';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'Tiros a Puerta';
    if (this.eventoSeleccionado === 'robos') return 'Robos';
    if (this.eventoSeleccionado === 'perdidas') return 'Pérdidas';
    return '';
  }

  getIconoEvento(): string {
    if (this.eventoSeleccionado === 'pasesClave') return 'football';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'trophy';
    if (this.eventoSeleccionado === 'robos') return 'shield';
    if (this.eventoSeleccionado === 'perdidas') return 'close-circle';
    return 'statsChart';
  }

  getTotalEvento(): number {
    if (!this.estadisticasEquipo) return 0;
    
    let campo = '';
    if (this.eventoSeleccionado === 'pasesClave') campo = 'totalPasesClave';
    else if (this.eventoSeleccionado === 'tirosAPuerta') campo = 'totalTirosAPuerta';
    else if (this.eventoSeleccionado === 'robos') campo = 'totalRobos';
    else if (this.eventoSeleccionado === 'perdidas') campo = 'totalPerdidas';
    
    console.log('🔍 Buscando campo total:', campo, '=', this.estadisticasEquipo[campo]);
    return this.estadisticasEquipo[campo] || 0;
  }

  getP90Evento(): number {
    if (!this.estadisticasEquipo) return 0;
    const prefijo = this.getPrefijoEvento();
    const campo = `${prefijo}P90`;
    console.log('🔍 Buscando campo P90:', campo, '=', this.estadisticasEquipo[campo]);
    return this.estadisticasEquipo[campo] || 0;
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
    if (this.eventoSeleccionado === 'pasesClave') return 'totalPasesClave';
    if (this.eventoSeleccionado === 'tirosAPuerta') return 'totalTirosAPuerta';
    if (this.eventoSeleccionado === 'robos') return 'totalRobos';
    if (this.eventoSeleccionado === 'perdidas') return 'totalPerdidas';
    return 'totalGoles';
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

  async actualizarEstadisticas() {
    if (!this.equipoSeleccionado) {
      console.log('⚠️ No hay equipo seleccionado');
      return;
    }

    const loading = await this.loadingController.create({
      message: 'Recalculando estadísticas...'
    });
    await loading.present();

    this.estadisticasService.actualizarEstadisticasEquipo(this.equipoSeleccionado, '2025-2026')
      .subscribe({
        next: (response) => {
          console.log('✅ Estadísticas actualizadas:', response);
          loading.dismiss();
          this.cargarEstadisticas();
        },
        error: (error) => {
          console.error('❌ Error al actualizar:', error);
          loading.dismiss();
        }
      });
  }
}

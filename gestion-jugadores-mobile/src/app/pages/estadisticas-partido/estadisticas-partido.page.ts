import { Component, OnInit, ViewChild, ElementRef, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonButtons, IonIcon,
  IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner,
  IonChip, IonLabel, IonSegment, IonSegmentButton, IonList, IonItem,
  IonBadge, IonProgressBar, AlertController
} from '@ionic/angular/standalone';
import { EventoJugadorService } from '../../core/services/evento-jugador.service';
import { PartidoService } from '../../core/services/partido.service';
import { JugadorService } from '../../core/services/jugador.service';
import { AuthService } from '../../core/services/auth.service';
import { EstadisticasPartido, EventoJugadorResumen, TopJugador } from '../../core/models/estadisticas-partido';
import { EventoJugador } from '../../core/models/partido';

@Component({
  selector: 'app-estadisticas-partido',
  templateUrl: './estadisticas-partido.page.html',
  styleUrls: ['./estadisticas-partido.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonButtons, IonIcon,
    IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonSpinner,
    IonChip, IonLabel, IonSegment, IonSegmentButton, IonList, IonItem,
    IonBadge, IonProgressBar,
    CommonModule, FormsModule
  ]
})
export class EstadisticasPartidoPage implements OnInit, AfterViewInit {

  @ViewChild('timelineWrapper', { read: ElementRef }) timelineWrapper?: ElementRef;

  partidoId: number = 0;
  cargando = true;
  estadisticas: EstadisticasPartido | null = null;
  eventos: EventoJugador[] = [];
  
  // Vista seleccionada
  vistaSeleccionada: 'general' | 'pasesClave' | 'tirosAPuerta' | 'robos' | 'perdidas' = 'general';
  seccionSeleccionada: 'resultado' | 'tiempo' = 'resultado';

  // Array de minutos para la línea de tiempo (cada 5 minutos)
  minutosArray: number[] = [0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75, 80, 85, 90];
  
  // Propiedades para zoom con pinch en timeline
  timelineZoom: number = 1;
  lastTouchDistance: number = 0;
  isTimelineZooming: boolean = false;
  
  // Ancho de la línea de tiempo (px por minuto * 90 minutos + padding)
  readonly TIMELINE_WIDTH: number = 2400; // ~26.6px por minuto
  readonly TIMELINE_PADDING: number = 40; // padding lateral

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private eventoService: EventoJugadorService,
    private partidoService: PartidoService,
    private jugadorService: JugadorService,
    private alertController: AlertController,
    private authService: AuthService
  ) {}

  ngOnInit() {
    this.partidoId = parseInt(this.route.snapshot.params['id']);
    this.cargarEstadisticas();
  }

  ngAfterViewInit() {
    // Método implementado para interfaz AfterViewInit
  }

  volver() {
    this.router.navigate(['/tabs/partidos']);
  }

  cerrarSesion() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }

  cargarEstadisticas() {
    this.cargando = true;

    // Cargar partido y eventos
    Promise.all([
      this.partidoService.obtenerPartidoPorId(this.partidoId).toPromise(),
      this.eventoService.obtenerEventosPorPartido(this.partidoId).toPromise()
    ]).then(([partido, eventos]: any[]) => {
      console.log('📊 Partido recibido:', partido);
      console.log('📋 Eventos recibidos:', eventos);
      if (partido && eventos) {
        this.eventos = eventos;
        this.procesarEstadisticas(partido, eventos);
      }
      this.cargando = false;
    }).catch(error => {
      console.error('❌ Error al cargar estadísticas:', error);
      this.cargando = false;
    });
  }

  procesarEstadisticas(partido: any, eventos: EventoJugador[]) {
    // Agrupar eventos por jugador
    const eventosPorJugador = new Map<number, EventoJugadorResumen>();
    
    // Rastrear marcador a lo largo del partido para clasificar por resultado
    let golesEquipo = 0;
    let golesRival = 0;
    
    // Contadores por resultado
    let pasesClave_ganando = 0, pasesClave_empatando = 0, pasesClave_perdiendo = 0;
    let tirosAPuerta_ganando = 0, tirosAPuerta_empatando = 0, tirosAPuerta_perdiendo = 0;
    let robos_ganando = 0, robos_empatando = 0, robos_perdiendo = 0;
    let perdidas_ganando = 0, perdidas_empatando = 0, perdidas_perdiendo = 0;
    
    // Distribución temporal
    const distribucionPasesClave = { intervalo0_15: 0, intervalo16_30: 0, intervalo31_45: 0, intervalo46_60: 0, intervalo61_75: 0, intervalo76_90: 0 };
    const distribucionTirosAPuerta = { intervalo0_15: 0, intervalo16_30: 0, intervalo31_45: 0, intervalo46_60: 0, intervalo61_75: 0, intervalo76_90: 0 };
    const distribucionRobos = { intervalo0_15: 0, intervalo16_30: 0, intervalo31_45: 0, intervalo46_60: 0, intervalo61_75: 0, intervalo76_90: 0 };
    const distribucionPerdidas = { intervalo0_15: 0, intervalo16_30: 0, intervalo31_45: 0, intervalo46_60: 0, intervalo61_75: 0, intervalo76_90: 0 };
    
    // Ordenar eventos por minuto
    const eventosOrdenados = eventos.sort((a, b) => a.minuto - b.minuto);
    
    eventosOrdenados.forEach(evento => {
      // Normalizar tipo de evento (minúsculas y espacios a guiones bajos)
      const tipoEvento = (evento.tipoEvento || '').toLowerCase().replace(/\s+/g, '_');

      // IMPORTANTE: Procesar goles rivales PRIMERO para actualizar el marcador
      // antes de calcular la situación para otros eventos
      if (tipoEvento === 'gol_rival') {
        golesRival++;
      }
      
      // Extraer jugadorId (puede venir como jugadorId o dentro de jugador.id)
      const jugadorId = evento.jugadorId || (evento as any).jugador?.id;
      
      // Actualizar resumen del jugador (solo para eventos con jugador)
      if (jugadorId) {
        if (!eventosPorJugador.has(jugadorId)) {
          eventosPorJugador.set(jugadorId, {
            jugadorId: jugadorId,
            jugadorNombre: '',
            goles: 0,
            asistencias: 0,
            pasesClave: 0,
            tarjetasAmarillas: 0,
            tarjetasRojas: 0,
            robos: 0,
            tirosAPuerta: 0,
            perdidas: 0
          });
        }
        
        const resumen = eventosPorJugador.get(jugadorId)!;;
        
        // Determinar situación del resultado ANTES del evento
        let situacion: 'ganando' | 'empatando' | 'perdiendo';
        if (golesEquipo > golesRival) situacion = 'ganando';
        else if (golesEquipo < golesRival) situacion = 'perdiendo';
        else situacion = 'empatando';
        
        switch (tipoEvento) {
          case 'gol':
            resumen.goles++;
            golesEquipo++;
            break;
          case 'asistencia':
            resumen.asistencias++;
            break;
          case 'pase_clave':
            resumen.pasesClave++;
            this.agregarADistribucion(distribucionPasesClave, evento.minuto);
            if (situacion === 'ganando') pasesClave_ganando++;
            else if (situacion === 'empatando') pasesClave_empatando++;
            else pasesClave_perdiendo++;
            break;
          case 'robo':
            resumen.robos++;
            this.agregarADistribucion(distribucionRobos, evento.minuto);
            if (situacion === 'ganando') robos_ganando++;
            else if (situacion === 'empatando') robos_empatando++;
            else robos_perdiendo++;
            break;
          case 'perdida':
            resumen.perdidas++;
            this.agregarADistribucion(distribucionPerdidas, evento.minuto);
            if (situacion === 'ganando') perdidas_ganando++;
            else if (situacion === 'empatando') perdidas_empatando++;
            else perdidas_perdiendo++;
            break;
          case 'tiro_a_puerta':
          case 'tiro_puerta':
            resumen.tirosAPuerta++;
            this.agregarADistribucion(distribucionTirosAPuerta, evento.minuto);
            if (situacion === 'ganando') tirosAPuerta_ganando++;
            else if (situacion === 'empatando') tirosAPuerta_empatando++;
            else tirosAPuerta_perdiendo++;
            break;
          case 'tarjeta_amarilla':
            resumen.tarjetasAmarillas++;
            break;
          case 'tarjeta_roja':
            resumen.tarjetasRojas++;
            break;
        }
      }
    });
    
    // Cargar nombres de jugadores
    const jugadorIds = Array.from(eventosPorJugador.keys());
    Promise.all(jugadorIds.map(id => this.jugadorService.obtenerJugadorPorId(id).toPromise()))
      .then((jugadores: any[]) => {
        jugadores.forEach(jugador => {
          if (jugador && eventosPorJugador.has(jugador.id)) {
            eventosPorJugador.get(jugador.id)!.jugadorNombre = `${jugador.nombre} ${jugador.apellido}`;
          }
        });
      });
    
    // Calcular totales
    const eventosPorJugadorArray = Array.from(eventosPorJugador.values());
    
    // Determinar resultado (usar valores del partido o los calculados)
    const golesEquipoFinal = partido.golesEquipo !== undefined ? partido.golesEquipo : golesEquipo;
    const golesRivalFinal = partido.golesRival !== undefined ? partido.golesRival : golesRival;
    
    let resultado = 'EMPATE';
    if (golesEquipoFinal > golesRivalFinal) resultado = 'VICTORIA';
    else if (golesEquipoFinal < golesRivalFinal) resultado = 'DERROTA';
    
    console.log('⚽ Marcador final:', golesEquipoFinal, '-', golesRivalFinal, '→', resultado);
    
    this.estadisticas = {
      id: partido.id,
      equipoId: partido.equipoId || partido.equipo?.id,
      equipoNombre: partido.equipo?.nombre || 'Equipo',
      fecha: partido.fecha,
      titulo: partido.titulo,
      duracion: partido.duracion,
      resultado: resultado,
      golesEquipo: golesEquipoFinal,
      golesRival: golesRivalFinal,
      eventosPorJugador: eventosPorJugadorArray,
      totalGoles: eventosPorJugadorArray.reduce((sum, j) => sum + j.goles, 0),
      totalAsistencias: eventosPorJugadorArray.reduce((sum, j) => sum + j.asistencias, 0),
      totalPasesClave: eventosPorJugadorArray.reduce((sum, j) => sum + j.pasesClave, 0),
      totalTarjetasAmarillas: eventosPorJugadorArray.reduce((sum, j) => sum + j.tarjetasAmarillas, 0),
      totalTarjetasRojas: eventosPorJugadorArray.reduce((sum, j) => sum + j.tarjetasRojas, 0),
      totalRobos: eventosPorJugadorArray.reduce((sum, j) => sum + j.robos, 0),
      totalTirosAPuerta: eventosPorJugadorArray.reduce((sum, j) => sum + j.tirosAPuerta, 0),
      totalPerdidas: eventosPorJugadorArray.reduce((sum, j) => sum + j.perdidas, 0),
      distribucionPasesClave,
      distribucionTirosAPuerta,
      distribucionRobos,
      distribucionPerdidas,
      pasesClave_ganando,
      pasesClave_empatando,
      pasesClave_perdiendo,
      tirosAPuerta_ganando,
      tirosAPuerta_empatando,
      tirosAPuerta_perdiendo,
      robos_ganando,
      robos_empatando,
      robos_perdiendo,
      perdidas_ganando,
      perdidas_empatando,
      perdidas_perdiendo
    };
  }

  agregarADistribucion(distribucion: any, minuto: number) {
    if (minuto <= 15) distribucion.intervalo0_15++;
    else if (minuto <= 30) distribucion.intervalo16_30++;
    else if (minuto <= 45) distribucion.intervalo31_45++;
    else if (minuto <= 60) distribucion.intervalo46_60++;
    else if (minuto <= 75) distribucion.intervalo61_75++;
    else distribucion.intervalo76_90++;
  }

  obtenerTopJugador(tipo: 'pasesClave' | 'tirosAPuerta' | 'robos' | 'perdidas'): TopJugador | null {
    if (!this.estadisticas || this.estadisticas.eventosPorJugador.length === 0) return null;
    
    const jugadorTop = this.estadisticas.eventosPorJugador
      .filter(j => j[tipo] > 0)
      .sort((a, b) => b[tipo] - a[tipo])[0];
    
    if (!jugadorTop) return null;
    
    return {
      jugadorNombre: jugadorTop.jugadorNombre,
      cantidad: jugadorTop[tipo]
    };
  }

  getColorResultado(): string {
    if (!this.estadisticas) return 'medium';
    if (this.estadisticas.resultado === 'VICTORIA') return 'success';
    if (this.estadisticas.resultado === 'DERROTA') return 'danger';
    return 'warning';
  }

  getIconoResultado(): string {
    if (!this.estadisticas) return 'help-circle';
    if (this.estadisticas.resultado === 'VICTORIA') return 'trophy';
    if (this.estadisticas.resultado === 'DERROTA') return 'close-circle';
    return 'remove-circle';
  }

  calcularPorcentaje(valor: number, total: number): number {
    return total > 0 ? (valor / total) * 100 : 0;
  }

  getArrayIntervalosTiempo(): number[] {
    if (!this.estadisticas) return [];
    
    let distribucion: any = {};
    if (this.vistaSeleccionada === 'pasesClave') {
      distribucion = this.estadisticas.distribucionPasesClave;
    } else if (this.vistaSeleccionada === 'tirosAPuerta') {
      distribucion = this.estadisticas.distribucionTirosAPuerta;
    } else if (this.vistaSeleccionada === 'robos') {
      distribucion = this.estadisticas.distribucionRobos;
    } else if (this.vistaSeleccionada === 'perdidas') {
      distribucion = this.estadisticas.distribucionPerdidas;
    }
    
    return [
      distribucion.intervalo0_15 || 0,
      distribucion.intervalo16_30 || 0,
      distribucion.intervalo31_45 || 0,
      distribucion.intervalo46_60 || 0,
      distribucion.intervalo61_75 || 0,
      distribucion.intervalo76_90 || 0
    ];
  }

  getLabelsIntervalosTiempo(): string[] {
    return ['0-15', '16-30', '31-45', '46-60', '61-75', '76-90'];
  }

  getTotalIntervalosTiempo(): number {
    const valores = this.getArrayIntervalosTiempo();
    return valores.reduce((total, valor) => total + valor, 0);
  }

  getValoresResultado(): { ganando: number, empatando: number, perdiendo: number } {
    if (!this.estadisticas) return { ganando: 0, empatando: 0, perdiendo: 0 };
    
    if (this.vistaSeleccionada === 'pasesClave') {
      return {
        ganando: this.estadisticas.pasesClave_ganando,
        empatando: this.estadisticas.pasesClave_empatando,
        perdiendo: this.estadisticas.pasesClave_perdiendo
      };
    } else if (this.vistaSeleccionada === 'tirosAPuerta') {
      return {
        ganando: this.estadisticas.tirosAPuerta_ganando,
        empatando: this.estadisticas.tirosAPuerta_empatando,
        perdiendo: this.estadisticas.tirosAPuerta_perdiendo
      };
    } else if (this.vistaSeleccionada === 'robos') {
      return {
        ganando: this.estadisticas.robos_ganando,
        empatando: this.estadisticas.robos_empatando,
        perdiendo: this.estadisticas.robos_perdiendo
      };
    } else if (this.vistaSeleccionada === 'perdidas') {
      return {
        ganando: this.estadisticas.perdidas_ganando,
        empatando: this.estadisticas.perdidas_empatando,
        perdiendo: this.estadisticas.perdidas_perdiendo
      };
    }
    
    return { ganando: 0, empatando: 0, perdiendo: 0 };
  }

  getTituloEvento(): string {
    if (this.vistaSeleccionada === 'pasesClave') return 'Pases Clave';
    if (this.vistaSeleccionada === 'tirosAPuerta') return 'Tiros a Puerta';
    if (this.vistaSeleccionada === 'robos') return 'Robos';
    if (this.vistaSeleccionada === 'perdidas') return 'Pérdidas';
    return '';
  }

  getIconoEvento(): string {
    if (this.vistaSeleccionada === 'pasesClave') return 'football-outline';
    if (this.vistaSeleccionada === 'tirosAPuerta') return 'navigate-circle-outline';
    if (this.vistaSeleccionada === 'robos') return 'shield-outline';
    if (this.vistaSeleccionada === 'perdidas') return 'close-circle-outline';
    return 'stats-chart-outline';
  }

  /**
   * Obtiene los eventos filtrados según la vista seleccionada
   */
  getEventosFiltrados(): EventoJugador[] {
    if (!this.eventos || this.eventos.length === 0) return [];
    
    // Si está en vista general, mostrar todos los eventos relevantes
    if (this.vistaSeleccionada === 'general') {
      return this.eventos.filter(e => {
        const tipo = (e.tipoEvento || '').toLowerCase().replace(/\s+/g, '_');
        return ['gol', 'gol_rival', 'asistencia', 'tarjeta_amarilla', 'tarjeta_roja', 'sustitucion'].includes(tipo);
      });
    }
    
    // Filtrar según el tipo seleccionado
    const tipoFiltro = this.vistaSeleccionada === 'pasesClave' ? 'pase_clave' :
                       this.vistaSeleccionada === 'tirosAPuerta' ? 'tiro' :
                       this.vistaSeleccionada === 'robos' ? 'robo' :
                       this.vistaSeleccionada === 'perdidas' ? 'perdida' : '';
    
    return this.eventos.filter(e => {
      const tipo = (e.tipoEvento || '').toLowerCase().replace(/\s+/g, '_');
      
      if (this.vistaSeleccionada === 'tirosAPuerta') {
        return tipo === 'tiro_a_puerta' || tipo === 'tiro_puerta';
      }
      
      return tipo === tipoFiltro;
    }).sort((a, b) => a.minuto - b.minuto);
  }

  /**
   * Obtiene el icono para un tipo de evento
   */
  getIconoEventoTipo(tipoEvento: string): string {
    const tipo = (tipoEvento || '').toLowerCase().replace(/\s+/g, '_');
    
    switch(tipo) {
      case 'gol': return '⚽';
      case 'gol_rival': return '😞';
      case 'asistencia': return '🎯';
      case 'pase_clave': return '🎪';
      case 'tiro_a_puerta':
      case 'tiro_puerta': return '🎯';
      case 'robo': return '🛡️';
      case 'perdida': return '❌';
      case 'tarjeta_amarilla': return '🟨';
      case 'tarjeta_roja': return '🟥';
      case 'sustitucion': return '🔄';
      default: return '📍';
    }
  }

  /**
   * Obtiene el color para un tipo de evento
   */
  getColorEvento(tipoEvento: string): string {
    const tipo = (tipoEvento || '').toLowerCase().replace(/\s+/g, '_');
    
    switch(tipo) {
      case 'gol': return '#10dc60';
      case 'gol_rival': return '#f04141';
      case 'asistencia': return '#3880ff';
      case 'pase_clave': return '#7044ff';
      case 'tiro_a_puerta':
      case 'tiro_puerta': return '#ffc409';
      case 'robo': return '#ffce00';
      case 'perdida': return '#f04141';
      case 'tarjeta_amarilla': return '#ffc409';
      case 'tarjeta_roja': return '#f04141';
      case 'sustitucion': return '#92949c';
      default: return '#3880ff';
    }
  }

  /**
   * Formatea el tipo de evento para mostrarlo
   */
  private formatearTipoEvento(tipo: string): string {
    const tipoNormalizado = (tipo || '').toLowerCase().replace(/\s+/g, '_');
    
    const nombres: { [key: string]: string } = {
      'gol': 'Gol',
      'gol_rival': 'Gol Rival',
      'asistencia': 'Asistencia',
      'pase_clave': 'Pase Clave',
      'tiro_a_puerta': 'Tiro a Puerta',
      'tiro_puerta': 'Tiro a Puerta',
      'robo': 'Robo',
      'perdida': 'Pérdida',
      'tarjeta_amarilla': 'Tarjeta Amarilla',
      'tarjeta_roja': 'Tarjeta Roja',
      'sustitucion': 'Sustitución'
    };
    
    return nombres[tipoNormalizado] || tipo;
  }

  /**
   * Muestra los detalles de un evento al hacer clic
   */
  async mostrarDetalleEvento(evento: EventoJugador) {
    // Obtener nombre del jugador si existe
    let jugadorNombre = 'Sin asignar';
    
    if (evento.jugadorId) {
      try {
        const jugador = await this.jugadorService.obtenerJugadorPorId(evento.jugadorId).toPromise();
        if (jugador) {
          jugadorNombre = `${jugador.nombre} ${jugador.apellido}`;
        }
      } catch (error) {
        console.warn('No se pudo cargar el jugador:', error);
      }
    } else if ((evento as any).jugador) {
      const jug = (evento as any).jugador;
      jugadorNombre = `${jug.nombre || ''} ${jug.apellido || ''}`;
    }

    // Formatear tipo de evento
    const tipoEvento = this.formatearTipoEvento(evento.tipoEvento);
    
    // Crear alert con la información (sin HTML, solo texto)
    const alert = await this.alertController.create({
      header: `${this.getIconoEventoTipo(evento.tipoEvento)} ${tipoEvento}`,
      message: `⏱️ Minuto: ${evento.minuto}'\n\n👤 Jugador:\n${jugadorNombre}`,
      buttons: ['Cerrar'],
      cssClass: 'evento-detalle-alert'
    });

    await alert.present();
  }
  
  /**
   * Calcula la posición en px de un marcador de minuto
   */
  getMarkerPosition(minuto: number): number {
    const usableWidth = this.TIMELINE_WIDTH - (this.TIMELINE_PADDING * 2);
    return this.TIMELINE_PADDING + (minuto / 90) * usableWidth;
  }
  
  /**
   * Calcula la posición en px de un evento
   */
  getEventPosition(minuto: number): number {
    const usableWidth = this.TIMELINE_WIDTH - (this.TIMELINE_PADDING * 2);
    return this.TIMELINE_PADDING + (minuto / 90) * usableWidth;
  }
  
  /**
   * Maneja el inicio del gesto de zoom (pinch) en timeline
   */
  onTimelineTouchStart(event: TouchEvent) {
    if (event.touches.length === 2) {
      this.isTimelineZooming = true;
      this.lastTouchDistance = this.getTouchDistance(event.touches[0], event.touches[1]);
      event.preventDefault();
    }
  }
  
  /**
   * Maneja el movimiento del gesto de zoom en timeline
   */
  onTimelineTouchMove(event: TouchEvent) {
    if (this.isTimelineZooming && event.touches.length === 2) {
      const currentDistance = this.getTouchDistance(event.touches[0], event.touches[1]);
      const distanceDelta = currentDistance - this.lastTouchDistance;
      
      // Ajustar zoom basado en el cambio de distancia
      const zoomDelta = distanceDelta * 0.005;
      this.timelineZoom = Math.max(1, Math.min(4, this.timelineZoom + zoomDelta));
      
      this.lastTouchDistance = currentDistance;
      event.preventDefault();
    }
  }
  
  /**
   * Maneja el fin del gesto de zoom en timeline
   */
  onTimelineTouchEnd(event: TouchEvent) {
    if (event.touches.length < 2) {
      this.isTimelineZooming = false;
    }
  }
  
  /**
   * Calcula la distancia entre dos puntos touch
   */
  private getTouchDistance(touch1: Touch, touch2: Touch): number {
    const dx = touch1.clientX - touch2.clientX;
    const dy = touch1.clientY - touch2.clientY;
    return Math.sqrt(dx * dx + dy * dy);
  }
  
  /**
   * Resetea el zoom de la timeline
   */
  resetTimelineZoom() {
    this.timelineZoom = 1;
  }
}


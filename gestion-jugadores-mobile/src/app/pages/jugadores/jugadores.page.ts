import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertController, LoadingController, ModalController } from '@ionic/angular';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
  IonFab, IonFabButton, IonSearchbar, IonRefresher, IonRefresherContent, 
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner, IonSelect, IonSelectOption, IonItem, IonLabel
} from '@ionic/angular/standalone';
import { JugadorService } from '../../core/services/jugador.service';
import { EquipoService } from '../../core/services/equipo.service';
import { RefreshService } from '../../core/services/refresh.service';
import { AiAnalysisService } from '../../core/services/ai-analysis.service';
import { Jugador } from '../../core/models/jugador';
import { Equipo } from '../../core/models/equipo';
import { InformeIaModalComponent } from './informe-ia-modal.component';

@Component({
  selector: 'app-jugadores',
  templateUrl: './jugadores.page.html',
  styleUrls: ['./jugadores.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
    IonFab, IonFabButton, IonSearchbar, IonRefresher, IonRefresherContent, 
    IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner, IonSelect, IonSelectOption, IonItem, IonLabel,
    CommonModule, FormsModule
  ],
  providers: [AlertController, LoadingController, ModalController]
})
export class JugadoresPage implements OnInit {

  jugadores: Jugador[] = [];
  jugadoresFiltrados: Jugador[] = [];
  equipos: Equipo[] = [];
  equipoSeleccionado: number | null = null;
  cargando: boolean = false;
  textoBusqueda: string = '';

  constructor(
    private jugadorService: JugadorService,
    private equipoService: EquipoService,
    private refreshService: RefreshService,
    private aiService: AiAnalysisService,
    private router: Router,
    private alertController: AlertController,
    private loadingCtrl: LoadingController,
    private modalCtrl: ModalController
  ) { }

  ngOnInit() {
    // Solo carga inicial de equipos
    this.cargarEquipos();
    
    // Suscribirse a eventos de refresco
    this.refreshService.onJugadoresRefresh.subscribe(() => {
      console.log('📢 Evento de refresco recibido en JugadoresPage');
      this.cargarJugadores();
    });
  }

  ionViewWillEnter() {
    // Se ejecuta cada vez que se entra a la página
    // Esto asegura que los datos se recarguen después de crear/editar
    this.cargarJugadores();
  }

  cargarJugadores() {
    this.cargando = true;
    
    // Obtener jugadores del usuario autenticado desde el backend
    this.jugadorService.obtenerJugadoresPorUsuario().subscribe({
      next: (jugadores) => {
        console.log('✅ Jugadores cargados desde backend:', jugadores);
        this.jugadores = jugadores;
        
        // DEBUG: Ver posiciones reales
        console.log('🔍 Posiciones de jugadores:', 
          jugadores.map(j => ({ nombre: j.nombre, posicion: j.posicion }))
        );
        
        this.aplicarFiltros();
        this.cargando = false;
      },
      error: (error) => {
        console.error('❌ Error al cargar jugadores:', error);
        console.error('Status:', error.status);
        console.error('Message:', error.message);
        this.cargando = false;
        
        // Mostrar lista vacía en caso de error
        this.jugadores = [];
        this.jugadoresFiltrados = [];
      }
    });
  }

  cargarEquipos() {
    this.equipoService.obtenerEquiposMe().subscribe({
      next: (equipos) => {
        console.log('✅ Equipos cargados para filtro:', equipos);
        this.equipos = equipos;
      },
      error: (error) => {
        console.error('❌ Error al cargar equipos:', error);
        this.equipos = [];
      }
    });
  }

  buscarJugadores(event: any) {
    this.textoBusqueda = event.target.value.toLowerCase();
    this.aplicarFiltros();
  }

  filtrarPorEquipo(event: any) {
    this.equipoSeleccionado = event.detail.value;
    this.aplicarFiltros();
  }

  aplicarFiltros() {
    let jugadoresFiltrados = [...this.jugadores];

    // Filtrar por equipo si hay uno seleccionado
    if (this.equipoSeleccionado !== null) {
      jugadoresFiltrados = jugadoresFiltrados.filter(
        jugador => jugador.equipoId === this.equipoSeleccionado
      );
    }

    // Filtrar por texto de búsqueda
    if (this.textoBusqueda) {
      jugadoresFiltrados = jugadoresFiltrados.filter(jugador => 
        jugador.nombre.toLowerCase().includes(this.textoBusqueda) ||
        jugador.apellido.toLowerCase().includes(this.textoBusqueda) ||
        jugador.posicion.toLowerCase().includes(this.textoBusqueda)
      );
    }

    this.jugadoresFiltrados = jugadoresFiltrados;
  }

  refrescarJugadores(event: any) {
    this.cargarJugadores();
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

  obtenerJugadoresPorPosicion(posicion: string): Jugador[] {
    // Agrupar posiciones según sus abreviaciones en la base de datos
    const gruposDefensas = ['DEF', 'LD', 'LI', 'CEN', 'DEFENSA', 'LATERAL_DERECHO', 'LATERAL_IZQUIERDO', 'CENTRAL'];
    const gruposMedios = ['MC', 'MCO', 'MCD', 'CENTROCAMPISTA', 'MEDIO_CENTRO', 'MEDIOCENTRO_DEFENSIVO', 'MEDIOCENTRO_OFENSIVO'];
    const gruposDelanteros = ['DC', 'DEL', 'EXD', 'EXI', 'EXIZ', 'DELANTERO', 'DELANTERO_CENTRO', 'EXTREMO_DERECHO', 'EXTREMO_IZQUIERDO'];
    
    return this.jugadores.filter(jugador => {
      const posicionJugador = jugador.posicion.toUpperCase();
      
      if (posicion === 'DELANTERO') {
        return gruposDelanteros.includes(posicionJugador);
      } else if (posicion === 'CENTROCAMPISTA') {
        return gruposMedios.includes(posicionJugador);
      } else if (posicion === 'DEFENSA') {
        return gruposDefensas.includes(posicionJugador);
      } else {
        return posicionJugador === posicion;
      }
    });
  }

  agregarJugador() {
    this.router.navigate(['/jugador-form/0']);
  }

  editarJugador(jugador: Jugador) {
    this.router.navigate(['/jugador-form', jugador.id]);
  }

  async eliminarJugador(jugador: Jugador) {
    const alert = await this.alertController.create({
      header: '⚠️ Confirmar Eliminación',
      message: `¿Estás seguro de eliminar al jugador "${jugador.nombre} ${jugador.apellido}"?`,
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel',
          cssClass: 'secondary'
        },
        {
          text: 'Eliminar',
          role: 'destructive',
          handler: () => {
            this.jugadorService.eliminarJugador(jugador.id).subscribe({
              next: () => {
                console.log('✅ Jugador eliminado correctamente');
                this.cargarJugadores();
                this.mostrarMensajeExito('Jugador eliminado correctamente');
              },
              error: (error) => {
                console.error('❌ Error al eliminar jugador:', error);
                this.mostrarMensajeError('Error al eliminar el jugador');
              }
            });
          }
        }
      ]
    });

    await alert.present();
  }

  async mostrarMensajeExito(mensaje: string) {
    const alert = await this.alertController.create({
      header: '✅ Éxito',
      message: mensaje,
      buttons: ['OK']
    });
    await alert.present();
  }

  async mostrarMensajeError(mensaje: string) {
    const alert = await this.alertController.create({
      header: '❌ Error',
      message: mensaje,
      buttons: ['OK']
    });
    await alert.present();
  }

  /**
   * Genera análisis técnico con IA para un jugador específico.
   * Optimizado para minimizar coste de tokens:
   * - Prompts estructurados en backend
   * - Respuestas limitadas a ~600 tokens
   * - Solo estadísticas agregadas, no datos crudos
   */
  async generarAnalisisIA(jugador: Jugador) {
    // Mostrar loading mientras genera el análisis
    const loading = await this.loadingCtrl.create({
      message: 'Generando análisis con IA...⚡',
      spinner: 'crescent',
      cssClass: 'custom-loading'
    });
    await loading.present();

    // Temporada por defecto (puedes hacer que sea configurable)
    const temporada = '2024/2025';

    this.aiService.generarInformeJugador(jugador.id, temporada).subscribe({
      next: async (informe) => {
        await loading.dismiss();
        console.log('✅ Informe IA generado:', informe);
        
        // Mostrar modal con el informe
        const modal = await this.modalCtrl.create({
          component: InformeIaModalComponent,
          componentProps: {
            informe: informe
          },
          cssClass: 'informe-modal'
        });
        await modal.present();
      },
      error: async (err) => {
        await loading.dismiss();
        console.error('❌ Error generando informe IA:', err);
        
        let mensaje = 'No se pudo generar el análisis. ';
        
        if (err.status === 404) {
          mensaje += 'El jugador no tiene estadísticas en esta temporada.';
        } else if (err.status === 500) {
          mensaje += 'Error del servidor. Verifica que el módulo de IA esté configurado.';
        } else if (err.status === 0) {
          mensaje += 'No se puede conectar con el servidor.';
        } else {
          mensaje += err.error?.mensaje || 'Error desconocido.';
        }
        
        const alert = await this.alertController.create({
          header: '⚠️ Error al generar análisis',
          message: mensaje,
          buttons: ['OK']
        });
        await alert.present();
      }
    });
  }

  /**
   * Navega a la página de análisis históricos del jugador.
   */
  verAnalisisJugador(jugador: Jugador) {
    this.router.navigate(['/analisis-jugador', jugador.id]);
  }

  /**
   * Obtiene las iniciales de la posición para mostrar en el badge.
   */
  obtenerInicialPosicion(posicion: string): string {
    const iniciales: { [key: string]: string } = {
      'PORTERO': 'POR',
      'DEFENSA': 'DEF',
      'LATERAL_DERECHO': 'LD',
      'LATERAL_IZQUIERDO': 'LI',
      'CENTRAL': 'DFC',
      'CENTROCAMPISTA': 'MC',
      'MEDIO_CENTRO': 'MC',
      'MEDIOCENTRO_DEFENSIVO': 'MCD',
      'MEDIOCENTRO_OFENSIVO': 'MCO',
      'EXTREMO_DERECHO': 'ED',
      'EXTREMO_IZQUIERDO': 'EI',
      'DELANTERO': 'DC',
      'DELANTERO_CENTRO': 'DC'
    };
    return iniciales[posicion] || posicion.substring(0, 3).toUpperCase();
  }

}

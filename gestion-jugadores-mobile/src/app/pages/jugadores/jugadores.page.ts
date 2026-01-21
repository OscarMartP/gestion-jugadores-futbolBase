import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
  IonFab, IonFabButton, IonSearchbar, IonRefresher, IonRefresherContent, 
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner, IonSelect, IonSelectOption, IonItem, IonLabel
} from '@ionic/angular/standalone';
import { JugadorService } from '../../core/services/jugador.service';
import { EquipoService } from '../../core/services/equipo.service';
import { Jugador } from '../../core/models/jugador';
import { Equipo } from '../../core/models/equipo';

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
  ]
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
    private router: Router,
    private alertController: AlertController
  ) { }

  ngOnInit() {
    // Solo carga inicial de equipos
    this.cargarEquipos();
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
    return this.jugadores.filter(jugador => jugador.posicion === posicion);
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

}

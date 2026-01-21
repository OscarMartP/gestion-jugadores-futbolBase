import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AlertController } from '@ionic/angular';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
  IonFab, IonFabButton, IonSearchbar, IonRefresher, IonRefresherContent, 
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner
} from '@ionic/angular/standalone';
import { EquipoService } from '../../core/services/equipo.service';
import { JugadorService } from '../../core/services/jugador.service';
import { Equipo } from '../../core/models/equipo';
import { Jugador } from '../../core/models/jugador';

@Component({
  selector: 'app-equipos',
  templateUrl: './equipos.page.html',
  styleUrls: ['./equipos.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
    IonFab, IonFabButton, IonSearchbar, IonRefresher, IonRefresherContent, 
    IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner, 
    CommonModule, FormsModule
  ]
})
export class EquiposPage implements OnInit {

  equipos: Equipo[] = [];
  equiposFiltrados: Equipo[] = [];
  jugadores: Jugador[] = []; // Para contar jugadores por equipo
  cargando: boolean = false;
  textoBusqueda: string = '';

  constructor(
    private equipoService: EquipoService,
    private jugadorService: JugadorService,
    private router: Router,
    private alertController: AlertController
  ) { }

  ngOnInit() {
    // Carga inicial
  }

  ionViewWillEnter() {
    // Se ejecuta cada vez que se entra a la página
    // Esto asegura que los datos se recarguen después de crear/editar
    this.cargarEquipos();
    this.cargarJugadores();
  }

  cargarEquipos() {
    this.cargando = true;
    
    // Conectar con el servicio real para obtener equipos del usuario autenticado
    this.equipoService.obtenerEquiposMe().subscribe({
      next: (equipos) => {
        console.log('✅ Equipos cargados desde el backend:', equipos);
        this.equipos = equipos;
        this.equiposFiltrados = [...equipos];
        this.cargando = false;
      },
      error: (error) => {
        console.error('❌ Error al cargar equipos:', error);
        console.error('Status:', error.status);
        console.error('Message:', error.message);
        this.cargando = false;
        
        // Si el error es de autenticación, mostrar equipos vacíos
        if (error.status === 401 || error.status === 403) {
          console.log('⚠️ Error de autenticación, mostrando lista vacía');
          this.equipos = [];
          this.equiposFiltrados = [];
        } else {
          // Para otros errores, también mostrar vacío en lugar de fallback
          this.equipos = [];
          this.equiposFiltrados = [];
        }
      }
    });
  }

  cargarJugadores() {
    // Cargar jugadores del usuario autenticado
    this.jugadorService.obtenerJugadoresPorUsuario().subscribe({
      next: (jugadores) => {
        console.log('✅ Jugadores cargados para mostrar en equipos:', jugadores);
        this.jugadores = jugadores;
      },
      error: (error) => {
        console.error('❌ Error al cargar jugadores:', error);
        this.jugadores = [];
      }
    });
  }

  buscarEquipos(event: any) {
    this.textoBusqueda = event.target.value.toLowerCase();
    this.equiposFiltrados = this.equipos.filter(equipo => 
      equipo.nombre.toLowerCase().includes(this.textoBusqueda)
    );
  }

  refrescarEquipos(event: any) {
    this.cargarEquipos();
    this.cargarJugadores();
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

  obtenerEquiposPorTipo(tipo: string): Equipo[] {
    return this.equipos.filter(equipo => equipo.tipoFutbol === tipo);
  }

  obtenerJugadoresDelEquipo(equipoId: number): Jugador[] {
    return this.jugadores.filter(jugador => jugador.equipoId === equipoId);
  }

  verEquipo(equipo: Equipo) {
    console.log('Ver equipo:', equipo);
    // Implementar navegación a detalles del equipo
  }

  editarEquipo(equipo: Equipo) {
    this.router.navigate(['/equipo-form', equipo.id]);
  }

  async eliminarEquipo(equipo: Equipo) {
    const jugadoresAsociados = this.obtenerJugadoresDelEquipo(equipo.id);
    const numJugadores = jugadoresAsociados.length;

    const alert = await this.alertController.create({
      header: '⚠️ Confirmar Eliminación',
      message: numJugadores > 0 
        ? `¿Estás seguro de eliminar el equipo "${equipo.nombre}"?<br><br><strong>⚠️ ATENCIÓN:</strong> Esto eliminará también los <strong>${numJugadores} jugador${numJugadores !== 1 ? 'es' : ''}</strong> asociado${numJugadores !== 1 ? 's' : ''} a este equipo.`
        : `¿Estás seguro de eliminar el equipo "${equipo.nombre}"?`,
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
            this.equipoService.eliminarEquipo(equipo.id).subscribe({
              next: () => {
                console.log('✅ Equipo eliminado correctamente');
                this.cargarEquipos();
                this.cargarJugadores();
                this.mostrarMensajeExito('Equipo eliminado correctamente');
              },
              error: (error) => {
                console.error('❌ Error al eliminar equipo:', error);
                this.mostrarMensajeError('Error al eliminar el equipo');
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

  agregarEquipo() {
    this.router.navigate(['/equipo-form/0']);
  }

}

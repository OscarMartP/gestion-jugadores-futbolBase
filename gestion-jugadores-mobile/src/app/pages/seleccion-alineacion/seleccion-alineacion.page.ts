import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon,
  IonList, IonItem, IonLabel, IonCheckbox, IonSelect, IonSelectOption,
  AlertController, IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonInput
} from '@ionic/angular/standalone';
import { EquipoService } from '../../core/services/equipo.service';
import { JugadorService } from '../../core/services/jugador.service';
import { PartidoService } from '../../core/services/partido.service';
import { Equipo } from '../../core/models/equipo';
import { Jugador } from '../../core/models/jugador';
import { Partido } from '../../core/models/partido';

@Component({
  selector: 'app-seleccion-alineacion',
  templateUrl: './seleccion-alineacion.page.html',
  styleUrls: ['./seleccion-alineacion.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon,
    IonList, IonItem, IonLabel, IonCheckbox, IonSelect, IonSelectOption,
    IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonInput,
    CommonModule, FormsModule
  ]
})
export class SeleccionAlineacionPage implements OnInit {

  equipos: Equipo[] = [];
  jugadores: Jugador[] = [];
  equipoSeleccionado: Equipo | null = null;
  titulares: Jugador[] = [];
  suplentes: Jugador[] = [];
  maxTitulares = 11;
  nombrePartido: string = '';

  constructor(
    private equipoService: EquipoService,
    private jugadorService: JugadorService,
    private partidoService: PartidoService,
    private router: Router,
    private route: ActivatedRoute,
    private alertController: AlertController
  ) {}

  ngOnInit() {
    this.cargarEquipos();
  }

  cargarEquipos() {
    this.equipoService.obtenerEquiposMe().subscribe({
      next: (equipos) => {
        this.equipos = equipos;
        console.log('✅ Equipos cargados:', equipos);
      },
      error: (error) => {
        console.error('❌ Error al cargar equipos:', error);
      }
    });
  }

  onEquipoSeleccionado(event: any) {
    const equipoId = parseInt(event.detail.value);
    this.equipoSeleccionado = this.equipos.find(e => e.id === equipoId) || null;
    
    if (this.equipoSeleccionado) {
      // Determinar número de titulares según tipo de fútbol
      switch (this.equipoSeleccionado.tipoFutbol) {
        case 'FUTBOL_11':
          this.maxTitulares = 11;
          break;
        case 'FUTBOL_7':
          this.maxTitulares = 7;
          break;
        case 'FUTBOL_SALA':
          this.maxTitulares = 5;
          break;
        default:
          this.maxTitulares = 11;
      }
      
      this.cargarJugadores(equipoId);
    }
  }

  cargarJugadores(equipoId: number) {
    this.jugadorService.obtenerJugadoresPorUsuario().subscribe({
      next: (jugadores) => {
        this.jugadores = jugadores.filter(j => j.equipoId === equipoId);
        console.log(`✅ ${this.jugadores.length} jugadores cargados`);
        this.titulares = [];
        this.suplentes = [];
      },
      error: (error) => {
        console.error('❌ Error al cargar jugadores:', error);
      }
    });
  }

  onJugadorSeleccionado(jugador: Jugador, event: any) {
    const isChecked = event.detail.checked;
    
    if (isChecked) {
      // Intentar agregar como titular
      if (this.titulares.length < this.maxTitulares) {
        this.titulares.push(jugador);
      } else {
        // Si ya están completos los titulares, agregar como suplente
        this.suplentes.push(jugador);
      }
    } else {
      // Quitar jugador
      this.titulares = this.titulares.filter(j => j.id !== jugador.id);
      this.suplentes = this.suplentes.filter(j => j.id !== jugador.id);
    }
  }

  isJugadorSeleccionado(jugador: Jugador): boolean {
    return this.titulares.some(j => j.id === jugador.id) || 
           this.suplentes.some(j => j.id === jugador.id);
  }

  esTitular(jugador: Jugador): boolean {
    return this.titulares.some(j => j.id === jugador.id);
  }

  async moverATitulares(jugador: Jugador) {
    if (this.titulares.length >= this.maxTitulares) {
      const alert = await this.alertController.create({
        header: 'Titulares completos',
        message: `Ya tienes ${this.maxTitulares} titulares seleccionados.`,
        buttons: ['OK']
      });
      await alert.present();
      return;
    }

    this.suplentes = this.suplentes.filter(j => j.id !== jugador.id);
    this.titulares.push(jugador);
  }

  moverASuplentes(jugador: Jugador) {
    this.titulares = this.titulares.filter(j => j.id !== jugador.id);
    this.suplentes.push(jugador);
  }

  async iniciarPartido() {
    if (!this.nombrePartido || this.nombrePartido.trim() === '') {
      const alert = await this.alertController.create({
        header: 'Error',
        message: 'Debes ingresar el nombre del partido (ej: Helios vs Real Madrid).',
        buttons: ['OK']
      });
      await alert.present();
      return;
    }

    if (!this.equipoSeleccionado) {
      const alert = await this.alertController.create({
        header: 'Error',
        message: 'Debes seleccionar un equipo.',
        buttons: ['OK']
      });
      await alert.present();
      return;
    }

    if (this.titulares.length === 0) {
      const alert = await this.alertController.create({
        header: 'Error',
        message: 'Debes seleccionar al menos un titular.',
        buttons: ['OK']
      });
      await alert.present();
      return;
    }

    // Validación estricta: debe haber exactamente el número de titulares requeridos
    if (this.titulares.length !== this.maxTitulares) {
      const alert = await this.alertController.create({
        header: '⚠️ Titulares incompletos',
        message: `Debes seleccionar exactamente ${this.maxTitulares} titulares para ${this.equipoSeleccionado.tipoFutbol.replace('_', ' ')}. Actualmente tienes ${this.titulares.length}.`,
        buttons: ['OK']
      });
      await alert.present();
      return;
    }

    // Crear partido
    const nuevoPartido: any = {
      id: 0,
      equipo: {
        id: this.equipoSeleccionado.id,
        nombre: this.equipoSeleccionado.nombre,
        tipoFutbol: this.equipoSeleccionado.tipoFutbol
      },
      fecha: new Date().toISOString(),
      duracion: this.equipoSeleccionado.duracionPartido,
      titulo: this.nombrePartido.trim(),
      partidoActivo: true,
      titulares: this.titulares.map(j => j.id),
      suplentes: this.suplentes.map(j => j.id)
    };

    this.partidoService.crearPartido(nuevoPartido).subscribe({
      next: (partido) => {
        console.log('✅ Partido creado:', partido);
        this.router.navigate(['/modo-partido', partido.id]);
      },
      error: (error) => {
        console.error('❌ Error al crear partido:', error);
        this.mostrarError('Error al crear el partido');
      }
    });
  }

  async mostrarError(mensaje: string) {
    const alert = await this.alertController.create({
      header: '❌ Error',
      message: mensaje,
      buttons: ['OK']
    });
    await alert.present();
  }

  volver() {
    this.router.navigate(['/tabs/partidos']);
  }

  async cancelar() {
    const alert = await this.alertController.create({
      header: '¿Cancelar selección?',
      message: 'Se perderá la alineación actual.',
      buttons: [
        {
          text: 'No',
          role: 'cancel'
        },
        {
          text: 'Sí, cancelar',
          handler: () => {
            this.router.navigate(['/tabs/partidos']);
          }
        }
      ]
    });
    await alert.present();
  }
}

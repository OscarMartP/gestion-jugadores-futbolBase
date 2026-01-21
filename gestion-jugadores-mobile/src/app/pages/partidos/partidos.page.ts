import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
  IonFab, IonFabButton, IonRefresher, IonRefresherContent, 
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner,
  IonChip, IonLabel, AlertController
} from '@ionic/angular/standalone';

interface Partido {
  id: number;
  equipoLocal: string;
  equipoVisitante: string;
  fechaHora: Date;
  duracion: number;
  estado: 'PROGRAMADO' | 'EN_CURSO' | 'COMPLETADO' | 'CANCELADO';
  golesLocal?: number;
  golesVisitante?: number;
  ubicacion?: string;
}

@Component({
  selector: 'app-partidos',
  templateUrl: './partidos.page.html',
  styleUrls: ['./partidos.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
    IonFab, IonFabButton, IonRefresher, IonRefresherContent, 
    IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner,
    IonChip, IonLabel,
    CommonModule, FormsModule
  ]
})
export class PartidosPage implements OnInit {

  partidos: Partido[] = [];
  partidosFiltrados: Partido[] = [];
  cargando: boolean = false;
  filtroActivo: string = 'TODOS';

  constructor(
    private router: Router,
    private alertController: AlertController
  ) { }

  ngOnInit() {
    this.cargarPartidos();
  }

  cargarPartidos() {
    this.cargando = true;
    
    // Datos de ejemplo
    const partidosEjemplo: Partido[] = [
      {
        id: 1,
        equipoLocal: 'Real Madrid CF',
        equipoVisitante: 'FC Barcelona',
        fechaHora: new Date('2026-01-20T16:00:00'),
        duracion: 90,
        estado: 'PROGRAMADO',
        ubicacion: 'Estadio Santiago Bernabéu'
      },
      {
        id: 2,
        equipoLocal: 'Los Amigos FC',
        equipoVisitante: 'Barrio Unidos',
        fechaHora: new Date('2026-01-18T19:30:00'),
        duracion: 70,
        estado: 'EN_CURSO',
        golesLocal: 1,
        golesVisitante: 0,
        ubicacion: 'Campo Municipal'
      },
      {
        id: 3,
        equipoLocal: 'Real Madrid CF',
        equipoVisitante: 'Los Amigos FC',
        fechaHora: new Date('2026-01-15T18:00:00'),
        duracion: 90,
        estado: 'COMPLETADO',
        golesLocal: 3,
        golesVisitante: 1,
        ubicacion: 'Estadio Santiago Bernabéu'
      },
      {
        id: 4,
        equipoLocal: 'FC Barcelona',
        equipoVisitante: 'Barrio Unidos',
        fechaHora: new Date('2026-01-22T17:00:00'),
        duracion: 90,
        estado: 'PROGRAMADO',
        ubicacion: 'Camp Nou'
      },
      {
        id: 5,
        equipoLocal: 'Los Veteranos',
        equipoVisitante: 'Los Rookies',
        fechaHora: new Date('2026-01-12T16:30:00'),
        duracion: 50,
        estado: 'COMPLETADO',
        golesLocal: 2,
        golesVisitante: 2,
        ubicacion: 'Polideportivo Local'
      }
    ];

    // Simular delay de API
    setTimeout(() => {
      this.partidos = partidosEjemplo;
      this.aplicarFiltro();
      this.cargando = false;
    }, 1000);
  }

  filtrarPartidos(filtro: string) {
    this.filtroActivo = filtro;
    this.aplicarFiltro();
  }

  aplicarFiltro() {
    if (this.filtroActivo === 'TODOS') {
      this.partidosFiltrados = [...this.partidos];
    } else {
      this.partidosFiltrados = this.partidos.filter(partido => 
        partido.estado === this.filtroActivo
      );
    }
  }

  refrescarPartidos(event: any) {
    this.cargarPartidos();
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

  obtenerPartidosPorEstado(estado: string): Partido[] {
    return this.partidos.filter(partido => partido.estado === estado);
  }

  obtenerTextoEstado(estado: string): string {
    const estados: { [key: string]: string } = {
      'PROGRAMADO': 'Programado',
      'EN_CURSO': 'En Curso',
      'COMPLETADO': 'Terminado',
      'CANCELADO': 'Cancelado'
    };
    return estados[estado] || estado;
  }

  obtenerColorEstado(estado: string): string {
    const colores: { [key: string]: string } = {
      'PROGRAMADO': 'var(--ion-color-warning)',
      'EN_CURSO': 'var(--ion-color-tertiary)',
      'COMPLETADO': 'var(--ion-color-success)',
      'CANCELADO': 'var(--ion-color-danger)'
    };
    return colores[estado] || 'var(--ion-color-primary)';
  }

  formatearFecha(fecha: Date): string {
    return fecha.toLocaleDateString('es-ES', {
      weekday: 'short',
      day: 'numeric',
      month: 'short'
    });
  }

  formatearHora(fecha: Date): string {
    return fecha.toLocaleTimeString('es-ES', {
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  async iniciarPartido(partido: Partido) {
    const alert = await this.alertController.create({
      header: 'Iniciar Partido',
      message: `¿Deseas iniciar el partido ${partido.equipoLocal} vs ${partido.equipoVisitante}?`,
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Iniciar',
          handler: () => {
            console.log('Iniciar partido:', partido);
            // Lógica para cambiar estado a EN_CURSO
          }
        }
      ]
    });

    await alert.present();
  }

  gestionarPartido(partido: Partido) {
    console.log('Gestionar partido en curso:', partido);
    // Navegar a página de gestión del partido
  }

  editarPartido(partido: Partido) {
    console.log('Editar partido:', partido);
    // Navegar a formulario de edición
  }

  verDetalles(partido: Partido) {
    console.log('Ver detalles del partido:', partido);
    // Navegar a página de detalles
  }

  async eliminarPartido(partido: Partido) {
    const alert = await this.alertController.create({
      header: 'Eliminar Partido',
      message: `¿Estás seguro de que deseas eliminar el partido ${partido.equipoLocal} vs ${partido.equipoVisitante}?`,
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Eliminar',
          role: 'destructive',
          handler: () => {
            console.log('Eliminar partido:', partido);
            // Lógica de eliminación
          }
        }
      ]
    });

    await alert.present();
  }

  agregarPartido() {
    console.log('Agregar nuevo partido');
    // Navegar a formulario de creación de partido
  }

}

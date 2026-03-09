import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PartidoService } from '../../core/services/partido.service';
import { EquipoService } from '../../core/services/equipo.service';
import { RefreshService } from '../../core/services/refresh.service';
import { AuthService } from '../../core/services/auth.service';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonButtons, IonIcon, 
  IonFab, IonFabButton, IonRefresher, IonRefresherContent, 
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner,
  IonChip, IonLabel, AlertController
} from '@ionic/angular/standalone';
import { addIcons } from 'ionicons';
import { statsChart } from 'ionicons/icons';

interface PartidoView {
  id: number;
  titulo: string;
  equipoNombre: string;
  fecha: Date;
  duracion: number;
  partidoActivo: boolean;
  golesEquipo: number;
  golesRival: number;
  resultado: 'victoria' | 'empate' | 'derrota' | null;
}

@Component({
  selector: 'app-partidos',
  templateUrl: './partidos.page.html',
  styleUrls: ['./partidos.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonButtons, IonIcon, 
    IonFab, IonFabButton, IonRefresher, IonRefresherContent, 
    IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonSpinner,
    IonChip, IonLabel,
    CommonModule, FormsModule
  ]
})
export class PartidosPage implements OnInit {

  partidos: PartidoView[] = [];
  partidosFiltrados: PartidoView[] = [];
  cargando: boolean = false;
  filtroActivo: string = 'TODOS';
  equiposMap: Map<number, string> = new Map();

  constructor(
    private router: Router,
    private alertController: AlertController,
    private partidoService: PartidoService,
    private equipoService: EquipoService,
    private refreshService: RefreshService,
    private authService: AuthService
  ) {
    addIcons({ statsChart });
  }

  ngOnInit() {
    this.cargarPartidos();
    
    // Suscribirse a eventos de refresco
    this.refreshService.onPartidosRefresh.subscribe(() => {
      console.log('📢 Evento de refresco recibido en PartidosPage');
      this.cargarPartidos();
    });
  }

  ionViewWillEnter() {
    // Recargar partidos cada vez que se entra a esta página
    this.cargarPartidos();
  }

  iniciarNuevoPartido() {
    this.router.navigate(['/seleccion-alineacion']);
  }

  cargarPartidos() {
    this.cargando = true;
    
    // Primero cargar equipos para tener los nombres
    this.equipoService.obtenerEquiposMe().subscribe({
      next: (equipos) => {
        // Crear mapa de equipos
        equipos.forEach(equipo => {
          this.equiposMap.set(equipo.id, equipo.nombre);
        });
        
        // Cargar todos los partidos de todos los equipos del usuario
        const partidosObservables = equipos.map(equipo => 
          this.partidoService.obtenerPartidosPorEquipo(equipo.id)
        );
        
        // Combinar todos los partidos
        Promise.all(partidosObservables.map(obs => obs.toPromise()))
          .then((results: any[]) => {
            const todosPartidos: any[] = [];
            results.forEach(partidos => {
              if (partidos) {
                todosPartidos.push(...partidos);
              }
            });
            
            // Convertir a PartidoView
            this.partidos = todosPartidos.map(partido => this.convertirAPartidoView(partido));
            this.aplicarFiltro();
            this.cargando = false;
          })
          .catch(error => {
            console.error('❌ Error al cargar partidos:', error);
            this.cargando = false;
          });
      },
      error: (error) => {
        console.error('❌ Error al cargar equipos:', error);
        this.cargando = false;
      }
    });
  }

  convertirAPartidoView(partido: any): PartidoView {
    let resultado: 'victoria' | 'empate' | 'derrota' | null = null;
    
    // Determinar resultado solo si el partido está finalizado (partidoActivo === false)
    if (partido.partidoActivo === false && 
        partido.golesEquipo !== undefined && 
        partido.golesEquipo !== null &&
        partido.golesRival !== undefined && 
        partido.golesRival !== null) {
      if (partido.golesEquipo > partido.golesRival) {
        resultado = 'victoria';
      } else if (partido.golesEquipo < partido.golesRival) {
        resultado = 'derrota';
      } else {
        resultado = 'empate';
      }
    }
    
    return {
      id: partido.id,
      titulo: partido.titulo || 'Partido sin nombre',
      equipoNombre: partido.equipo?.nombre || this.equiposMap.get(partido.equipoId) || 'Equipo',
      fecha: new Date(partido.fecha),
      duracion: partido.duracion,
      partidoActivo: partido.partidoActivo,
      golesEquipo: partido.golesEquipo || 0,
      golesRival: partido.golesRival || 0,
      resultado: resultado
    };
  }

  filtrarPartidos(filtro: string) {
    this.filtroActivo = filtro;
    this.aplicarFiltro();
  }

  aplicarFiltro() {
    if (this.filtroActivo === 'TODOS') {
      this.partidosFiltrados = [...this.partidos];
    } else if (this.filtroActivo === 'GANADOS') {
      this.partidosFiltrados = this.partidos.filter(partido => partido.resultado === 'victoria');
    } else if (this.filtroActivo === 'EMPATADOS') {
      this.partidosFiltrados = this.partidos.filter(partido => partido.resultado === 'empate');
    } else if (this.filtroActivo === 'PERDIDOS') {
      this.partidosFiltrados = this.partidos.filter(partido => partido.resultado === 'derrota');
    } else if (this.filtroActivo === 'EN_CURSO') {
      this.partidosFiltrados = this.partidos.filter(partido => partido.partidoActivo === true);
    } else if (this.filtroActivo === 'COMPLETADO') {
      this.partidosFiltrados = this.partidos.filter(partido => partido.partidoActivo === false);
    }
  }

  contarPartidosPorResultado(resultado: 'victoria' | 'empate' | 'derrota'): number {
    return this.partidos.filter(partido => partido.resultado === resultado).length;
  }

  refrescarPartidos(event: any) {
    this.cargarPartidos();
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

  obtenerPartidosPorEstado(estado: string): PartidoView[] {
    if (estado === 'EN_CURSO') {
      return this.partidos.filter(partido => partido.partidoActivo === true);
    } else if (estado === 'COMPLETADO') {
      return this.partidos.filter(partido => partido.partidoActivo === false);
    }
    return [];
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

  continuarPartido(partidoId: number) {
    this.router.navigate(['/modo-partido', partidoId]);
  }

  verDetalles(partidoId: number) {
    this.router.navigate(['/estadisticas-partido', partidoId]);
  }

  verEstadisticasEquipo() {
    this.router.navigate(['/estadisticas-equipo']);
  }

  async confirmarEliminarPartido(partidoId: number) {
    const alert = await this.alertController.create({
      header: 'Confirmar eliminación',
      message: '¿Estás seguro de que quieres eliminar este partido? Se eliminarán todas sus estadísticas y no podrán recuperarse.',
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel',
          cssClass: 'secondary'
        },
        {
          text: 'Eliminar',
          cssClass: 'danger',
          handler: () => {
            this.eliminarPartido(partidoId);
          }
        }
      ]
    });

    await alert.present();
  }

  eliminarPartido(partidoId: number) {
    console.log('🗑️ Eliminando partido:', partidoId);
    
    this.partidoService.eliminarPartido(partidoId).subscribe({
      next: () => {
        console.log('✅ Partido eliminado correctamente');
        
        // Eliminar de la lista local
        this.partidos = this.partidos.filter(p => p.id !== partidoId);
        this.aplicarFiltro();
        
        // Mostrar mensaje de éxito
        this.mostrarMensajeExito();
      },
      error: (error) => {
        console.error('❌ Error al eliminar partido:', error);
        this.mostrarMensajeError();
      }
    });
  }

  async mostrarMensajeExito() {
    const alert = await this.alertController.create({
      header: 'Éxito',
      message: 'El partido y sus estadísticas han sido eliminados correctamente.',
      buttons: ['OK']
    });
    await alert.present();
  }

  async mostrarMensajeError() {
    const alert = await this.alertController.create({
      header: 'Error',
      message: 'No se pudo eliminar el partido. Por favor, inténtalo de nuevo.',
      buttons: ['OK']
    });
    await alert.present();

  }

  cerrarSesion() {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}

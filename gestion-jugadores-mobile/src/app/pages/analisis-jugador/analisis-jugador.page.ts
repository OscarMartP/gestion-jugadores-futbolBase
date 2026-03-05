import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { 
  IonHeader, IonToolbar, IonTitle, IonButtons, IonBackButton, IonContent,
  IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent,
  IonSpinner, IonIcon, IonButton, IonBadge, IonItem, IonLabel, IonList,
  IonRefresher, IonRefresherContent,
  LoadingController, AlertController, ModalController
} from '@ionic/angular/standalone';
import { AiAnalysisService, AnalisisJugador } from '../../core/services/ai-analysis.service';
import { InformeIaModalComponent } from '../jugadores/informe-ia-modal.component';

@Component({
  selector: 'app-analisis-jugador',
  templateUrl: './analisis-jugador.page.html',
  styleUrls: ['./analisis-jugador.page.scss'],
  standalone: true,
  imports: [
    CommonModule, FormsModule,
    IonHeader, IonToolbar, IonTitle, IonButtons, IonBackButton, IonContent,
    IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent,
    IonRefresher, IonRefresherContent,
    IonSpinner, IonIcon, IonButton, IonBadge, IonItem, IonLabel, IonList
  ]
})
export class AnalisisJugadorPage implements OnInit {
  jugadorId!: number;
  analisis: AnalisisJugador[] = [];
  cargando = true;

  constructor(
    private route: ActivatedRoute,
    private aiService: AiAnalysisService,
    private loadingCtrl: LoadingController,
    private alertCtrl: AlertController,
    private modalCtrl: ModalController
  ) {}

  ngOnInit() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.jugadorId = parseInt(id, 10);
      this.cargarAnalisis();
    }
  }

  cargarAnalisis() {
    this.cargando = true;
    this.aiService.obtenerAnalisisJugador(this.jugadorId).subscribe({
      next: (data) => {
        this.analisis = data;
        this.cargando = false;
        console.log('📚 Análisis cargados:', data);
      },
      error: (err) => {
        console.error('❌ Error cargando análisis:', err);
        this.cargando = false;
        this.mostrarError('No se pudieron cargar los análisis.');
      }
    });
  }

  async verAnalisis(analisis: AnalisisJugador) {
    const modal = await this.modalCtrl.create({
      component: InformeIaModalComponent,
      componentProps: {
        informe: {
          jugadorId: analisis.jugadorId,
          nombreCompleto: analisis.nombreCompleto,
          posicion: analisis.posicion,
          temporada: analisis.temporada,
          analisisTecnico: analisis.analisisTecnico
        }
      },
      cssClass: 'informe-modal'
    });
    await modal.present();
  }

  formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: 'short',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  async mostrarError(mensaje: string) {
    const alert = await this.alertCtrl.create({
      header: 'Error',
      message: mensaje,
      buttons: ['OK']
    });
    await alert.present();
  }

  refrescarAnalisis(event: any) {
    this.cargarAnalisis();
    if (event) {
      event.target.complete();
    }
  }
}

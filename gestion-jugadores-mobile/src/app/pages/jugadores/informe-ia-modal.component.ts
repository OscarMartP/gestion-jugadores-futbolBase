import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { 
  IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon, 
  IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent,
  ModalController, IonSpinner
} from '@ionic/angular/standalone';
import { InformeJugador } from '../../core/services/ai-analysis.service';

/**
 * Modal para mostrar el informe de análisis con IA de un jugador.
 * Diseñado para visualizar el análisis técnico generado automáticamente.
 */
@Component({
  selector: 'app-informe-ia-modal',
  standalone: true,
  imports: [
    CommonModule,
    IonHeader, IonToolbar, IonTitle, IonButtons, IonButton, IonIcon,
    IonContent, IonCard, IonCardHeader, IonCardTitle, IonCardSubtitle, IonCardContent,
    IonSpinner
  ],
  template: `
    <ion-header>
      <ion-toolbar color="primary">
        <ion-title>
          <div style="display: flex; align-items: center; gap: 8px;">
            <ion-icon name="sparkles"></ion-icon>
            <span>Análisis con IA</span>
          </div>
        </ion-title>
        <ion-buttons slot="end">
          <ion-button (click)="cerrar()">
            <ion-icon name="close" slot="icon-only"></ion-icon>
          </ion-button>
        </ion-buttons>
      </ion-toolbar>
    </ion-header>

    <ion-content class="ion-padding">
      <!-- Información del jugador -->
      <ion-card class="jugador-info-card">
        <ion-card-header>
          <ion-card-title style="font-size: 1.3rem;">
            {{ informe.nombreCompleto }}
          </ion-card-title>
          <ion-card-subtitle style="font-size: 1rem; margin-top: 4px;">
            <span class="position-badge">{{ informe.posicion }}</span>
            <span class="temporada-badge">📅 {{ informe.temporada }}</span>
          </ion-card-subtitle>
        </ion-card-header>
      </ion-card>

      <!-- Análisis técnico -->
      <ion-card class="analisis-card">
        <ion-card-header>
          <ion-card-title style="display: flex; align-items: center; gap: 8px; font-size: 1.1rem;">
            <ion-icon name="analytics" color="tertiary"></ion-icon>
            Análisis Técnico
          </ion-card-title>
        </ion-card-header>
        <ion-card-content>
          <p class="analisis-texto">{{ informe.analisisTecnico }}</p>
          
          <!-- Badge de IA -->
          <div class="ia-badge">
            <ion-icon name="bulb" style="margin-right: 4px;"></ion-icon>
            Generado con Inteligencia Artificial
          </div>
        </ion-card-content>
      </ion-card>

      <!-- Botón de cerrar -->
      <ion-button expand="block" (click)="cerrar()" style="margin-top: 16px;">
        Cerrar
      </ion-button>
    </ion-content>
  `,
  styles: [`
    .jugador-info-card {
      background: linear-gradient(135deg, var(--ion-color-primary-tint) 0%, var(--ion-color-primary) 100%);
      color: white;
      
      ion-card-title {
        color: white;
      }
      
      ion-card-subtitle {
        color: rgba(255, 255, 255, 0.9);
      }
    }

    .position-badge {
      background: rgba(255, 255, 255, 0.3);
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 0.85rem;
      font-weight: 600;
      margin-right: 8px;
      display: inline-block;
    }

    .temporada-badge {
      background: rgba(255, 255, 255, 0.2);
      padding: 4px 12px;
      border-radius: 12px;
      font-size: 0.85rem;
      display: inline-block;
    }

    .analisis-card {
      border-left: 4px solid var(--ion-color-tertiary);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    .analisis-texto {
      white-space: pre-wrap;
      line-height: 1.7;
      font-size: 0.95rem;
      color: var(--ion-color-dark);
      text-align: justify;
    }

    .ia-badge {
      margin-top: 16px;
      padding: 8px 12px;
      background: linear-gradient(135deg, rgba(var(--ion-color-warning-rgb), 0.1), rgba(var(--ion-color-warning-rgb), 0.2));
      border-radius: 8px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 0.85rem;
      color: var(--ion-color-warning-shade);
      font-weight: 500;
    }

    @keyframes fadeIn {
      from {
        opacity: 0;
        transform: translateY(10px);
      }
      to {
        opacity: 1;
        transform: translateY(0);
      }
    }

    .analisis-card {
      animation: fadeIn 0.5s ease-out;
    }
  `]
})
export class InformeIaModalComponent {
  @Input() informe!: InformeJugador;

  constructor(private modalCtrl: ModalController) {}

  cerrar() {
    this.modalCtrl.dismiss();
  }
}

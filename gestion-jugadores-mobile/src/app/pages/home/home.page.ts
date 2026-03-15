import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { IonContent, IonSpinner } from '@ionic/angular/standalone';
import { EquipoService } from '../../core/services/equipo.service';

@Component({
  selector: 'app-home',
  template: `
    <ion-content class="ion-padding ion-text-center">
      <div style="margin-top: 50%; transform: translateY(-50%);">
        <ion-spinner name="crescent"></ion-spinner>
        <p style="margin-top: 20px; color: #666;">Cargando...</p>
      </div>
    </ion-content>
  `,
  standalone: true,
  imports: [IonContent, IonSpinner, CommonModule]
})
export class HomePage implements OnInit {

  constructor(
    private router: Router,
    private equipoService: EquipoService
  ) {}

  ngOnInit() {
    this.verificarEquipos();
  }

  verificarEquipos() {
    const token = localStorage.getItem('token');
    
    if (!token) {
      // Si no hay token, ir a login
      this.router.navigate(['/login']);
      return;
    }

    // Usar el servicio en lugar de llamada HTTP directa
    this.equipoService.obtenerEquiposMe().subscribe({
      next: (equipos) => {
        if (equipos && equipos.length > 0) {
          // Tiene equipos, ir a jugadores
          this.router.navigate(['/tabs/jugadores']);
        } else {
          // No tiene equipos, ir a crear equipo
          console.log('⚠️ Usuario sin equipos, redirigiendo a crear equipo');
          this.router.navigate(['/equipo-form']);
        }
      },
      error: (error) => {
        console.error('Error al verificar equipos:', error);
        // En caso de error, ir a jugadores por defecto
        this.router.navigate(['/tabs/jugadores']);
      }
    });
  }
}

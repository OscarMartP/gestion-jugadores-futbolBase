import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonCard, 
  IonCardContent, IonCardHeader, IonCardTitle, IonIcon,
  IonRefresher, IonRefresherContent
} from '@ionic/angular/standalone';

interface EstadisticasData {
  totalJugadores: number;
  totalEquipos: number;
  totalPartidos: number;
  partidosGanados: number;
  partidosEmpatados: number;
  partidosPerdidos: number;
  jugadoresPorPosicion: { posicion: string; cantidad: number }[];
  rendimientoPorEquipo: {
    nombre: string;
    partidos: number;
    victorias: number;
    empates: number;
    derrotas: number;
    puntos: number;
  }[];
  ultimosPartidos: {
    equipoLocal: string;
    equipoVisitante: string;
    golesLocal: number;
    golesVisitante: number;
    fecha: Date;
  }[];
}

@Component({
  selector: 'app-estadisticas',
  templateUrl: './estadisticas.page.html',
  styleUrls: ['./estadisticas.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonCard, 
    IonCardContent, IonCardHeader, IonCardTitle, IonIcon,
    IonRefresher, IonRefresherContent,
    CommonModule, FormsModule
  ]
})
export class EstadisticasPage implements OnInit {

  estadisticas: EstadisticasData = {
    totalJugadores: 0,
    totalEquipos: 0,
    totalPartidos: 0,
    partidosGanados: 0,
    partidosEmpatados: 0,
    partidosPerdidos: 0,
    jugadoresPorPosicion: [],
    rendimientoPorEquipo: [],
    ultimosPartidos: []
  };

  constructor() { }

  ngOnInit() {
    this.cargarEstadisticas();
  }

  cargarEstadisticas() {
    // Datos de ejemplo
    this.estadisticas = {
      totalJugadores: 23,
      totalEquipos: 4,
      totalPartidos: 12,
      partidosGanados: 7,
      partidosEmpatados: 3,
      partidosPerdidos: 2,
      jugadoresPorPosicion: [
        { posicion: 'Portero', cantidad: 3 },
        { posicion: 'Defensa', cantidad: 8 },
        { posicion: 'Centrocampista', cantidad: 7 },
        { posicion: 'Delantero', cantidad: 5 }
      ],
      rendimientoPorEquipo: [
        {
          nombre: 'Real Madrid CF',
          partidos: 6,
          victorias: 4,
          empates: 1,
          derrotas: 1,
          puntos: 13
        },
        {
          nombre: 'FC Barcelona',
          partidos: 4,
          victorias: 2,
          empates: 1,
          derrotas: 1,
          puntos: 7
        },
        {
          nombre: 'Los Amigos FC',
          partidos: 5,
          victorias: 3,
          empates: 1,
          derrotas: 1,
          puntos: 10
        },
        {
          nombre: 'Barrio Unidos',
          partidos: 3,
          victorias: 1,
          empates: 0,
          derrotas: 2,
          puntos: 3
        }
      ],
      ultimosPartidos: [
        {
          equipoLocal: 'Real Madrid CF',
          equipoVisitante: 'FC Barcelona',
          golesLocal: 2,
          golesVisitante: 1,
          fecha: new Date('2026-01-15T18:00:00')
        },
        {
          equipoLocal: 'Los Amigos FC',
          equipoVisitante: 'Barrio Unidos',
          golesLocal: 3,
          golesVisitante: 0,
          fecha: new Date('2026-01-12T19:30:00')
        },
        {
          equipoLocal: 'FC Barcelona',
          equipoVisitante: 'Los Amigos FC',
          golesLocal: 1,
          golesVisitante: 1,
          fecha: new Date('2026-01-10T17:00:00')
        },
        {
          equipoLocal: 'Real Madrid CF',
          equipoVisitante: 'Barrio Unidos',
          golesLocal: 4,
          golesVisitante: 0,
          fecha: new Date('2026-01-08T16:30:00')
        },
        {
          equipoLocal: 'Los Amigos FC',
          equipoVisitante: 'Real Madrid CF',
          golesLocal: 0,
          golesVisitante: 2,
          fecha: new Date('2026-01-05T18:00:00')
        }
      ]
    };
  }

  // Métodos para gráficos SVG
  getStrokeDashArray(type: string): string {
    const total = this.estadisticas.totalPartidos;
    if (total === 0) return '0 100';
    
    let value = 0;
    switch (type) {
      case 'ganados':
        value = this.estadisticas.partidosGanados;
        break;
      case 'empates':
        value = this.estadisticas.partidosEmpatados;
        break;
      case 'perdidos':
        value = this.estadisticas.partidosPerdidos;
        break;
    }
    
    const percentage = (value / total) * 100;
    const circumference = 2 * Math.PI * 15.91549430918954;
    const strokeLength = (percentage / 100) * circumference;
    
    return `${strokeLength} ${circumference}`;
  }

  getStrokeDashOffset(type: string): number {
    const total = this.estadisticas.totalPartidos;
    if (total === 0) return 25;
    
    const circumference = 2 * Math.PI * 15.91549430918954;
    let previousPercentage = 0;
    
    if (type === 'empates') {
      previousPercentage = (this.estadisticas.partidosGanados / total) * 100;
    } else if (type === 'perdidos') {
      previousPercentage = ((this.estadisticas.partidosGanados + this.estadisticas.partidosEmpatados) / total) * 100;
    }
    
    const previousLength = (previousPercentage / 100) * circumference;
    return 25 - previousLength;
  }

  // Métodos para gráficos de barras
  getBarWidth(cantidad: number): number {
    const max = Math.max(...this.estadisticas.jugadoresPorPosicion.map(p => p.cantidad));
    return max > 0 ? (cantidad / max) * 100 : 0;
  }

  getColorPorPosicion(posicion: string): string {
    const colores: { [key: string]: string } = {
      'Portero': 'var(--ion-color-warning)',
      'Defensa': 'var(--ion-color-primary)',
      'Centrocampista': 'var(--ion-color-tertiary)',
      'Delantero': 'var(--ion-color-danger)'
    };
    return colores[posicion] || 'var(--ion-color-medium)';
  }

  // Métodos para rendimiento de equipos
  getWinPercentage(equipo: any): number {
    if (equipo.partidos === 0) return 0;
    return (equipo.victorias / equipo.partidos) * 100;
  }

  getTeamColor(equipo: any): string {
    const percentage = this.getWinPercentage(equipo);
    if (percentage >= 70) return 'var(--ion-color-success)';
    if (percentage >= 50) return 'var(--ion-color-warning)';
    return 'var(--ion-color-danger)';
  }

  // Métodos para últimos partidos
  formatearFecha(fecha: Date): string {
    return fecha.toLocaleDateString('es-ES', {
      day: 'numeric',
      month: 'short'
    });
  }

  getResultClass(partido: any): string {
    if (partido.golesLocal > partido.golesVisitante) return 'win';
    if (partido.golesLocal < partido.golesVisitante) return 'loss';
    return 'draw';
  }

  getResultText(partido: any): string {
    if (partido.golesLocal > partido.golesVisitante) return 'Victoria';
    if (partido.golesLocal < partido.golesVisitante) return 'Derrota';
    return 'Empate';
  }

  refrescarEstadisticas(event: any) {
    this.cargarEstadisticas();
    setTimeout(() => {
      event.target.complete();
    }, 1000);
  }

}

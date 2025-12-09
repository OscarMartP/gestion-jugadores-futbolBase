import { Component, OnInit } from '@angular/core';
import { PartidoService } from '../partido.service';
import { EquipoService } from '../equipo.service';

@Component({
  selector: 'app-historial-partidos',
  templateUrl: './historial-partidos.component.html',
  styleUrls: ['./historial-partidos.component.css']
})
export class HistorialPartidosComponent implements OnInit {

  partidos: any[] = [];
  equipos: any[] = [];
  equipoSeleccionado: any;
  cargando: boolean = false;
  mensaje: string = '';
  tipoMensaje: 'success' | 'error' | 'info' = 'info';

  constructor(
    private partidoService: PartidoService,
    private equipoService: EquipoService
  ) { }

  ngOnInit(): void {
    this.cargarEquipos();
  }

  cargarEquipos(): void {
    this.cargando = true;
    this.equipoService.obtenerEquiposMe().subscribe(
      (equipos: any[]) => {
        this.equipos = equipos;
        if (this.equipos.length > 0) {
          this.equipoSeleccionado = this.equipos[0];
          this.cargarHistorialPartidos();
        } else {
          this.mostrarMensaje('No tienes equipos registrados', 'info');
          this.cargando = false;
        }
      },
      (error) => {
        console.error('Error al cargar equipos:', error);
        this.mostrarMensaje('Error al cargar equipos', 'error');
        this.cargando = false;
      }
    );
  }

  onEquipoSeleccionado(equipo: any): void {
    this.equipoSeleccionado = equipo;
    this.cargarHistorialPartidos();
  }

  cargarHistorialPartidos(): void {
    if (!this.equipoSeleccionado) return;

    this.cargando = true;
    this.partidoService.obtenerPartidosPorEquipo(this.equipoSeleccionado.id).subscribe(
      (partidos: any[]) => {
        // Mostrar todos los partidos (historial completo)
        this.partidos = partidos.sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime());
        
        if (this.partidos.length > 0) {
          this.mostrarMensaje(`${this.partidos.length} partido(s) encontrado(s)`, 'info');
        } else {
          this.mostrarMensaje('No hay partidos registrados para este equipo', 'info');
        }
        this.cargando = false;
      },
      (error) => {
        console.error('Error al cargar historial:', error);
        this.mostrarMensaje('Error al cargar historial de partidos', 'error');
        this.cargando = false;
      }
    );
  }

  eliminarPartido(partido: any): void {
    if (confirm(`¿Deseas eliminar el partido del ${this.formatearFecha(partido.fecha)}?`)) {
      this.cargando = true;
      this.partidoService.eliminarPartido(partido.id).subscribe(
        () => {
          this.mostrarMensaje('Partido eliminado correctamente', 'success');
          this.cargarHistorialPartidos();
        },
        (error) => {
          console.error('Error al eliminar partido:', error);
          this.mostrarMensaje('Error al eliminar partido', 'error');
          this.cargando = false;
        }
      );
    }
  }

  private formatearFecha(fecha: string): string {
    const date = new Date(fecha);
    return date.toLocaleDateString('es-ES', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  private mostrarMensaje(msg: string, tipo: 'success' | 'error' | 'info'): void {
    this.mensaje = msg;
    this.tipoMensaje = tipo;
    setTimeout(() => this.mensaje = '', 4000);
  }
}

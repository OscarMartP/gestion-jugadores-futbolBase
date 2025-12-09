import { Component, OnInit } from '@angular/core';
import { PartidoService } from '../../partido.service';
import { EquipoService } from '../../equipo.service';
import { UserService } from '../../login/services/user.service';

@Component({
  selector: 'app-gestionar-partidos',
  templateUrl: './gestionar-partidos.component.html',
  styleUrls: ['./gestionar-partidos.component.css']
})
export class GestionarPartidosComponent implements OnInit {

  partidos: any[] = [];
  equipos: any[] = [];
  equipoSeleccionado: any;
  cargando: boolean = false;
  mensaje: string = '';
  tipoMensaje: 'success' | 'error' | 'info' = 'info';
  usuarioId: number;

  constructor(
    private partidoService: PartidoService,
    private equipoService: EquipoService,
    private userService: UserService
  ) { }

  ngOnInit(): void {
    // Cargar equipos del usuario autenticado usando endpoint /equipos/me
    this.cargarEquipos();
  }

  cargarEquipos(): void {
    // Intentar obtener equipos del usuario autenticado
    this.equipoService.obtenerEquiposMe().subscribe(
      (equipos: any[]) => {
        this.equipos = equipos;
        if (this.equipos.length > 0) {
          this.equipoSeleccionado = this.equipos[0];
          this.cargarPartidos();
        }
      },
      (error) => {
        console.error('Error al cargar equipos (/equipos/me):', error);
        // Fallback: intentar cargar por userId si existe en el token
        this.userService.getIdUsuario().subscribe((id: number | null) => {
          if (id) {
            this.equipoService.obtenerEquiposPorUsuario(id).subscribe(
              (equipos2: any[]) => {
                this.equipos = equipos2;
                if (this.equipos.length > 0) {
                  this.equipoSeleccionado = this.equipos[0];
                  this.cargarPartidos();
                }
              }, (err2) => {
                console.error('Fallback: error al cargar equipos por id:', err2);
                this.mostrarMensaje('Error al cargar equipos', 'error');
              }
            );
          } else {
            this.mostrarMensaje('No se pudo identificar el usuario para cargar equipos', 'error');
          }
        }, (errId) => {
          console.error('Error al obtener id usuario fallback:', errId);
          this.mostrarMensaje('Error al cargar equipos', 'error');
        });
      }
    );
  }

  onEquipoSeleccionado(equipo: any): void {
    this.equipoSeleccionado = equipo;
    this.cargarPartidos();
  }

  cargarPartidos(): void {
    if (!this.equipoSeleccionado) return;

    this.cargando = true;
    this.partidoService.obtenerPartidosPorEquipo(this.equipoSeleccionado.id).subscribe(
      (partidos: any[]) => {
        this.partidos = partidos.sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime());
        this.cargando = false;
      },
      (error) => {
        console.error('Error al cargar partidos:', error);
        this.mostrarMensaje('Error al cargar partidos', 'error');
        this.cargando = false;
      }
    );
  }

  activarPartido(partido: any): void {
    if (partido.partidoActivo) {
      this.mostrarMensaje('Este partido ya está activo', 'info');
      return;
    }

    this.cargando = true;
    this.partidoService.activarPartido(partido.id).subscribe(
      (response: any) => {
        // Desactivar otros partidos del mismo equipo
        this.partidos.forEach(p => {
          if (p.id !== partido.id && p.equipoId === this.equipoSeleccionado.id && p.partidoActivo) {
            this.desactivarPartidoSilencioso(p.id);
          }
        });
        
        // Actualizar el partido localmente
        partido.partidoActivo = true;
        this.mostrarMensaje(`Partido del ${this.formatearFecha(partido.fecha)} activado`, 'success');
        this.cargando = false;
        
        // Recargar lista después de 1 segundo
        setTimeout(() => this.cargarPartidos(), 1000);
      },
      (error) => {
        console.error('Error al activar partido:', error);
        this.mostrarMensaje('Error al activar partido', 'error');
        this.cargando = false;
      }
    );
  }

  desactivarPartido(partido: any): void {
    if (!partido.partidoActivo) {
      this.mostrarMensaje('Este partido ya está inactivo', 'info');
      return;
    }

    this.cargando = true;
    this.partidoService.desactivarPartido(partido.id).subscribe(
      (response: any) => {
        partido.partidoActivo = false;
        this.mostrarMensaje(`Partido del ${this.formatearFecha(partido.fecha)} desactivado`, 'success');
        this.cargando = false;
        setTimeout(() => this.cargarPartidos(), 1000);
      },
      (error) => {
        console.error('Error al desactivar partido:', error);
        this.mostrarMensaje('Error al desactivar partido', 'error');
        this.cargando = false;
      }
    );
  }

  private desactivarPartidoSilencioso(partidoId: number): void {
    this.partidoService.desactivarPartido(partidoId).subscribe(
      () => {
        // Silencioso, sin mensaje
      },
      (error) => {
        console.error('Error al desactivar partido anterior:', error);
      }
    );
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

  eliminarPartido(partido: any): void {
    if (confirm(`¿Deseas eliminar el partido del ${this.formatearFecha(partido.fecha)}?`)) {
      this.cargando = true;
      this.partidoService.eliminarPartido(partido.id).subscribe(
        () => {
          this.mostrarMensaje('Partido eliminado correctamente', 'success');
          this.cargarPartidos();
        },
        (error) => {
          console.error('Error al eliminar partido:', error);
          this.mostrarMensaje('Error al eliminar partido', 'error');
          this.cargando = false;
        }
      );
    }
  }
}
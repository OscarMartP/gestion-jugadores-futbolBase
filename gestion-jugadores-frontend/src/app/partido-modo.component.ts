import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PartidoService } from './partido.service';
import { EquipoService } from './equipo.service';
import { JugadorService } from './jugador.service';
import { EventoJugadorService } from './evento-jugador.service';

// Logica Iniciar Partido - Fase 1: Seleccionar | Fase 2: Modo Juego
@Component({
  selector: 'app-partido-modo',
  templateUrl: './partido-modo.component.html',
  styleUrls: ['./partido-modo.component.css']
})
export class PartidoModoComponent implements OnInit, OnDestroy {

  // ===== FASE 1: Seleccionar Partido =====
  equipos: any[] = [];
  equipoSeleccionado: any;
  partidos: any[] = [];
  partidoSeleccionado: any;
  cargando: boolean = false;
  mensaje: string = '';
  tipoMensaje: 'success' | 'error' | 'info' = 'info';

  // ===== FASE 2: Modo Juego =====
  enModoJuego: boolean = false;
  partidoActivo: any;
  duracionPartido: number = 90;
  tiempoRestante: number = 0;
  jugadores: any[] = [];
  timerActivo: boolean = false;
  intervalId: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private partidoService: PartidoService,
    private equipoService: EquipoService,
    private jugadorService: JugadorService,
    private eventoService: EventoJugadorService
  ) {}

  ngOnInit(): void {
    this.cargarEquipos();
  }

  // ===== FASE 1: Seleccionar Partido =====

  cargarEquipos(): void {
    this.cargando = true;
    this.equipoService.obtenerEquiposMe().subscribe(
      (equipos: any[]) => {
        this.equipos = equipos;
        if (this.equipos.length > 0) {
          this.equipoSeleccionado = this.equipos[0];
          this.cargarPartidosDelEquipo();
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
    this.cargarPartidosDelEquipo();
  }

  cargarPartidosDelEquipo(): void {
    if (!this.equipoSeleccionado) return;

    this.cargando = true;
    this.partidoService.obtenerPartidosPorEquipo(this.equipoSeleccionado.id).subscribe(
      (partidos: any[]) => {
        // Filtrar solo partidos inactivos (disponibles para jugar)
        this.partidos = partidos
          .filter(p => !p.partidoActivo)
          .sort((a, b) => new Date(b.fecha).getTime() - new Date(a.fecha).getTime());

        if (this.partidos.length > 0) {
          this.partidoSeleccionado = this.partidos[0];
          this.mostrarMensaje(`${this.partidos.length} partido(s) disponible(s)`, 'info');
        } else {
          this.mostrarMensaje('No hay partidos disponibles para este equipo', 'info');
        }
        this.cargando = false;
      },
      (error) => {
        console.error('Error al cargar partidos:', error);
        this.mostrarMensaje('Error al cargar partidos', 'error');
        this.cargando = false;
      }
    );
  }

  onPartidoSeleccionado(partido: any): void {
    this.partidoSeleccionado = partido;
  }

  iniciarPartido(): void {
    if (!this.partidoSeleccionado) {
      this.mostrarMensaje('Selecciona un partido', 'error');
      return;
    }

    this.cargando = true;
    this.partidoService.activarPartido(this.partidoSeleccionado.id).subscribe(
      (response: any) => {
        this.partidoActivo = response;
        this.duracionPartido = this.equipoSeleccionado.duracionPartido || 90;
        this.tiempoRestante = 0;
        
        // Cargar jugadores del equipo
        this.cargarJugadores();
        
        this.enModoJuego = true;
        this.mostrarMensaje('¡Partido iniciado! Listo para registrar eventos', 'success');
        this.cargando = false;
        console.log('Partido activo:', this.partidoActivo);
      },
      (error) => {
        console.error('Error al iniciar partido:', error);
        this.mostrarMensaje('Error al iniciar el partido', 'error');
        this.cargando = false;
      }
    );
  }

  // ===== FASE 2: Modo Juego =====

  cargarJugadores(): void {
    this.jugadorService.obtenerJugadoresPorEquipoId(this.equipoSeleccionado.id).subscribe({
      next: (jugadores) => {
        this.jugadores = jugadores;
        console.log('✅ Jugadores cargados:', jugadores);
      },
      error: (err) => {
        console.error('❌ Error cargando jugadores:', err);
        this.mostrarMensaje('Error al cargar jugadores', 'error');
      }
    });
  }

  toggleTimer(): void {
    if (this.timerActivo) {
      clearInterval(this.intervalId);
    } else {
      this.intervalId = setInterval(() => {
        if (this.tiempoRestante < this.duracionPartido * 60) {
          this.tiempoRestante++;
        } else {
          clearInterval(this.intervalId);
          this.timerActivo = false;
        }
      }, 1000);
    }
    this.timerActivo = !this.timerActivo;
  }

  formatearTiempo(): string {
    const minutos = Math.floor(this.tiempoRestante / 60);
    const segundos = this.tiempoRestante % 60;
    return `${minutos}:${segundos < 10 ? '0' + segundos : segundos}`;
  }

  registrarEvento(jugadorId: number, tipoEvento: string): void {
    const minuto = Math.floor(this.tiempoRestante / 60);

    const evento = {
      jugadorId: jugadorId,
      partidoId: this.partidoActivo.id,
      tipoEvento: tipoEvento,
      minuto: minuto
    };

    console.log('📤 Enviando evento:', evento);

    this.eventoService.registrar(evento).subscribe({
      next: () => {
        console.log('✅ Evento registrado');
        this.mostrarMensaje(`${tipoEvento} registrado`, 'success');
      },
      error: (err) => {
        console.error('❌ Error al registrar evento:', err);
        this.mostrarMensaje('Error al registrar evento', 'error');
      }
    });
  }

  finalizarPartido(): void {
    if (confirm('¿Deseas finalizar y guardar el partido?')) {
      this.cargando = true;
      this.partidoService.desactivarPartido(this.partidoActivo.id).subscribe(
        (response: any) => {
          this.timerActivo = false;
          clearInterval(this.intervalId);
          this.enModoJuego = false;
          this.cargando = false;
          this.mostrarMensaje('Partido finalizado y guardado', 'success');

          // Limpiar y recargar lista
          setTimeout(() => {
            this.partidoSeleccionado = null;
            this.cargarPartidosDelEquipo();
          }, 2000);
        },
        (error) => {
          console.error('Error al finalizar partido:', error);
          this.mostrarMensaje('Error al finalizar el partido', 'error');
          this.cargando = false;
        }
      );
    }
  }

  cancelarPartido(): void {
    if (confirm('¿Deseas cancelar sin guardar?')) {
      this.timerActivo = false;
      clearInterval(this.intervalId);
      this.enModoJuego = false;
      this.partidoActivo = null;
      this.cargarPartidosDelEquipo();
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

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }
}
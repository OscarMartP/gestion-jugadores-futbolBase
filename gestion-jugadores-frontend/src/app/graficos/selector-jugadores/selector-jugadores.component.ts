import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PartidoService } from '../../partido.service';
import { JugadorService } from '../../jugador.service';
import { EventoJugadorService } from '../../evento-jugador.service';


@Component({
  selector: 'app-selector-jugadores',
  templateUrl: './selector-jugadores.component.html',
})
export class SelectorJugadoresComponent implements OnInit, OnDestroy {
  equipoId: number = 0;
  partidoId: number = 0;
  duracionPartido: number = 90;
  tiempoTranscurrido: number = 0;
  jugadores: any[] = [];

  timerActivo: boolean = false;
  intervalId: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private partidoService: PartidoService,
    private jugadorService: JugadorService,
    private eventoService: EventoJugadorService
  ) {}

  ngOnInit(): void {
    const equipoIdParam = this.route.snapshot.paramMap.get('equipoId');
    if (!equipoIdParam) {
      alert('❌ No se recibió el ID del equipo en la URL');
      return;
    }

    this.equipoId = +equipoIdParam;
    console.log("➡️ ID del equipo desde la ruta:", this.equipoId);

    if (!this.equipoId || this.equipoId <= 0) {
      alert('❌ ID de equipo inválido');
      return;
    }

    this.jugadorService.obtenerJugadoresPorEquipoId(this.equipoId).subscribe({
      next: (jugadores) => {
        console.log("✅ Jugadores desde servicio:", jugadores);
        this.jugadores = jugadores;
      },
      error: (err) => {
        console.error("❌ Error cargando jugadores:", err);
      }
    });

    this.partidoService.obtenerPartidosPorEquipo(this.equipoId).subscribe({
      next: (partidos) => {
        if (partidos.length > 0) {
          const ultimoPartido = partidos[partidos.length - 1];
          this.partidoId = ultimoPartido.id;
          this.duracionPartido = ultimoPartido.duracion;
        }
      },
      error: (err) => {
        console.error("❌ Error cargando partidos:", err);
      }
    });
  }

  toggleTimer(): void {
    if (this.timerActivo) {
      clearInterval(this.intervalId);
    } else {
      this.intervalId = setInterval(() => {
        if (this.tiempoTranscurrido < this.duracionPartido * 60) {
          this.tiempoTranscurrido++;
        } else {
          clearInterval(this.intervalId);
          this.timerActivo = false;
        }
      }, 1000);
    }
    this.timerActivo = !this.timerActivo;
  }

  registrarEvento(jugadorId: number, tipoEvento: string): void {
    const minuto = Math.floor(this.tiempoTranscurrido / 60);
    const evento = {
      jugadorId: jugadorId,
      partidoId: this.partidoId,
      tipoEvento: tipoEvento,
      minuto: minuto
    };
    console.log("📤 Enviando evento:", evento);

    this.eventoService.registrar(evento).subscribe({
      next: () => console.log("✅ Evento registrado"),
      error: (err) => console.error("❌ Error al registrar evento:", err)
    });
  }

  get tiempoFormateado(): string {
    const min = Math.floor(this.tiempoTranscurrido / 60);
    const sec = this.tiempoTranscurrido % 60;
    return `${min}:${sec.toString().padStart(2, '0')}`;
  }

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }
}

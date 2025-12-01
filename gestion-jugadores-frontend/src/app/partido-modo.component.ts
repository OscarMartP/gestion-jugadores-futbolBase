import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { PartidoService } from './partido.service';
import { JugadorService } from './jugador.service';
import { EventoJugadorService } from './evento-jugador.service';
//Logica Modo Partido
@Component({
  selector: 'app-partido-modo',
  templateUrl: './partido-modo.component.html',
})
export class PartidoModoComponent implements OnInit, OnDestroy {
  equipoId: number = 0;
  partidoId: number = 0;
  duracionPartido: number = 90;
  tiempoRestante: number = 0;
  jugadores: any[] = [];

  timerActivo: boolean = false;
  intervalId: any;

  constructor(
    private route: ActivatedRoute,
    private partidoService: PartidoService,
    private jugadorService: JugadorService,
    private eventoService: EventoJugadorService
  ) {}

  ngOnInit(): void {
    const equipoIdParam = this.route.snapshot.paramMap.get('equipoId');
    if (!equipoIdParam) return;

    this.equipoId = +equipoIdParam;
    console.log("➡️ ID del equipo desde la ruta:", this.equipoId);

    this.jugadorService.obtenerJugadoresPorEquipoId(this.equipoId).subscribe({
      next: (jugadores) => {
        console.log("✅ Jugadores desde servicio:", jugadores);
        this.jugadores = jugadores;
      },
      error: (err) => {
        console.error("❌ Error cargando jugadores:", err);
      }
    });

    this.partidoService.obtenerPartidosPorEquipo(this.equipoId).subscribe(partidos => {
      if (partidos.length > 0) {
        const ultimoPartido = partidos[partidos.length - 1];
        this.partidoId = ultimoPartido.id;
        this.duracionPartido = ultimoPartido.duracion;
        this.tiempoRestante = 0; 
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

  ngOnDestroy(): void {
    if (this.intervalId) {
      clearInterval(this.intervalId);
    }
  }
}
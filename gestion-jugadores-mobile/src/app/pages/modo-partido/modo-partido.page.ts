import { Component, OnInit, OnDestroy } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import {
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon,
  IonList, IonItem, IonLabel, IonBadge, IonFab, IonFabButton,
  AlertController, IonCard, IonCardHeader, IonCardTitle, IonCardContent,
  IonChip, ActionSheetController
} from '@ionic/angular/standalone';
import { Partido, EventoJugador, TipoEvento } from '../../core/models/partido';
import { Jugador } from '../../core/models/jugador';
import { PartidoService } from '../../core/services/partido.service';
import { JugadorService } from '../../core/services/jugador.service';
import { EventoJugadorService } from '../../core/services/evento-jugador.service';
import { RefreshService } from '../../core/services/refresh.service';

interface JugadorConEventos extends Jugador {
  eventos: EventoJugador[];
  esTitular: boolean;
}

@Component({
  selector: 'app-modo-partido',
  templateUrl: './modo-partido.page.html',
  styleUrls: ['./modo-partido.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon,
    IonList, IonItem, IonLabel, IonBadge, IonFab, IonFabButton,
    IonCard, IonCardHeader, IonCardTitle, IonCardContent, IonChip,
    CommonModule, FormsModule
  ]
})
export class ModoPartidoPage implements OnInit, OnDestroy {

  partido: Partido | null = null;
  jugadoresTitulares: JugadorConEventos[] = [];
  jugadoresSuplentes: JugadorConEventos[] = [];
  
  // Timer
  tiempoTranscurrido = 0; // en segundos
  timerInterval: any = null;
  timerActivo = false;

  // Eventos
  golesEquipo = 0;
  golesRival = 0;

  constructor(
    private partidoService: PartidoService,
    private jugadorService: JugadorService,
    private eventoService: EventoJugadorService,
    private refreshService: RefreshService,
    private router: Router,
    private route: ActivatedRoute,
    private alertController: AlertController,
    private actionSheetController: ActionSheetController
  ) {}

  ngOnInit() {
    const partidoId = parseInt(this.route.snapshot.params['id']);
    this.cargarPartido(partidoId);
  }

  ngOnDestroy() {
    this.detenerTimer();
  }

  cargarPartido(partidoId: number) {
    this.partidoService.obtenerPartidoPorId(partidoId).subscribe({
      next: (partido) => {
        this.partido = partido;
        this.golesEquipo = partido.golesEquipo || 0;
        this.golesRival = partido.golesRival || 0;
        this.cargarJugadores();
      },
      error: (error) => {
        console.error('❌ Error al cargar partido:', error);
        this.mostrarError('Error al cargar el partido');
      }
    });
  }

  cargarJugadores() {
    if (!this.partido) return;

    this.jugadorService.obtenerJugadoresPorUsuario().subscribe({
      next: (jugadores) => {
        // Separar titulares y suplentes
        this.jugadoresTitulares = this.partido!.titulares.map(id => {
          const jugador = jugadores.find(j => j.id === id);
          return jugador ? { ...jugador, eventos: [], esTitular: true } as JugadorConEventos : null;
        }).filter(j => j !== null) as JugadorConEventos[];

        this.jugadoresSuplentes = this.partido!.suplentes.map(id => {
          const jugador = jugadores.find(j => j.id === id);
          return jugador ? { ...jugador, eventos: [], esTitular: false } as JugadorConEventos : null;
        }).filter(j => j !== null) as JugadorConEventos[];

        // Ordenar titulares por posición (de atrás hacia adelante)
        this.jugadoresTitulares.sort((a, b) => this.obtenerPrioridadPosicion(a) - this.obtenerPrioridadPosicion(b));

        console.log('✅ Jugadores cargados y ordenados:', {
          titulares: this.jugadoresTitulares.length,
          suplentes: this.jugadoresSuplentes.length
        });
      },
      error: (error) => {
        console.error('❌ Error al cargar jugadores:', error);
      }
    });
  }

  // TIMER
  iniciarTimer() {
    this.timerActivo = true;
    this.timerInterval = setInterval(() => {
      this.tiempoTranscurrido++;
      
      // Verificar si se acabó el tiempo
      if (this.partido && this.tiempoTranscurrido >= this.partido.duracion * 60) {
        this.detenerTimer();
        this.finalizarPartido();
      }
    }, 1000);
  }

  pausarTimer() {
    this.timerActivo = false;
    if (this.timerInterval) {
      clearInterval(this.timerInterval);
      this.timerInterval = null;
    }
  }

  detenerTimer() {
    this.pausarTimer();
  }

  get tiempoFormateado(): string {
    const minutos = Math.floor(this.tiempoTranscurrido / 60);
    const segundos = this.tiempoTranscurrido % 60;
    return `${minutos.toString().padStart(2, '0')}:${segundos.toString().padStart(2, '0')}`;
  }

  get minutosPartido(): number {
    return Math.floor(this.tiempoTranscurrido / 60);
  }

  // EVENTOS
  async mostrarMenuEventos(jugador: JugadorConEventos) {
    const botones = [
      {
        text: '⚽ Gol',
        handler: () => this.registrarEvento(jugador, TipoEvento.GOL)
      },
      {
        text: '🎯 Asistencia',
        handler: () => this.registrarEvento(jugador, TipoEvento.ASISTENCIA)
      },
      {
        text: '🔑 Pase Clave',
        handler: () => this.registrarEvento(jugador, TipoEvento.PASE_CLAVE)
      },
      {
        text: '🛡️ Robo',
        handler: () => this.registrarEvento(jugador, TipoEvento.ROBO)
      },
      {
        text: '❌ Pérdida',
        handler: () => this.registrarEvento(jugador, TipoEvento.PERDIDA)
      },
      {
        text: '🎯 Tiro a Puerta',
        handler: () => this.registrarEvento(jugador, TipoEvento.TIRO_PUERTA)
      },
      {
        text: '🟨 Tarjeta Amarilla',
        handler: () => this.registrarEvento(jugador, TipoEvento.TARJETA_AMARILLA)
      },
      {
        text: '🟥 Tarjeta Roja',
        handler: () => this.registrarEvento(jugador, TipoEvento.TARJETA_ROJA)
      }
    ];

    // Agregar parada solo para porteros
    if (jugador.posicion === 'POR') {
      botones.splice(4, 0, {
        text: '🧤 Parada',
        handler: () => this.registrarEvento(jugador, TipoEvento.PARADA)
      });
    }

    // Agregar cambio solo para titulares
    if (jugador.esTitular) {
      botones.push({
        text: '🔄 Cambio',
        handler: () => this.mostrarMenuCambio(jugador)
      });
    }

    botones.push({
      text: 'Cancelar',
      handler: () => {}
    });

    const actionSheet = await this.actionSheetController.create({
      header: `${jugador.nombre} ${jugador.apellido}`,
      buttons: botones
    });

    await actionSheet.present();
  }

  registrarEvento(jugador: JugadorConEventos, tipoEvento: TipoEvento) {
    if (!this.partido) return;

    const evento: EventoJugador = {
      jugadorId: jugador.id,
      partidoId: this.partido.id,
      tipoEvento: tipoEvento,
      minuto: this.minutosPartido
    };

    this.eventoService.registrarEvento(evento).subscribe({
      next: (eventoCreado) => {
        console.log('✅ Evento registrado:', eventoCreado);
        jugador.eventos.push(eventoCreado);
        
        // Actualizar goles
        if (tipoEvento === TipoEvento.GOL) {
          this.golesEquipo++;
        }
      },
      error: (error) => {
        console.error('❌ Error al registrar evento:', error);
        this.mostrarError('Error al registrar el evento');
      }
    });
  }

  async mostrarMenuCambio(jugadorSale: JugadorConEventos) {
    if (this.jugadoresSuplentes.length === 0) {
      const alert = await this.alertController.create({
        header: 'Sin suplentes',
        message: 'No hay suplentes disponibles para hacer cambios.',
        buttons: ['OK']
      });
      await alert.present();
      return;
    }

    const botones = this.jugadoresSuplentes.map(suplente => ({
      text: `${suplente.nombre} ${suplente.apellido} (${suplente.posicion})`,
      handler: () => this.realizarCambio(jugadorSale, suplente)
    }));

    botones.push({
      text: 'Cancelar',
      handler: () => {}
    });

    const actionSheet = await this.actionSheetController.create({
      header: '¿Quién entra?',
      buttons: botones
    });

    await actionSheet.present();
  }

  realizarCambio(jugadorSale: JugadorConEventos, jugadorEntra: JugadorConEventos) {
    if (!this.partido) return;

    const evento: EventoJugador = {
      jugadorId: jugadorSale.id,
      partidoId: this.partido.id,
      tipoEvento: TipoEvento.SUSTITUCION,
      minuto: this.minutosPartido,
      jugadorSaleId: jugadorSale.id,
      jugadorEntraId: jugadorEntra.id
    };

    this.eventoService.registrarEvento(evento).subscribe({
      next: (eventoCreado) => {
        console.log('✅ Cambio registrado:', eventoCreado);
        
        // Intercambiar jugadores
        jugadorSale.esTitular = false;
        jugadorEntra.esTitular = true;
        
        this.jugadoresTitulares = this.jugadoresTitulares.filter(j => j.id !== jugadorSale.id);
        this.jugadoresTitulares.push(jugadorEntra);
        
        // Reordenar titulares por posición después del cambio
        this.jugadoresTitulares.sort((a, b) => this.obtenerPrioridadPosicion(a) - this.obtenerPrioridadPosicion(b));
        
        this.jugadoresSuplentes = this.jugadoresSuplentes.filter(j => j.id !== jugadorEntra.id);
        this.jugadoresSuplentes.push(jugadorSale);

        this.mostrarMensaje('✅ Cambio realizado');
      },
      error: (error) => {
        console.error('❌ Error al realizar cambio:', error);
        this.mostrarError('Error al realizar el cambio');
      }
    });
  }

  async registrarGolRival() {
    const alert = await this.alertController.create({
      header: 'Gol del Rival',
      message: '¿Confirmas el gol del equipo rival?',
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Confirmar',
          handler: () => {
            if (!this.partido) return;

            const evento: EventoJugador = {
              jugadorId: this.jugadoresTitulares[0]?.id || 0, // Placeholder
              partidoId: this.partido.id,
              tipoEvento: TipoEvento.GOL_RIVAL,
              minuto: this.minutosPartido
            };

            this.eventoService.registrarEvento(evento).subscribe({
              next: () => {
                this.golesRival++;
                this.mostrarMensaje('Gol rival registrado');
              },
              error: (error) => {
                console.error('❌ Error:', error);
              }
            });
          }
        }
      ]
    });

    await alert.present();
  }

  async finalizarPartido() {
    // Verificar si el tiempo ya terminó
    const tiempoTerminado = this.partido && this.tiempoTranscurrido >= this.partido.duracion * 60;
    
    if (tiempoTerminado) {
      // Si el tiempo ya terminó, finalizar directamente
      await this.confirmarFinalizacion();
    } else {
      // Si todavía queda tiempo, pedir confirmación
      const minutoActual = Math.floor(this.tiempoTranscurrido / 60);
      const tiempoRestante = this.partido ? this.partido.duracion - minutoActual : 0;
      
      const alert = await this.alertController.create({
        header: '⚠️ Finalizar Partido',
        message: `¿Deseas finalizar el partido ahora?\n\nQuedan ${tiempoRestante} minutos. Se asumirá que no hay más eventos hasta el final del partido.\n\nResultado actual: ${this.golesEquipo} - ${this.golesRival}`,
        buttons: [
          {
            text: 'Cancelar',
            role: 'cancel'
          },
          {
            text: 'Finalizar Partido',
            handler: () => {
              this.confirmarFinalizacion();
            }
          }
        ]
      });

      await alert.present();
    }
  }

  async confirmarFinalizacion() {
    // Detener el timer si está activo
    this.detenerTimer();

    // Actualizar el partido con el resultado final antes de desactivarlo
    if (this.partido) {
      // Primero actualizar el partido con los goles
      const partidoActualizado = {
        ...this.partido,
        golesEquipo: this.golesEquipo,
        golesRival: this.golesRival
      };

      // Actualizar partido en el backend
      this.partidoService.desactivarPartido(this.partido.id).subscribe({
        next: () => {
          console.log('✅ Partido finalizado correctamente');
          // Notificar que los partidos y estadísticas deben refrescarse
          this.refreshService.refreshPartidos();
          this.refreshService.refreshEstadisticas();
        },
        error: (error) => {
          console.error('❌ Error al finalizar partido:', error);
        }
      });
    }

    // Mostrar resultado final
    const alertFinal = await this.alertController.create({
      header: '🏁 Partido Finalizado',
      message: `Resultado Final: ${this.golesEquipo} - ${this.golesRival}`,
      buttons: [
        {
          text: 'Volver a Partidos',
          handler: () => {
            this.router.navigate(['/tabs/partidos']);
          }
        }
      ],
      backdropDismiss: false
    });

    await alertFinal.present();
  }

  async cancelarPartido() {
    const alert = await this.alertController.create({
      header: '⚠️ Cancelar Partido',
      message: 'El partido se eliminará completamente y NO se contará en las estadísticas.\n\n¿Estás seguro?',
      buttons: [
        {
          text: 'No',
          role: 'cancel'
        },
        {
          text: 'Sí, Cancelar',
          role: 'destructive',
          handler: () => {
            this.confirmarCancelacion();
          }
        }
      ]
    });

    await alert.present();
  }

  async confirmarCancelacion() {
    if (!this.partido) return;

    // Detener el timer
    this.detenerTimer();

    // Eliminar el partido del backend
    this.partidoService.eliminarPartido(this.partido.id).subscribe({
      next: () => {
        console.log('✅ Partido cancelado y eliminado correctamente');
        // Notificar que los partidos deben refrescarse
        this.refreshService.refreshPartidos();
        
        // Mostrar mensaje de confirmación
        this.mostrarMensajeYSalir('Partido cancelado. No se ha guardado en estadísticas.');
      },
      error: (error) => {
        console.error('❌ Error al cancelar partido:', error);
        this.mostrarError('Error al cancelar el partido');
      }
    });
  }

  async mostrarMensajeYSalir(mensaje: string) {
    const alert = await this.alertController.create({
      header: '✅ Cancelado',
      message: mensaje,
      buttons: [
        {
          text: 'OK',
          handler: () => {
            this.router.navigate(['/tabs/partidos']);
          }
        }
      ],
      backdropDismiss: false
    });

    await alert.present();
  }

  async salir() {
    const alert = await this.alertController.create({
      header: '¿Salir del partido?',
      message: 'El partido se pausará y podrás continuarlo más tarde.',
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Salir',
          handler: () => {
            this.detenerTimer();
            this.router.navigate(['/tabs/partidos']);
          }
        }
      ]
    });

    await alert.present();
  }

  contarEventos(jugador: JugadorConEventos, tipoEvento: string): number {
    return jugador.eventos.filter(e => e.tipoEvento === tipoEvento).length;
  }

  async mostrarError(mensaje: string) {
    const alert = await this.alertController.create({
      header: '❌ Error',
      message: mensaje,
      buttons: ['OK']
    });
    await alert.present();
  }

  async mostrarMensaje(mensaje: string) {
    const alert = await this.alertController.create({
      message: mensaje,
      buttons: ['OK']
    });
    await alert.present();
    setTimeout(() => {
      alert.dismiss();
    }, 2000);
  }

  /**
   * Obtiene la prioridad de ordenamiento según la posición del jugador.
   * Orden: PORTERO → CENTRALES → LATERALES → MEDIOCENTROS → MCO → EXTREMOS → DELANTEROS
   */
  obtenerPrioridadPosicion(jugador: Jugador): number {
    if (!jugador || !jugador.posicion) {
      return 999; // Sin posición va al final
    }
    
    const posicion = jugador.posicion.toUpperCase().trim();
    
    // 1️⃣ PORTERO
    if (posicion === 'PORTERO' || posicion === 'ARQUERO' || posicion === 'GK' || posicion === 'GOALKEEPER' || posicion === 'POR') {
      return 1;
    }
    
    // 2️⃣ DEFENSAS: Primero centrales, luego laterales
    // Centrales
    if (posicion.includes('CENTRAL') || posicion === 'CEN' || posicion === 'DFC' || posicion === 'DEF' || 
        posicion === 'DCD' || posicion === 'DCI' || posicion === 'CD' || posicion === 'CI') {
      return 2.0;
    }
    // Laterales
    if (posicion.includes('LATERAL IZQUIERDO') || posicion === 'LI' || posicion === 'LAI') {
      return 2.1;
    }
    if (posicion.includes('LATERAL DERECHO') || posicion === 'LD' || posicion === 'LAD') {
      return 2.2;
    }
    if (posicion.includes('LATERAL') || posicion.includes('DEFENSA')) {
      return 2.3;
    }
    
    // 3️⃣ MEDIOCAMPISTAS
    if (posicion.includes('PIVOTE') || posicion === 'PV' || posicion === 'MCD') {
      return 3.0;
    }
    if (posicion.includes('MEDIOCENTRO') || posicion === 'MC' || posicion === 'MEDIO CENTRO') {
      return 3.1;
    }
    if (posicion.includes('MEDIO CENTRO OFENSIVO') || posicion === 'MCO') {
      return 3.2;
    }
    if (posicion.includes('MEDIA PUNTA') || posicion === 'MP' || posicion === 'CAM') {
      return 3.3;
    }
    
    // 4️⃣ DELANTEROS: Primero extremos, luego delantero centro
    if (posicion.includes('EXTREMO IZQUIERDO') || posicion.includes('EXTREMO IZQUIERDA') || 
        posicion === 'EI' || posicion === 'EXI' || posicion === 'EXIZ') {
      return 4.1;
    }
    if (posicion.includes('EXTREMO DERECHO') || posicion.includes('EXTREMO DERECHA') || 
        posicion === 'ED' || posicion === 'EXD') {
      return 4.2;
    }
    if (posicion.includes('EXTREMO') || posicion === 'EX') {
      return 4.15;
    }
    if (posicion.includes('DELANTERO') || posicion === 'DEL' || posicion === 'DC' || posicion === 'ST' || posicion === 'CF') {
      return 4.3;
    }
    
    // Posición no reconocida - va al final
    console.warn(`⚠️ Posición no reconocida: "${posicion}" para jugador ${jugador.nombre}`);
    return 999;
  }
}

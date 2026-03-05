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
  
  // Resultado del partido
  golesEquipo: number = 0;
  golesRival: number = 0;
  resultado: string = '';
  
  // Sustituciones
  suplentes: any[] = [];
  mostrarDialogoSustitucion: boolean = false;
  jugadorASalir: any = null;
  
  // Lista de eventos disponibles
  eventosDisponibles = [
    { tipo: 'gol', nombre: 'Gol', icono: '⚽', color: 'success' },
    { tipo: 'asistencia', nombre: 'Asistencia', icono: '🎯', color: 'info' },
    { tipo: 'tarjeta_amarilla', nombre: 'T. Amarilla', icono: '🟨', color: 'warning' },
    { tipo: 'tarjeta_roja', nombre: 'T. Roja', icono: '🟥', color: 'danger' },
    { tipo: 'pase_clave', nombre: 'Pase Clave', icono: '🔑', color: 'primary' },
    { tipo: 'robo', nombre: 'Robo', icono: '🛡️', color: 'secondary' },
    { tipo: 'tiro_a_puerta', nombre: 'Tiro a Puerta', icono: '🥅', color: 'info' }
  ];

  // Evento exclusivo para porteros
  eventoPortero = { tipo: 'parada', nombre: 'Parada', icono: '🧤', color: 'primary' };

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
        
        // Cargar jugadores del equipo (titulares y suplentes)
        this.cargarJugadores();
        this.cargarSuplentes();
        
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
        // Filtrar solo los jugadores que están en la lista de titulares
        if (this.partidoActivo?.titulares && this.partidoActivo.titulares.length > 0) {
          this.jugadores = jugadores.filter((j: any) => this.partidoActivo.titulares.includes(j.id));
          console.log('✅ Titulares cargados:', this.jugadores);
        } else {
          // Si no hay titulares definidos, cargar todos (compatibilidad con partidos antiguos)
          this.jugadores = jugadores;
          console.log('⚠️ No hay titulares definidos, mostrando todos los jugadores');
        }
        
        // Ordenar jugadores por posición (de atrás hacia adelante)
        this.jugadores.sort((a, b) => this.obtenerPrioridadPosicion(a) - this.obtenerPrioridadPosicion(b));
        
        // Debug: mostrar posiciones
        this.jugadores.forEach(j => {
          console.log(`Jugador: ${j.nombre} ${j.apellido}, Posición: "${j.posicion}", Prioridad: ${this.obtenerPrioridadPosicion(j)}`);
        });
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
        
        // Actualizar marcador automáticamente si es un gol
        if (tipoEvento.toLowerCase() === 'gol') {
          this.incrementarGolesEquipo();
          console.log('⚽ Marcador actualizado: ' + this.golesEquipo);
        }
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
      
      // Calcular resultado automáticamente
      if (this.golesEquipo > this.golesRival) {
        this.resultado = 'Victoria';
      } else if (this.golesEquipo < this.golesRival) {
        this.resultado = 'Derrota';
      } else {
        this.resultado = 'Empate';
      }
      
      // Actualizar partido con resultado
      const partidoActualizado = {
        ...this.partidoActivo,
        golesEquipo: this.golesEquipo,
        golesRival: this.golesRival,
        resultado: this.resultado
      };
      
      // Primero actualizar el partido con el resultado
      this.partidoService.actualizarPartido(this.partidoActivo.id, partidoActualizado).subscribe(
        () => {
          console.log('✅ Resultado guardado:', partidoActualizado);
          
          // Luego desactivar el partido
          this.partidoService.desactivarPartido(this.partidoActivo.id).subscribe(
            (response: any) => {
              this.timerActivo = false;
              clearInterval(this.intervalId);
              this.enModoJuego = false;
              this.cargando = false;
              this.mostrarMensaje(`Partido finalizado. Resultado: ${this.resultado} (${this.golesEquipo}-${this.golesRival})`, 'success');

              // Limpiar y recargar lista
              setTimeout(() => {
                this.partidoSeleccionado = null;
                this.golesEquipo = 0;
                this.golesRival = 0;
                this.resultado = '';
                this.cargarPartidosDelEquipo();
              }, 3000);
            },
            (error) => {
              console.error('Error al finalizar partido:', error);
              this.mostrarMensaje('Error al finalizar el partido', 'error');
              this.cargando = false;
            }
          );
        },
        (error) => {
          console.error('Error al guardar resultado:', error);
          this.mostrarMensaje('Error al guardar el resultado', 'error');
          this.cargando = false;
        }
      );
    }
  }

  decrementarGolesEquipo(): void {
    if (this.golesEquipo > 0) {
      this.golesEquipo--;
    }
  }

  incrementarGolesEquipo(): void {
    this.golesEquipo++;
  }

  decrementarGolesRival(): void {
    if (this.golesRival > 0) {
      this.golesRival--;
      // TODO: Eliminar el último evento de gol_rival si existe
      console.log('⚽ Gol rival decrementado:', this.golesRival);
    }
  }

  incrementarGolesRival(): void {
    this.golesRival++;
    // Crear evento de gol rival para que sea rastreable en estadísticas
    const eventoGolRival = {
      jugadorId: null, // Gol del rival, no tiene jugador de nuestro equipo
      partidoId: this.partidoActivo.id,
      tipoEvento: 'gol_rival',
      minuto: Math.floor(this.tiempoRestante / 60)
    };
    
    console.log('📤 Enviando evento de gol rival:', eventoGolRival);
    this.eventoService.registrarEvento(eventoGolRival).subscribe(
      (response) => {
        console.log('✅ Evento de gol rival registrado');
      },
      (error) => {
        console.error('❌ Error al registrar gol rival:', error);
      }
    );
  }

  obtenerResultado(): string {
    if (this.golesEquipo > this.golesRival) {
      return 'Victoria';
    } else if (this.golesEquipo < this.golesRival) {
      return 'Derrota';
    } else {
      return 'Empate';
    }
  }

  esPortero(jugador: any): boolean {
    if (!jugador || !jugador.posicion) {
      console.log('⚠️ Jugador sin posición:', jugador);
      return false;
    }
    const posicion = jugador.posicion.toUpperCase().trim();
    const esPortero = posicion === 'PORTERO' || posicion === 'ARQUERO' || posicion === 'GK' || posicion === 'GOALKEEPER' || posicion === 'POR';
    return esPortero;
  }

  /**
   * Obtiene la prioridad de ordenamiento según la posición del jugador.
   * Orden: PORTERO → CENTRALES → LATERALES → MEDIOCENTROS → MCO → EXTREMOS → DELANTEROS
   */
  obtenerPrioridadPosicion(jugador: any): number {
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

  cargarSuplentes(): void {
    this.jugadorService.obtenerJugadoresPorEquipoId(this.equipoSeleccionado.id).subscribe({
      next: (jugadores) => {
        // Filtrar solo los jugadores que están en la lista de suplentes
        if (this.partidoActivo?.suplentes && this.partidoActivo.suplentes.length > 0) {
          this.suplentes = jugadores.filter((j: any) => this.partidoActivo.suplentes.includes(j.id));
          console.log('✅ Suplentes cargados:', this.suplentes);
        }
      },
      error: (err) => {
        console.error('❌ Error cargando suplentes:', err);
      }
    });
  }

  abrirDialogoSustitucion(jugador: any): void {
    this.jugadorASalir = jugador;
    // No recargar suplentes - usar la lista local actualizada con las sustituciones
    this.mostrarDialogoSustitucion = true;
  }

  cerrarDialogoSustitucion(): void {
    this.mostrarDialogoSustitucion = false;
    this.jugadorASalir = null;
  }

  realizarSustitucion(jugadorEntra: any): void {
    const minuto = Math.floor(this.tiempoRestante / 60);
    
    // Crear evento de sustitución
    const evento = {
      jugadorId: jugadorEntra.id,
      partidoId: this.partidoActivo.id,
      tipoEvento: 'sustitucion',
      minuto: minuto,
      jugadorSaleId: this.jugadorASalir.id,
      jugadorEntraId: jugadorEntra.id
    };

    this.eventoService.registrarEvento(evento).subscribe({
      next: () => {
        // Actualizar listas: quitar el que sale de titulares y agregarlo a suplentes
        const indiceTitular = this.jugadores.findIndex(j => j.id === this.jugadorASalir.id);
        if (indiceTitular > -1) {
          this.jugadores.splice(indiceTitular, 1);
          this.suplentes.push(this.jugadorASalir);
        }

        // Quitar el que entra de suplentes y agregarlo a titulares
        const indiceSuplente = this.suplentes.findIndex(j => j.id === jugadorEntra.id);
        if (indiceSuplente > -1) {
          this.suplentes.splice(indiceSuplente, 1);
          this.jugadores.push(jugadorEntra);
          
          // Reordenar titulares por posición después del cambio
          this.jugadores.sort((a, b) => this.obtenerPrioridadPosicion(a) - this.obtenerPrioridadPosicion(b));
        }

        this.mostrarMensaje(`Cambio realizado: Sale ${this.jugadorASalir.nombre}, Entra ${jugadorEntra.nombre}`, 'success');
        this.cerrarDialogoSustitucion();
      },
      error: (err) => {
        console.error('Error al registrar sustitución:', err);
        this.mostrarMensaje('Error al registrar el cambio', 'error');
      }
    });
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
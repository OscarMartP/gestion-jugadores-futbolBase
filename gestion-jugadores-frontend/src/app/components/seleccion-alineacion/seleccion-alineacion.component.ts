import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PartidoService } from '../../partido.service';
import { JugadorService } from '../../jugador.service';
import { EquipoService } from '../../equipo.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Jugador } from '../../jugador';

@Component({
  selector: 'app-seleccion-alineacion',
  templateUrl: './seleccion-alineacion.component.html',
  styleUrls: ['./seleccion-alineacion.component.css']
})
export class SeleccionAlineacionComponent implements OnInit {
  partidoId: number;
  equipoId: number;
  tipoFutbol: string = 'FUTBOL_11';
  numeroTitulares: number = 11;
  
  jugadoresDisponibles: Jugador[] = [];
  titulares: Jugador[] = [];
  suplentes: Jugador[] = [];
  
  cargando: boolean = true;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private partidoService: PartidoService,
    private jugadorService: JugadorService,
    private equipoService: EquipoService,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit(): void {
    this.partidoId = +this.route.snapshot.paramMap.get('partidoId')!;
    this.equipoId = +this.route.snapshot.paramMap.get('equipoId')!;

    // Obtener tipo de fútbol del equipo
    this.equipoService.obtenerEquipo(this.equipoId).subscribe({
      next: (equipo) => {
        this.tipoFutbol = equipo.tipoFutbol || 'FUTBOL_11';
        this.numeroTitulares = this.tipoFutbol === 'FUTBOL_7' ? 7 : 11;
        
        // Cargar jugadores
        this.jugadorService.obtenerJugadoresPorEquipoId(this.equipoId).subscribe({
          next: (jugadores) => {
            this.jugadoresDisponibles = jugadores;
            
            // Ordenar jugadores por posición (de atrás hacia adelante)
            this.jugadoresDisponibles.sort((a, b) => this.obtenerPrioridadPosicion(a) - this.obtenerPrioridadPosicion(b));
            
            this.cargando = false;
          },
          error: (err) => {
            console.error('Error al cargar jugadores:', err);
            this.snackBar.open('Error al cargar jugadores', 'Cerrar', { duration: 3000 });
            this.cargando = false;
          }
        });
      },
      error: (err) => {
        console.error('Error al cargar equipo:', err);
        this.snackBar.open('Error al cargar equipo', 'Cerrar', { duration: 3000 });
        this.cargando = false;
      }
    });
  }

  agregarTitular(jugador: Jugador): void {
    if (this.titulares.length >= this.numeroTitulares) {
      this.snackBar.open(`Solo puedes seleccionar ${this.numeroTitulares} titulares`, 'Cerrar', { duration: 2000 });
      return;
    }
    
    const index = this.jugadoresDisponibles.indexOf(jugador);
    if (index > -1) {
      this.jugadoresDisponibles.splice(index, 1);
      this.titulares.push(jugador);
    }
  }

  quitarTitular(jugador: Jugador): void {
    const index = this.titulares.indexOf(jugador);
    if (index > -1) {
      this.titulares.splice(index, 1);
      this.jugadoresDisponibles.push(jugador);
    }
  }

  agregarSuplente(jugador: Jugador): void {
    const index = this.jugadoresDisponibles.indexOf(jugador);
    if (index > -1) {
      this.jugadoresDisponibles.splice(index, 1);
      this.suplentes.push(jugador);
    }
  }

  quitarSuplente(jugador: Jugador): void {
    const index = this.suplentes.indexOf(jugador);
    if (index > -1) {
      this.suplentes.splice(index, 1);
      this.jugadoresDisponibles.push(jugador);
    }
  }

  confirmarAlineacion(): void {
    if (this.titulares.length !== this.numeroTitulares) {
      this.snackBar.open(`Debes seleccionar exactamente ${this.numeroTitulares} titulares`, 'Cerrar', { duration: 3000 });
      return;
    }

    const titularesIds = this.titulares.map(j => j.id);
    const suplentesIds = this.suplentes.map(j => j.id);

    // Actualizar partido con las listas de titulares y suplentes
    this.partidoService.actualizarAlineacion(this.partidoId, titularesIds, suplentesIds).subscribe({
      next: () => {
        this.snackBar.open('Alineación guardada exitosamente', 'Cerrar', { duration: 2000 });
        this.router.navigate(['/modo-partido', this.equipoId]);
      },
      error: (err) => {
        console.error('Error al guardar alineación:', err);
        this.snackBar.open('Error al guardar alineación', 'Cerrar', { duration: 3000 });
      }
    });
  }

  cancelar(): void {
    if (confirm('¿Deseas cancelar? Se perderá la alineación seleccionada.')) {
      this.router.navigate(['/jugadores']);
    }
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
    if (posicion.includes('CENTRAL') || posicion === 'CEN' || posicion === 'DFC' || posicion === 'DEF' || 
        posicion === 'DCD' || posicion === 'DCI' || posicion === 'CD' || posicion === 'CI') {
      return 2.0;
    }
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
    return 999;
  }
}

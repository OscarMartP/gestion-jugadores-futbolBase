import { Component, OnInit } from '@angular/core';
import { EquipoService } from '../equipo.service'; 
import { Jugador } from '../jugador';
import { Router } from '@angular/router';
import { JugadorService } from '../jugador.service';
import { LoginService } from '../login/services/login.service';
import { Equipo } from '../equipo';
import { FormGroup } from '@angular/forms';

@Component({
  selector: 'app-lista-jugadores',
  templateUrl: './lista-jugadores.component.html',
  styleUrls: ['./lista-jugadores.component.css']
})
export class ListaJugadoresComponent implements OnInit {

  jugadores: Jugador[];
  equipoId: number;
  equipos: Equipo[] = [];
  jugadorForm!: FormGroup;

  constructor(
    private jugadorServicio: JugadorService,
    private equipoService: EquipoService,
    private loginService: LoginService,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.obtenerEquipoIdDelUsuario();
  }

  obtenerEquipoIdDelUsuario() {
    const usuario = this.loginService.getUser();
    if (usuario && usuario.id) {
      this.equipoService.obtenerEquiposPorUsuario(usuario.id).subscribe({
        next: (equipos) => {
          if (equipos?.length > 0) {
            this.equipos = equipos;
  
            this.equipoId = equipos[0].id;
  
            // 🔥 Aquí rellenamos el FormGroup si existe
            this.jugadorForm?.patchValue({
              equipoId: this.equipoId
            });
  
            this.obtenerJugadores(); // Llamar después de tener el ID
          } else {
            console.warn('El usuario no tiene equipos asignados');
            this.router.navigate(['/crear-equipo']);
            alert('No tienes equipos asignados. Por favor crea uno primero.');
          }
        },
        error: (err) => {
          console.error('Error al obtener equipos:', err);
          alert('Error al cargar los equipos');
        }
      });
    } else {
      console.error('Usuario no disponible');
      this.router.navigate(['/login']);
    }
  }
  

  
  private obtenerJugadores(): void {
    console.log('Equipo ID:', this.equipoId);
    if (this.equipoId) {
      this.jugadorServicio.obtenerJugadoresPorEquipoId(this.equipoId).subscribe({
        next: (dato) => {
          this.jugadores = dato;
        },
        error: (err) => {
          console.error('Error al obtener jugadores:', err);
          alert('Error al cargar los jugadores');
        }
      });
    }
  }

  actualizarJugador(id: number) {
    this.router.navigate(['actualizar-jugador', id]);
  }

  eliminarJugador(id: number) {
    this.jugadorServicio.eliminarJugador(id).subscribe({
      next: () => {
        this.obtenerJugadores();
      },
      error: (err) => {
        console.error('Error al eliminar jugador:', err);
        alert('Error al eliminar el jugador');
      }
    });
  }

  verDetallesDelJugador(id: number) {
    this.router.navigate(['jugador-detalles', id]);
  }
}
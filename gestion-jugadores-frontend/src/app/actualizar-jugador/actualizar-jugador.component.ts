import { Component, OnInit } from '@angular/core';
import { Jugador } from '../jugador';
import { ActivatedRoute, Router } from '@angular/router';
import { JugadorService } from '../jugador.service';
import { LoginService } from '../login/services/login.service';
import { EquipoService } from '../equipo.service';
import { Equipo } from '../equipo';



@Component({
  selector: 'app-actualizar-jugador',
  templateUrl: './actualizar-jugador.component.html',
  styleUrl: './actualizar-jugador.component.css'
})
export class ActualizarJugadorComponent implements OnInit {

  id:number;
  jugador:Jugador = new Jugador();

  equipos: Equipo[] = [];
  equipoSeleccionado: number;

  posiciones = ['POR' , 'LD' , 'LI' , 'CEN' , 'MC' , 'MCO' , 'EXD' , 'EXIZ' , 'DC'];
  posicionSeleccionada: string;

  equiposDisponibles: Equipo[];
  selectedEquipoId: number;

  constructor(
    private jugadorService: JugadorService,
    private loginService: LoginService,
    private route: ActivatedRoute,
    private router: Router,
    private equipoService: EquipoService
  ) { }

  // ngOnInit(): void {
  //   this.id = this.route.snapshot.params['id'];
  //   this.jugadorService.obtenerJugadorPorId(this.id).subscribe(dato =>{
  //     this.jugador = dato;
  //   },error => console.log(error));
  // }

  ngOnInit(): void {
    this.id = this.route.snapshot.params['id'];
    this.jugadorService.obtenerJugadorPorId(this.id).subscribe(dato => {
      this.jugador = dato;
      // Inicializar los valores seleccionados con los datos actuales del jugador
      this.posicionSeleccionada = dato.posicion;
      this.equipoSeleccionado = dato.equipoId;
    }, error => console.log(error));

    const usuario = this.loginService.getUser();
    this.equipoService.obtenerEquiposPorUsuario(usuario.id).subscribe(equipos => {
      this.equipos = equipos;
    });
  }

  private obtenerJugador(): void {
    this.jugadorService.obtenerJugadorPorId(this.id).subscribe(
      dato => {
        this.jugador = dato;
        this.selectedEquipoId = this.jugador.equipoId; // Establecer el equipo seleccionado por defecto
      },
      error => {
        console.error('Error al obtener el jugador:', error);
      }
    );
  }

  private obtenerEquiposDisponibles(): void {
    const user = this.loginService.getUser();
    if (user && user.equipos && user.equipos.length > 0) {
      this.equiposDisponibles = user.equipos;
    } else {
      console.error('El usuario no tiene equipos asignados.');
    }
  }

  // onSubmit(){
  //   this.jugador.posicion = this.posicionSeleccionada;
  //   this.jugadorService.actualizarJugador(this.id,this.jugador).subscribe(dato => {
  //     this.irAlaListaDeJugadores();
  //   },error => console.log(error));
  // }

  onSubmit(): void {
    this.jugador.posicion = this.posicionSeleccionada;
    this.jugador.equipoId = Number(this.equipoSeleccionado);
    console.log('Actualizando jugador:', this.jugador);
    this.jugadorService.actualizarJugador(this.id, this.jugador).subscribe(
      data => {
        console.log('Jugador actualizado exitosamente:', data);
        this.irAlaListaDeJugadores();
      },
      error => {
        console.error('Error al actualizar el jugador:', error);
      }
    );
  }

  irAlaListaDeJugadores(){
    this.router.navigate(['/jugadores']);
    //swal('Empleado actualizado',`El empleado ${this.empleado.nombre} ha sido actualizado con exito`,`success`);
  }

}

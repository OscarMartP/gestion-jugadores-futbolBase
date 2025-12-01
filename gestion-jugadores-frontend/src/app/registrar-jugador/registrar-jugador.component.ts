import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { JugadorService } from '../jugador.service';
import { Router } from '@angular/router';
import { LoginService } from '../login/services/login.service';
import { EquipoService } from '../equipo.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Equipo } from '../equipo';

@Component({
  selector: 'app-registrar-jugador',
  templateUrl: './registrar-jugador.component.html',
  styleUrls: ['./registrar-jugador.component.css']
})
export class RegistrarJugadorComponent implements OnInit {

  jugadorForm: FormGroup;
  posiciones = ['POR', 'LD', 'LI', 'CEN', 'MC', 'MCO', 'EXD', 'EXIZ', 'DC'];
  equipos: Equipo[] = [];

  constructor(
    private fb: FormBuilder,
    private jugadorServicio: JugadorService,
    private router: Router,
    private loginService: LoginService,
    private equipoService: EquipoService,
    private snackBar: MatSnackBar
  ) {
    this.jugadorForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(10)]],
      apellido: ['', [Validators.required, Validators.minLength(4), Validators.maxLength(10)]],
      posicion: ['', Validators.required],
      equipoId: [null, Validators.required]
    });
    console.log('Estructura del formulario al iniciar:', this.jugadorForm.value);
  }

  ngOnInit(): void {
    const usuario = this.loginService.getUser();
    if (usuario?.id) {
      this.equipoService.obtenerEquiposPorUsuario(usuario.id).subscribe({
        next: (equipos) => {
          this.equipos = equipos;
          if (equipos.length > 0) {
            this.jugadorForm.patchValue({ equipoId: equipos[0].id });
          }
        },
        error: (err) => {
          console.error('Error al cargar equipos:', err);
          this.snackBar.open('Error al cargar equipos', 'Cerrar', { duration: 5000 });
        }
      });
    }
  }

  guardarJugador(): void {
    if (this.jugadorForm.invalid) return;
  
    const jugadorData = {
      nombre: this.jugadorForm.value.nombre,
      apellido: this.jugadorForm.value.apellido,
      posicion: this.jugadorForm.value.posicion,
      equipo: {
        id: this.jugadorForm.value.equipoId
      }
    };
  
    this.jugadorServicio.registrarJugador(jugadorData).subscribe({
      next: () => this.router.navigate(['/jugadores']),
      error: (error) => console.error('Error al registrar jugador:', error)
    });
  }
  
  

  onSubmit(): void {
    this.guardarJugador();
  }
}

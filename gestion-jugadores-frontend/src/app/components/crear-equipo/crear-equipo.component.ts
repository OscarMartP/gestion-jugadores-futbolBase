import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';

import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';
import { EquipoService } from '../../equipo.service';
import { LoginService } from '../../login/services/login.service';


@Component({
  selector: 'app-crear-equipo',
  templateUrl: './crear-equipo.component.html',
  styleUrls: ['./crear-equipo.component.css']
})
export class CrearEquipoComponent implements OnInit {
  equipoForm: FormGroup;
  currentUserId: number;

  constructor(
    private fb: FormBuilder,
    private equipoService: EquipoService,
    private snackBar: MatSnackBar,
    private router: Router,
    private loginService: LoginService
  ) {
    this.equipoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      duracionPartido: ['', [Validators.required, Validators.min(1), Validators.max(120)]]
    });
  }

  ngOnInit(): void {
    const user = this.loginService.getUser();
    this.currentUserId = user?.id; // o user?.userId según tu backend
  }

  onSubmit(): void {
    if (this.equipoForm.valid && this.currentUserId) {
      this.equipoService.crearEquipo(this.equipoForm.value, this.currentUserId).subscribe({
        next: (response) => {
          this.snackBar.open('Equipo creado exitosamente', 'Cerrar', {
            duration: 3000
          });
          this.router.navigate(['/equipos']);
        },
        error: (err) => {
          console.error('Error al crear equipo:', err);
          this.snackBar.open('Error al crear equipo', 'Cerrar', {
            duration: 5000,
            panelClass: ['error-snackbar']
          });
        }
      });
    }
  }
}
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { PartidoService } from './partido.service';
import { EquipoService } from './equipo.service';
import { LoginService } from './login/services/login.service';
//Logica Crear
@Component({
  selector: 'app-partido-crear',
  templateUrl: './partido-crear.component.html',
})
export class PartidoCrearComponent implements OnInit {
  partidoForm: FormGroup;
  equipos: any[] = [];
  equipoSeleccionadoId: number | null = null;
  usuarioId: number | null = null;

  constructor(
    private fb: FormBuilder,
    private partidoService: PartidoService,
    private equipoService: EquipoService,
    private loginService: LoginService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.partidoForm = this.fb.group({
      equipoId: [null, Validators.required],
      titulo: ['', Validators.required],
      fecha: [new Date().toISOString().slice(0, 16), Validators.required]
    });
  }

  ngOnInit(): void {
    const equipoIdParam = this.route.snapshot.paramMap.get('equipoId');
    if (equipoIdParam) {
      const equipoId = +equipoIdParam;
      console.log("✅ ID del equipo desde la ruta:", equipoId);
    }

    const usuario = this.loginService.getUser();
    if (usuario?.id) {
      this.usuarioId = usuario.id;
      this.equipoService.obtenerEquiposPorUsuario(usuario.id).subscribe({
        next: (data) => {
          this.equipos = data;
          if (data.length > 0) {
            const primerEquipo = data[0];
            this.equipoSeleccionadoId = primerEquipo.id;
            this.partidoForm.patchValue({ equipoId: primerEquipo.id });
          } else {
            alert('No tienes equipos con jugadores asignados');
            this.router.navigate(['/crear-equipo']);
          }
        },
        error: () => alert('Error cargando equipos')
      });
    }
  }

 crearPartido(): void {
  const equipoId = this.partidoForm.value.equipoId;
  console.log("🎯 Form equipoId:", equipoId);
  console.log("📦 Todos los equipos:", this.equipos);

  if (!equipoId || equipoId === 0) {
    alert('Equipo inválido seleccionado');
    return;
  }

  const partido = {
    equipo: { id: equipoId },
    titulo: this.partidoForm.value.titulo,
    fecha: new Date(this.partidoForm.value.fecha),
    duracion: this.equipos.find(eq => eq.id === equipoId)?.duracionPartido || 90
  };

  this.partidoService.crearPartido(partido).subscribe({
    next: (partidoCreado) => {
      console.log("✅ Partido creado:", partidoCreado);
      console.log("📋 Redirigiendo a selección de alineación");
      // Redirigir a pantalla de selección de alineación
      this.router.navigate(['/seleccion-alineacion', partidoCreado.id, equipoId]);
    },
    error: () => alert('Error creando el partido')
  });
}

}

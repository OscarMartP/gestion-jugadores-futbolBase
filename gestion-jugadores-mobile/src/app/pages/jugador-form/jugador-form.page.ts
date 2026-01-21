import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { 
  IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
  IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonItem, 
  IonLabel, IonInput, IonSelect, IonSelectOption, IonTextarea,
  IonNote, IonSpinner, IonButtons, IonBackButton, AlertController
} from '@ionic/angular/standalone';
import { JugadorService } from '../../core/services/jugador.service';
import { EquipoService } from '../../core/services/equipo.service';
import { Jugador } from '../../core/models/jugador';
import { Equipo } from '../../core/models/equipo';

@Component({
  selector: 'app-jugador-form',
  templateUrl: './jugador-form.page.html',
  styleUrls: ['./jugador-form.page.scss'],
  standalone: true,
  imports: [
    IonContent, IonHeader, IonTitle, IonToolbar, IonButton, IonIcon, 
    IonCard, IonCardContent, IonCardHeader, IonCardTitle, IonItem, 
    IonLabel, IonInput, IonSelect, IonSelectOption, IonTextarea,
    IonNote, IonSpinner, IonButtons, IonBackButton,
    CommonModule, ReactiveFormsModule
  ]
})
export class JugadorFormPage implements OnInit {

  jugadorForm!: FormGroup;
  equiposDisponibles: Equipo[] = [];
  esEdicion: boolean = false;
  jugadorId: number | null = null;
  guardando: boolean = false;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private jugadorService: JugadorService,
    private equipoService: EquipoService,
    private alertController: AlertController
  ) {
    this.crearFormulario();
  }

  ngOnInit() {
    this.cargarEquipos();
    this.verificarEdicion();
  }

  crearFormulario() {
    this.jugadorForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      apellido: ['', [Validators.required, Validators.minLength(2), Validators.maxLength(50)]],
      posicion: ['', [Validators.required]],
      equipoId: [null, [Validators.required]]
    });
  }

  cargarEquipos() {
    this.equipoService.obtenerEquiposMe().subscribe({
      next: (equipos) => {
        console.log('✅ Equipos disponibles cargados:', equipos);
        this.equiposDisponibles = equipos;
        
        if (equipos.length === 0) {
          this.alertController.create({
            header: 'Sin equipos',
            message: 'Debes crear un equipo antes de agregar jugadores.',
            buttons: [{
              text: 'Crear Equipo',
              handler: () => {
                this.router.navigate(['/equipo-form/0']);
              }
            }, {
              text: 'Cancelar',
              role: 'cancel',
              handler: () => {
                this.router.navigate(['/tabs/jugadores']);
              }
            }]
          }).then(alert => alert.present());
        }
      },
      error: (error) => {
        console.error('❌ Error cargando equipos:', error);
        this.equiposDisponibles = [];
      }
    });
  }

  verificarEdicion() {
    const id = this.route.snapshot.paramMap.get('id');
    if (id && id !== '0') {
      this.esEdicion = true;
      this.jugadorId = parseInt(id);
      this.cargarJugador();
    }
  }

  cargarJugador() {
    if (this.jugadorId) {
      this.jugadorService.obtenerJugadorPorId(this.jugadorId).subscribe({
        next: (jugador) => {
          this.jugadorForm.patchValue({
            nombre: jugador.nombre,
            apellido: jugador.apellido,
            posicion: jugador.posicion,
            equipoId: jugador.equipoId
          });
        },
        error: (error) => {
          console.error('Error cargando jugador:', error);
          this.alertController.create({
            header: 'Error',
            message: 'No se pudo cargar el jugador.',
            buttons: [{
              text: 'OK',
              handler: () => this.router.navigate(['/tabs/jugadores'])
            }]
          }).then(alert => alert.present());
        }
      });
    }
  }

  async guardarJugador() {
    if (this.jugadorForm.valid) {
      this.guardando = true;
      
      const jugadorData: any = {
        nombre: this.jugadorForm.value.nombre,
        apellido: this.jugadorForm.value.apellido,
        posicion: this.jugadorForm.value.posicion,
        equipoId: parseInt(this.jugadorForm.value.equipoId)
      };

      console.log('📤 Enviando jugador al backend:', jugadorData);

      try {
        let resultado;
        if (this.esEdicion && this.jugadorId) {
          // Actualizar jugador existente
          resultado = await this.jugadorService.actualizarJugador(this.jugadorId, jugadorData).toPromise();
        } else {
          // Crear nuevo jugador
          resultado = await this.jugadorService.crearJugador(jugadorData).toPromise();
        }
        
        console.log('✅ Jugador guardado exitosamente:', resultado);
        
        const alert = await this.alertController.create({
          header: '¡Éxito!',
          message: `Jugador ${this.esEdicion ? 'actualizado' : 'creado'} correctamente`,
          buttons: [{
            text: 'OK',
            handler: () => {
              this.router.navigate(['/tabs/jugadores']);
            }
          }]
        });
        
        await alert.present();
        
      } catch (error) {
        console.error('Error guardando jugador:', error);
        
        const alert = await this.alertController.create({
          header: 'Error',
          message: 'No se pudo guardar el jugador. Inténtalo de nuevo.',
          buttons: ['OK']
        });
        
        await alert.present();
      } finally {
        this.guardando = false;
      }
    }
  }

  async eliminarJugador() {
    const alert = await this.alertController.create({
      header: 'Confirmar eliminación',
      message: '¿Estás seguro de que deseas eliminar este jugador? Esta acción no se puede deshacer.',
      buttons: [
        {
          text: 'Cancelar',
          role: 'cancel'
        },
        {
          text: 'Eliminar',
          role: 'destructive',
          handler: () => {
            console.log('Eliminar jugador:', this.jugadorId);
            this.router.navigate(['/tabs/jugadores']);
          }
        }
      ]
    });

    await alert.present();
  }

  cancelar() {
    this.router.navigate(['/tabs/jugadores']);
  }

}
